package musta.belmo.plugins.ast;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;

import java.util.Arrays;
import java.util.List;

public class ModifiersHelper {
    public static boolean isPrivate(PsiMethod method) {
        return isWantedModifierFound(method.getModifierList(), "private");
    }

    public static boolean isStatic(PsiMethod method) {
        return isWantedModifierFound(method.getModifierList(), "static");
    }

    public static boolean isStatic(PsiField field) {
        return isWantedModifierFound(field.getModifierList(), "static");
    }
    private static boolean isWantedModifierFound(PsiModifierList modifierList, String wantedModifier) {
        if (modifierList == null) {
            return false;
        }
        List<PsiElement> modifiers = Arrays.asList(modifierList.getChildren());
        boolean ignoreField = false;
        for (PsiElement modifier : modifiers) {
            if (modifier instanceof PsiKeyword keyword) {
                if (keyword.getText().equals(wantedModifier)) {
                    ignoreField = true;
                    break;
                }
            }
        }
        return ignoreField;
    }
}
