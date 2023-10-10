package musta.belmo.plugins.ast;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

public class PsiAnnotationUtils {

    public static void addAnnotation(PsiModifierListOwner parameter, String annotation) {
        PsiModifierList modifierList = parameter.getModifierList();
        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(parameter.getProject()).getElementFactory();
        PsiAnnotation psiAnnotation = elementFactory.createAnnotationFromText(annotation, parameter);
        if (modifierList != null) {
            modifierList.add(psiAnnotation);
        }
        JavaCodeStyleManager.getInstance(parameter.getProject()).shortenClassReferences(psiAnnotation.getParent());
    }
}
