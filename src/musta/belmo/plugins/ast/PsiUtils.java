package musta.belmo.plugins.ast;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import musta.belmo.plugins.action.DFS;
import musta.belmo.plugins.action.DfsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PsiUtils {
    public static void addImport(final PsiJavaFile file, final String qualifiedName) {
        final Project project = file.getProject();
        Optional<PsiClass> possibleClass = Optional.ofNullable(JavaPsiFacade.getInstance(project)
                .findClass(qualifiedName, GlobalSearchScope.everythingScope(project)));
        Consumer<PsiClass> psiClassConsumer = psiClass -> {
            JavaCodeStyleManager.getInstance(project).addImport(file, psiClass);
        };
        possibleClass.ifPresent(psiClassConsumer);
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
        DfsBuilder<PsiElement> dfsBuilder = new DfsBuilder<PsiElement>()
                .root(dir)
                .nodePredicate(element -> element instanceof PsiDirectory)
                .leafPredicate(element -> element instanceof PsiJavaFile)
                .directChildrenGetter(psiElement -> Arrays.asList(psiElement.getChildren()));
        return dfsBuilder.toList();
    }

    static void addImports(PsiJavaFile psiJavaFile, List<LombokAnnotation> lombokAnnotations) {
        for (LombokAnnotation lombokAnnotation : lombokAnnotations) {
            addImport(psiJavaFile, lombokAnnotation.getImportName());
        }
    }
}
