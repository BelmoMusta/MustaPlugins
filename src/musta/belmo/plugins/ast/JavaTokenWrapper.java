package musta.belmo.plugins.ast;

import com.github.javaparser.JavaToken;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JavaTokenWrapper {
    final JavaToken javaToken;
    boolean deleted = false;
    public JavaTokenWrapper(JavaToken javaToken) {this.javaToken = javaToken;}
    @NotNull
    static List<JavaTokenWrapper> getJavaTokens(CompilationUnit compilationUnitSrc) {
        List<JavaTokenWrapper> tokens = new ArrayList<>();
        TokenRange javaTokens = compilationUnitSrc.getTokenRange().get();
        JavaToken currentToken = javaTokens.getBegin();
        while (currentToken != null) {
            tokens.add(new JavaTokenWrapper(currentToken));
            System.out.print(currentToken.getText());
            currentToken = currentToken.getNextToken().orElse(null);
        }
        return tokens;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public boolean isDeleted() {
        return deleted;
    }
}
