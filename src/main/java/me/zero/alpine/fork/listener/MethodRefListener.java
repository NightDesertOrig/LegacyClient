package me.zero.alpine.fork.listener;

import java.util.function.Predicate;

public class MethodRefListener extends Listener {
    private Class target;

    @SafeVarargs
    public MethodRefListener(Class target, EventHook hook, Predicate... filters) {
        super(hook, filters);
        this.target = target;
    }

    @SafeVarargs
    public MethodRefListener(Class target, EventHook hook, int priority, Predicate... filters) {
        super(hook, priority, filters);
        this.target = target;
    }

    public Class getTarget() {
        return this.target;
    }
}
