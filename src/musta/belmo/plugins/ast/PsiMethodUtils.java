package musta.belmo.plugins.ast;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.refactoring.JavaRefactoringFactory;
import com.intellij.refactoring.SafeDeleteRefactoring;
import com.intellij.usageView.UsageInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public final class PsiMethodUtils {
    public static boolean isAssociatedWithAField(PsiMethod method, List<PsiField> fieldsToBeLombokified) {

        for (PsiField psiField : fieldsToBeLombokified) {
            if (isAssociatedWithField(method, psiField)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isAssociatedWithField(PsiMethod method, PsiField psiField) {
        String methodName = method.getName();
        String capitalizedFieldName = CodeUtils.capitalize(psiField.getName());
        int subStringIndex = 0;
        if (isASetter(method) || isAGetter(method)) {
            subStringIndex = 3;
        } else if (isABooleanGetter(method)) {
            subStringIndex = 2;
        }
        return capitalizedFieldName.equals(methodName.substring(subStringIndex));
    }
    private static boolean isASetter(PsiMethod method) {
        String methodName = method.getName();
        boolean isVoid = isVoid(method);
        boolean isPublic = isPublic(method);
        return isVoid && isPublic && methodName.startsWith("set");
    }
    private static boolean isAGetter(PsiMethod method) {
        String methodName = method.getName();
        boolean isVoid = isVoid(method);
        boolean isPublic = isPublic(method);
        return !isVoid && isPublic && methodName.startsWith("get");
    }
    private static boolean isABooleanGetter(PsiMethod method) {
        String methodName = method.getName();
        boolean isVoid = isVoid(method);
        boolean isPublic = isPublic(method);
        return !isVoid && isPublic && methodName.startsWith("is");
    }
    public static boolean isStatic(PsiField field) {
        return isWantedModifierFound(field.getModifierList(), PsiModifier.STATIC);
    }
    public static boolean isStatic(PsiMethod method) {
        return isWantedModifierFound(method.getModifierList(), PsiModifier.STATIC);
    }
    static boolean isWantedModifierFound(PsiModifierList modifierList, String wantedModifier) {
        return modifierList != null && modifierList.hasModifierProperty(wantedModifier);
    }

    public static boolean isVoid(PsiMethod method) {
        return method.getReturnType() != null && method.getReturnType().equalsToText("void");
    }

    public static boolean isPublic(PsiMethod method) {
        return isWantedModifierFound(method.getModifierList(), PsiModifier.PUBLIC);
    }

    public static void deleteMethods(List<PsiClass> psiClasses, Predicate<PsiMethod> methodPredicate) {
        for (PsiClass psiClass : psiClasses) {
            JavaRefactoringFactory javaRefactoringFactory = JavaRefactoringFactory.getInstance(psiClass.getProject());
            PsiField[] fields = psiClass.getFields();
            List<PsiField> fieldsToBeLombokified = new ArrayList<>();
            for (PsiField field : fields) {
                boolean ignoreField = isStatic(field);
                if (!ignoreField) {
                    fieldsToBeLombokified.add(field);
                }
            }

            Predicate<PsiMethod> isValidMethodPredicate = psiMethod ->
                    psiMethod.getBody() != null
                            && !isStatic(psiMethod)
                            && isAssociatedWithAField(psiMethod, fieldsToBeLombokified);

            List<PsiMethod> methodsToBeRemoved = Arrays.stream(psiClass.getMethods())
                    .filter(methodPredicate.and(isValidMethodPredicate))
                    .toList();

            PsiElement[] methods = new PsiElement[methodsToBeRemoved.size()];
            for (int i = 0; i < methodsToBeRemoved.size(); i++) {
                methods[i] = methodsToBeRemoved.get(i);
            }
            SafeDeleteRefactoring safeDelete = javaRefactoringFactory.createSafeDelete(methods);
            safeDelete.doRefactoring(new UsageInfo[]{});
        }
    }
}
