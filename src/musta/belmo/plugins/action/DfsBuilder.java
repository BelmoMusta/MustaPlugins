package musta.belmo.plugins.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;

public final class DfsBuilder<T> {
    private T root;
    private Predicate<T> nodePredicate;
    private Predicate<T> leafPredicate;
    private Predicate<T> retainFilter;
    private Function<T, List<T>> directChildrenGetter;
    private boolean includeRoot;
    public DfsBuilder<T> root(T root) {
        this.root = root;
        return this;
    }
    public DfsBuilder<T> nodePredicate(Predicate<T> nodePredicate) {
        this.nodePredicate = nodePredicate;
        return this;
    }
    public DfsBuilder<T> leafPredicate(Predicate<T> leafPredicate) {
        this.leafPredicate = leafPredicate;
        return this;
    }
    public DfsBuilder<T> directChildrenGetter(Function<T, List<T>> directChildrenGetter) {
        this.directChildrenGetter = directChildrenGetter;
        return this;
    }
    public DfsBuilder<T> includeRoot(boolean includeRoot) {
        this.includeRoot = includeRoot;
        return this;
    }
    public DfsBuilder<T> retainFilter(Predicate<T> retainFilter) {
        this.retainFilter = retainFilter;
        return this;
    }

    public List<T> toList() {
        List<T> elements = new ArrayList<>();
        if (includeRoot && !leafPredicate.test(root)) {
            elements.add(root);
        }
        Stack<T> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            T child = stack.pop();
            if (leafPredicate.test(child)) { // if child is a leaf
                elements.add(child);
            } else if (nodePredicate.test(child)) {
                for (T f : directChildrenGetter.apply(child)) {
                    stack.push(f);
                }
            }
        }
        if (retainFilter != null) {
            return elements.stream().filter(retainFilter).toList();
        }
        return elements;
    }
}