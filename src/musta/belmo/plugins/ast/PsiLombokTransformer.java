package musta.belmo.plugins.ast;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReturnStatement;
import com.intellij.psi.PsiStatement;
import com.intellij.refactoring.JavaRefactoringFactory;
import com.intellij.refactoring.SafeDeleteRefactoring;
import musta.belmo.plugins.action.DFS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates Fields from gettes
 *
 * @author default author
 * @version 0.0.0
 * @since 0.0.0.SNAPSHOT
 */
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
//                    SafeDeleteRefactoring safeDelete = javaRefactoringFactory.createSafeDelete(new PsiElement[]{field});
//                    UsageInfo[] usages = safeDelete.findUsages();
//                    field.getReferences();
            //    safeDeleteMethods.doRefactoring(usages);
            }

        }
        List<PsiMethod> methodsToBeRemoved = new ArrayList<>();
        List<PsiMethod> methods = Arrays.asList(psiClass.getMethods())
                .stream().filter(psiMethod ->
                        psiMethod.getBody() != null
                   && !ModifiersHelper.isStatic(psiMethod)
                ).collect(Collectors.toList());

        for (PsiMethod method : methods) {
            PsiStatement[] statements = method.getBody().getStatements();
            for (PsiStatement statement : statements) {
                if (statement instanceof PsiReturnStatement returnStatement){
                    PsiExpression returnValue = returnStatement.getReturnValue();
                    if (returnValue instanceof PsiReferenceExpression referenceExpression) {
                        System.out.println(referenceExpression);
                        referenceExpression.getReference().getElement();
                    }
                }
            }
            System.out.println(method);
        }

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
