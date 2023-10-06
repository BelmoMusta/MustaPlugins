package musta.belmo.plugins.ast;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiAssignmentExpression;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionStatement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.refactoring.JavaRefactoringFactory;
import com.intellij.refactoring.SafeDeleteRefactoring;
import com.intellij.usageView.UsageInfo;
import musta.belmo.plugins.action.DFS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class PsiLombokTransformer {
    private final List<String> annotations;
    private PsiDocumentManager documentManager;
    public PsiLombokTransformer(List<String> annotations) {
        this.annotations = annotations;
    }

    public PsiJavaFile transformPsi(PsiJavaFile psiJavaFile, int line) {
        documentManager = PsiDocumentManager.getInstance(psiJavaFile.getProject());

        if (line == -1) {
            return null; // todo
        }

        PsiClass selectedClass = getSelectedClass(line, psiJavaFile);
        if (selectedClass == null) {
            return null;
        }
        deleteGettersAndSetters(selectedClass);
        return psiJavaFile;
    }
    private void deleteGettersAndSetters(PsiClass psiClass) {
        JavaRefactoringFactory javaRefactoringFactory = JavaRefactoringFactory.getInstance(psiClass.getProject());
        SafeDeleteRefactoring safeDeleteMethods = javaRefactoringFactory.createSafeDelete(psiClass.getMethods());
        PsiField[] fields = psiClass.getFields();
        List<PsiField> fieldsToBeLombokified = new ArrayList<>();
        for (PsiField field : fields) {
            boolean ignoreField = ModifiersHelper.isStatic(field);
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
                                && !ModifiersHelper.isStatic(psiMethod)
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
    private PsiClass getSelectedClass(int line, PsiJavaFile psiJavaFile) {
        List<PsiClass> psiClasses = new ArrayList<>();
        for (PsiClass aClass : psiJavaFile.getClasses()) {
            List<PsiClass> currentInnerClasses = DFS.toList(aClass, aCls -> Arrays.asList(aCls.getInnerClasses()));
            psiClasses.addAll(currentInnerClasses);
        }
        for (PsiClass psiClass : psiClasses) {
            Document document = documentManager.getDocument(psiJavaFile);
            int lineNumber = document.getLineNumber(psiClass.getTextOffset());
            System.out.println(lineNumber);
        }
        // todo select class using line where the event is invoked
        return psiClasses.get(0);
    }
}
