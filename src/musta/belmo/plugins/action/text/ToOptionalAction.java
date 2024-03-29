package musta.belmo.plugins.action.text;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Optional;

public class ToOptionalAction extends AbstractTextAction {
    static String format = "public class TempClass {void dummy() {%s}}";

    public String changeText(String statement) {
        if (!statement.endsWith(";")) {
            statement = statement + ";";
        }
        String formated = String.format(format, statement);
        CompilationUnit compilationUnit = JavaParser.parse(formated);
        Optional<ClassOrInterfaceDeclaration> first = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class);
        Optional<MethodDeclaration> method = first.get().findFirst(MethodDeclaration.class);
        Optional<BlockStmt> body = method.get().getBody();
        Statement expressionStmt = body.get().getStatements().stream().findFirst().get();
        StringBuilder stringBuilder = new StringBuilder();
        expressionStmt.accept(new StatementVisitor(), stringBuilder);
        stringBuilder = new StringBuilder(stringBuilder.toString().trim());
        stringBuilder.insert(stringBuilder.length(), ";");

        return stringBuilder.toString();
    }
}
