package musta.belmo.plugins.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

public class DFS {
    public static <T> List<T> toList(T root, Function<T, List<T>> getDirectChildren) {
        List<T> elements = new ArrayList<>();
        Stack<T> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            T child = stack.pop();
            elements.add(child);
            for (T f : getDirectChildren.apply(child)) {
                stack.push(f);
            }
        }
        return elements;
    }
}
