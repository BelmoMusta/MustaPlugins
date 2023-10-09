package musta.belmo.plugins.action;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class DFS {

    public static <T> List<T> toList(T root,
                                     Predicate<T> nodePredicate,
                                     Predicate<T> leafPredicate,
                                     Function<T, List<T>> getDirectChildren, boolean includeRoot) {
        return new DfsBuilder<T>()
                .root(root)
                .nodePredicate(nodePredicate)
                .leafPredicate(leafPredicate)
                .directChildrenGetter(getDirectChildren)
                .includeRoot(includeRoot)
                .toList();
        /*List<T> elements = new ArrayList<>();
        Stack<T> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            T child = stack.pop();
            if (filter.test(child)) {
                elements.add(child);
            }
            for (T f : getDirectChildren.apply(child)) {
                stack.push(f);
            }
        }
        return elements;*/
    }
}
