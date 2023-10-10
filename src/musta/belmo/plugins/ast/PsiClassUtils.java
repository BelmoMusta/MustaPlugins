package musta.belmo.plugins.ast;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PsiClassUtils {
    public static void addLombokAnnotations(PsiClass psiClass, List<LombokAnnotation> lombokAnnotations) {
        for (LombokAnnotation lombokAnnotation : lombokAnnotations) {
            PsiModifierList modifierList = psiClass.getModifierList();
            if (modifierList != null) {
                boolean addAnnotation = true;
                for (PsiAnnotation annotation : modifierList.getAnnotations()) {
                    if (lombokAnnotation.getImportName().equals(annotation.getQualifiedName())) {
                        addAnnotation = false;
                        break;
                    }
                }
                if (addAnnotation) {
                    modifierList.addAnnotation(lombokAnnotation.toString());
                }
            }
        }
    }
    public static void addAnnotations(List<LombokAnnotation> lombokAnnotations,
                                      List<PsiClass> classes) {
        for (PsiClass psiClass : classes) {
            addLombokAnnotations(psiClass, lombokAnnotations);
        }
    }
    public static void deleteMethods(List<PsiClass> classes, List<LombokAnnotation> annotations) {
        List<String> methodPrefixesToDelete = annotations.stream()
                .flatMap(annotation -> annotation.getMethodPrefixes().stream())
                .collect(Collectors.toList());
        List<Predicate<PsiMethod>> methodPredicates = new ArrayList<>();
        for (String prefix : methodPrefixesToDelete) {
            Predicate<PsiMethod> predicate = psiMethod -> psiMethod.getName().startsWith(prefix);
            methodPredicates.add(predicate);
        }
        for (Predicate<PsiMethod> methodPredicate : methodPredicates) {
            PsiMethodUtils.deleteMethods(classes, methodPredicate);
        }
    }
    public static void deleteConstructors(List<PsiClass> classes, List<LombokAnnotation> annotations) {
        boolean noArgsConstructorPresent = isNoArgsConstructorPresent(annotations);
        for (PsiClass aClass : classes) {
            PsiMethod[] constructors = aClass.getConstructors();
            if (noArgsConstructorPresent) {
                for (PsiMethod constructor : constructors) {
                    if (!constructor.hasParameters()) {
                        PsiMethodUtils.deleteConstructor(constructor);
                    }
                }
            }
            int fieldsCount = aClass.getFields().length;
            for (PsiMethod constructor : constructors) {
                int paramsCount = constructor.getParameterList().getParameters().length;
                if (fieldsCount == paramsCount) {
                    PsiMethodUtils.deleteConstructor(constructor);
                }
            }
        }
    }
    private static boolean isNoArgsConstructorPresent(List<LombokAnnotation> annotations) {
        return annotations.stream().anyMatch(annotation -> annotation.toString().equals("NoArgsConstructor"));
    }
}
