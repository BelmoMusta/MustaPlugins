package musta.belmo.plugins.action.text;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AddNullCheckAction extends AbstractTextAction {
    static String format = "public class TempClass {void dummy() {%s}}";

    public String changeText(String statement) {
        statement = addSemicolumnIfAbsent(statement);
        String formated = String.format(format, statement);

        CompilationUnit compilationUnit = JavaParser.parse(formated);
        Optional<ClassOrInterfaceDeclaration> first = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class);
        Optional<MethodDeclaration> method = first.get().findFirst(MethodDeclaration.class);
        Optional<BlockStmt> body = method.get().getBody();
        Statement expressionStmt = body.get().getStatements().stream().findFirst().get();

        StringBuilder stringBuilder = new StringBuilder();
        expressionStmt.accept(new NullCheckVisitor(), stringBuilder);
        stringBuilder = new StringBuilder(stringBuilder.toString().trim());
       // stringBuilder.insert(stringBuilder.length(), ";");

        return stringBuilder.toString();
    }

    @NotNull
    private String addSemicolumnIfAbsent(String statement) {
        if (!statement.endsWith(";")) {
            statement = statement + ";";
        }
        return statement;
    }

    public static void main(String[] args) {
        String s = "a().getC();";
        AddNullCheckAction addNullCheckAction = new AddNullCheckAction();
        String text = addNullCheckAction.changeText(s);
        System.out.println(text);
    }
}
