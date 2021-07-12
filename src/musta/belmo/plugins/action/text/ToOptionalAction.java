package musta.belmo.plugins.action.text;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Optional;

public class ToOptionalAction extends AbstractTextAction {
    static String format = "public class TempClass {\n" +
            "\n" +
            "    private void dummy() {\n" +
            "%s"
            + "    }\n" +
            "}";

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

    public static void main(String[] args) {
        class C{}
        class B{C c;}
        class A{B b;}

        A a= new A();

        String s = null;
        Optional.ofNullable(s)
     .map(mapped -> mapped.getClass())
     .map(mapped -> mapped.getClassLoader())
     .map(mapped -> mapped.getResource("toto"))
     .map(mapped -> mapped.equals(null));

        ToOptionalAction toOptionalAction = new ToOptionalAction();

        System.out.println(toOptionalAction.changeText("false"));

    }
}
