package musta.belmo.plugins.ast;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.TextEditorLocation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiKeyword;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import musta.belmo.plugins.action.DFS;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PsiUtils {
    public static void addImport(final PsiJavaFile file, final String qualifiedName) {
        final Project project = file.getProject();
        Optional<PsiClass> possibleClass = Optional.ofNullable(JavaPsiFacade.getInstance(project)
                .findClass(qualifiedName, GlobalSearchScope.everythingScope(project)));
        possibleClass.ifPresent(psiClass -> JavaCodeStyleManager.getInstance(project).addImport(file, psiClass));
    }
    @NotNull
    static List<PsiClass> getAllClassesInFile(PsiJavaFile psiJavaFile) {
        List<PsiClass> psiClasses = new ArrayList<>();
        for (PsiClass aClass : psiJavaFile.getClasses()) {
            List<PsiClass> currentInnerClasses = DFS.toList(aClass, //root
                    psiClass -> psiClass.getInnerClasses().length != 0, // isNode
                    psiClass -> psiClass.getInnerClasses().length == 0, // isLeaf
                    aCls -> Arrays.asList(aCls.getInnerClasses()),
                    true);
            psiClasses.addAll(currentInnerClasses.stream()
                    .filter(
                            cls -> !cls.isInterface()
                            && !cls.isEnum()
                    )
                    .collect(Collectors.toList()));
        }
        return psiClasses;
    }
    public static List<PsiElement> getAllJavaFiles(PsiElement dir) {
        return DFS.toList(dir,
                element -> element instanceof PsiDirectory,
                element -> element instanceof PsiJavaFile,
                psiElement -> Arrays.asList(psiElement.getChildren()), false);
    }
    public static int getSelectedLine(@NotNull AnActionEvent event) {
        FileEditor fileEditor = event.getData(PlatformDataKeys.FILE_EDITOR);
        final int line;
        if (fileEditor != null) {
            FileEditorLocation currentLocation = fileEditor.getCurrentLocation();
            if (currentLocation instanceof TextEditorLocation textEditorLocation){
                line = textEditorLocation.getPosition().line;
            } else {
                line = -1;
            }

        } else {
            line = -1;
        }
        return line;
    }
    public static boolean isStatic(PsiField field) {
        return isWantedModifierFound(field.getModifierList(), "static");
    }
    public static boolean isStatic(PsiMethod method) {
        return isWantedModifierFound(method.getModifierList(), "static");
    }
    static boolean isWantedModifierFound(PsiModifierList modifierList, String wantedModifier) {
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
    public static boolean isPrivate(PsiMethod method) {
        return isWantedModifierFound(method.getModifierList(), "private");
    }
}
