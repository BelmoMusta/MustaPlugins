package musta.belmo.plugins.ast;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.impl.PsiElementFactoryImpl;
import com.intellij.psi.impl.PsiParserFacadeImpl;
import com.intellij.refactoring.JavaRefactoringFactory;
import com.intellij.refactoring.SafeDeleteRefactoring;
import com.intellij.usageView.UsageInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class PsiLombokTransformer implements Transformer {
    private final List<LombokAnnotation> annotations;
    private PsiDocumentManager documentManager;
    private PsiElementFactoryImpl psiElementFactory;
    private PsiParserFacade psiJavaParserFacade;
    private int line;

    public PsiLombokTransformer(List<String> annotations) {
        this.annotations =
                annotations.stream().map(LombokAnnotation::new).collect(Collectors.toList());
    }


    public void transformPsi(PsiElement psiElement) {
        if (annotations.isEmpty()) {
            return;
        }

        psiElementFactory = new PsiElementFactoryImpl(psiElement.getProject());
        documentManager = PsiDocumentManager.getInstance(psiElement.getProject());
        psiJavaParserFacade = new PsiParserFacadeImpl(psiElement.getProject());
        List<PsiClass> classes = new ArrayList<>();

        if (psiElement instanceof PsiJavaFile psiJavaFile) {
            List<PsiClass> allClassesInFile = PsiUtils.getAllClassesInFile(psiJavaFile);
            classes.addAll(allClassesInFile);
            deleteGettersAndSetters(classes);
            addAnnotations(annotations, classes, psiJavaFile);
        }
    }
    @Override
    public String getActionName() {
        return "Lombokify";
    }
    private void addAnnotations(List<LombokAnnotation> lombokAnnotations,
                                List<PsiClass> psiClasses,
                                PsiJavaFile psiJavaFile) {
        for (PsiClass psiClass : psiClasses) {
            for (LombokAnnotation lombokAnnotation : lombokAnnotations) {
                PsiElement psiWhiteSpace = psiJavaParserFacade.createWhiteSpaceFromText("\n");
                PsiAnnotation entityAnnotation = psiElementFactory
                        .createAnnotationFromText(lombokAnnotation.getAnnotation(), null);
                entityAnnotation.add(psiWhiteSpace);
                psiJavaFile.addBefore(entityAnnotation, psiClass);
                PsiUtils.addImport(psiJavaFile, lombokAnnotation.getImportName());
            }
        }

    }
    private void deleteGettersAndSetters(List<PsiClass> psiClasses) {
        for (PsiClass psiClass : psiClasses) {
            JavaRefactoringFactory javaRefactoringFactory = JavaRefactoringFactory.getInstance(psiClass.getProject());
            PsiField[] fields = psiClass.getFields();
            List<PsiField> fieldsToBeLombokified = new ArrayList<>();
            for (PsiField field : fields) {
                boolean ignoreField = PsiUtils.isStatic(field);
                if (!ignoreField) {
                    fieldsToBeLombokified.add(field);
                }
            }

            List<PsiMethod> methodsToBeRemoved = Arrays.asList(psiClass.getMethods())
                    .stream().filter(psiMethod ->
                            (psiMethod.getName().startsWith("set")
                                    || psiMethod.getName().startsWith("get")
                                    || psiMethod.getName().startsWith("is"))
                                    && psiMethod.getBody() != null
                                    && !PsiUtils.isStatic(psiMethod)
                                    && isAssociatedWithAField(psiMethod, fieldsToBeLombokified)
                    ).collect(Collectors.toList());

            PsiElement[] methods = new PsiElement[methodsToBeRemoved.size()];
            for (int i = 0; i < methodsToBeRemoved.size(); i++) {
                methods[i] = methodsToBeRemoved.get(i);
            }
            SafeDeleteRefactoring safeDelete = javaRefactoringFactory.createSafeDelete(methods);
            UsageInfo[] usages = safeDelete.findUsages();
            safeDelete.doRefactoring(usages);
        }
    }
    private static boolean isAssociatedWithAField(PsiMethod method, List<PsiField> fieldsToBeLombokified) {
        boolean isMethodAssociatedWithAField = false;
        PsiStatement[] statements = method.getBody().getStatements();
        for (PsiStatement statement : statements) {
            if (statement instanceof PsiReturnStatement returnStatement) { // getter
                PsiExpression returnValue = returnStatement.getReturnValue();
                if (returnValue instanceof PsiReferenceExpression referenceExpression) {
                    for (PsiField psiField : fieldsToBeLombokified) {
                        if (psiField.getName().equals(referenceExpression.getText())) {
                            isMethodAssociatedWithAField = true;
                            break;
                        }
                    }
                }
            } else if (statement instanceof PsiExpressionStatement psiExpression) {
                for (PsiElement child : psiExpression.getChildren()) {
                    if (child instanceof PsiAssignmentExpression assignmentExpression) {
                        PsiExpression lExpression = assignmentExpression.getLExpression();
                        for (PsiField psiField : fieldsToBeLombokified) {
                            if (psiField.getName().equals(lExpression.getText())
                                    || ("this." + psiField.getName()).equals(lExpression.getText())) {
                                isMethodAssociatedWithAField = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return isMethodAssociatedWithAField;
    }
    private PsiClass getSelectedClass(PsiJavaFile psiJavaFile) {
        final List<PsiClass> psiClasses = PsiUtils.getAllClassesInFile(psiJavaFile);
        for (PsiClass psiClass : psiClasses) {
            Document document = documentManager.getDocument(psiJavaFile);
            int lineNumber = document.getLineNumber(psiClass.getTextOffset());
            System.out.println(lineNumber);
        }
        // todo select class using line where the event is invoked
        return psiClasses.get(0);
    }

}
