package musta.belmo.plugins.ast;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.PsiElementFactoryImpl;
import org.jetbrains.annotations.NotNull;

public class RestWSCreatorTransformer implements Transformer {

    private final WsSignature wsSignature;
    private final int offset;

    public RestWSCreatorTransformer(String wsSignatureString, int line) {
        this.offset = line;
        this.wsSignature = new WsSignature(wsSignatureString.trim());
    }
    public RestWSCreatorTransformer(WsSignature wsSignature, int line) {
        this.offset = line;
        this.wsSignature = wsSignature;
    }
    @Override
    public void transformPsi(PsiElement psiElement) {

        if (psiElement instanceof PsiJavaFile psiJavaFile) {
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(psiJavaFile.getProject());
            Document document = documentManager.getDocument(psiJavaFile);
            if (document == null) {
                return;
            }
            PsiElement psiClassElement = null;
            if (psiJavaFile.getClasses().length > 0) {
                psiClassElement = psiJavaFile.getClasses()[0];
            }
            if (psiClassElement instanceof PsiClass psiClass) {
                PsiMethod methodFromText = createMethod(psiClass);
                document.insertString(offset, methodFromText.getText());
                ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(psiJavaFile.getProject(),
                        psiJavaFile,
                        methodFromText.getTextRange(),
                        true);
                reformatCodeProcessor.run();
                documentManager.commitDocument(document);
                for (String anImport : wsSignature.getImports()) {
                    PsiUtils.addImport(psiJavaFile, anImport);
                }
                JavaCodeStyleManager javaCodeStyleManager = JavaCodeStyleManager.getInstance(psiClass.getProject());
                javaCodeStyleManager.shortenClassReferences(psiJavaFile);
            }
        }
    }
    private PsiMethod createMethod(PsiClass psiClass) {
        PsiElementFactoryImpl psiElementFactory = new PsiElementFactoryImpl(psiClass.getProject());
        PsiType type = psiElementFactory.createTypeFromText(wsSignature.getReturnType(), psiClass);
        PsiMethod method = psiElementFactory.createMethod(wsSignature.getPossibleMethodName(), type);
        for (WsParam wsParam : wsSignature.getWsParams()) {
            PsiType paramType = psiElementFactory.createTypeFromText(wsParam.getType(), psiClass);
            PsiParameter parameter = psiElementFactory.createParameter(wsParam.getName(), paramType);
            PsiAnnotationUtils.addAnnotation(parameter, wsParam.getAnnotation());
            method.getParameterList().add(parameter);
        }
        PsiAnnotation annotation = psiElementFactory.createAnnotationFromText(
                wsSignature.getAnnotation(),
                psiClass);
        PsiModifierList modifierList = method.getModifierList();
        PsiElement firstChild = modifierList.getFirstChild();
        modifierList.addBefore(annotation, firstChild);
        PsiCodeBlock body = method.getBody();
        if (body != null) {
            PsiComment comment = psiElementFactory.createCommentFromText(wsSignature.getMethodComment(), psiClass);
            PsiComment generatedComment =
                    psiElementFactory.createCommentFromText("/*\n* Generated from '" + wsSignature.getOriginalRawUrl() + "'\n*/",
                            annotation);
            method.addBefore(generatedComment, method.getFirstChild());
            body.add(comment);
            PsiStatement statement = getPsiStatement(psiClass, psiElementFactory);
            body.add(statement);
        }
        CodeStyleManager.getInstance(psiClass.getProject())
                .reformat(method.getParent());
        return method;
    }
    @NotNull
    private PsiStatement getPsiStatement(PsiClass psiClass,
                                         PsiElementFactoryImpl psiElementFactory) {
        return psiElementFactory.createStatementFromText(wsSignature.getMethodBody(), psiClass);
    }

    @Override
    public String getActionName() {
        return "REST WS Creator";
    }
    @Override
    public boolean isApplied() {
        return wsSignature.isValid();
    }
}
