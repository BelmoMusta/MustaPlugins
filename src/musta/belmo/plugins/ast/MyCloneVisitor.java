package musta.belmo.plugins.ast;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.CloneVisitor;

public class MyCloneVisitor extends CloneVisitor {

    @Override
    protected <T extends Node> T cloneNode(T node, Object arg) {
        if (node == null) {
            return null;
        }
        Node r = (Node) node.accept(this, arg);
        if (r == null) {
            return null;
        }
        r.setComment(node.getComment().orElse(null));
        for (Comment orphanComment : node.getOrphanComments()) {
            r.addOrphanComment(orphanComment);
        }
        return (T) r;
    }
}
