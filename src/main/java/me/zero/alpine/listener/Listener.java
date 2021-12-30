package me.zero.alpine.listener;

import net.jodah.typetools.TypeResolver;

import java.util.function.Predicate;

public final class Listener implements EventHook {
    private final Class target;
    private final EventHook hook;
    private final Predicate[] filters;
    private final byte priority;

    @SafeVarargs
    public Listener(EventHook hook, Predicate... filters) {
        this(hook, (byte) 3, filters);
    }

    @SafeVarargs
    public Listener(EventHook hook, byte priority, Predicate... filters) {
        this.hook = hook;
        this.priority = priority;
        this.target = TypeResolver.resolveRawArgument(EventHook.class, hook.getClass());
        this.filters = filters;
    }

    public final Class getTarget() {
        return this.target;
    }

    public final byte getPriority() {
        return this.priority;
    }

    public final void invoke(Object event) {
        if (this.filters.length > 0) {
            Predicate[] var2 = this.filters;
            int var3 = var2.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                Predicate filter = var2[var4];
                if (!filter.test(event)) {
                    return;
                }
            }
        }

        this.hook.invoke(event);
    }
}
