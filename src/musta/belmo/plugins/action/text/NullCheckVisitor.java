package musta.belmo.plugins.action.text;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class NullCheckVisitor extends VoidVisitorAdapter<StringBuilder> {

    @Override
    public void visit(MethodCallExpr n, StringBuilder arg) {

        String params = n.getArguments().stream()
                .map(Node::toString)
                .collect(Collectors.joining(", "));
        String nameAsString = n.getNameAsString() + "(" + params + ")";
        nameAsString = wrapWithIfNotNull(nameAsString);
        arg.insert(0, nameAsString).insert(0, " ");
        super.visit(n, arg);
    }

    @Override
    public void visit(NameExpr n, StringBuilder arg) {
        arg.insert(0, wrapWithIfNotNull(n) + "{\n    ");
        super.visit(n, arg);
        arg.append("\n}");
    }

    @NotNull
    private String wrapWithIfNotNull(Object n) {
        return "if(" + n + " != null) ";
    }

    @Override
    public void visit(FieldAccessExpr n, StringBuilder arg) {

        String nameAsString = wrapWithIfNotNull(n) + "{\n";
        arg.insert(0, nameAsString).insert(0, " ");
        super.visit(n, arg);
    }

    public void visit(StringLiteralExpr n, StringBuilder arg) {
        visitLiteral(n, arg);
        super.visit(n, arg);
    }

    public void visit(BooleanLiteralExpr n, StringBuilder arg) {
        visitLiteral(n, arg);
        super.visit(n, arg);
    }

    public void visit(CharLiteralExpr n, StringBuilder arg) {
        visitLiteral(n, arg);
        super.visit(n, arg);
    }

    public void visit(DoubleLiteralExpr n, StringBuilder arg) {
        visitLiteral(n, arg);
        super.visit(n, arg);
    }

    public void visit(IntegerLiteralExpr n, StringBuilder arg) {
        visitLiteral(n, arg);
        super.visit(n, arg);
    }

    public void visit(LongLiteralExpr n, StringBuilder arg) {
        visitLiteral(n, arg);
        super.visit(n, arg);
    }

    private void visitLiteral(LiteralExpr n, StringBuilder arg) {
        String nameAsString = wrapWithIfNotNull(n) + "{\n    ";

        arg.insert(0, nameAsString);
    }
}
