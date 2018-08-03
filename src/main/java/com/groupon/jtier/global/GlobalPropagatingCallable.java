package com.groupon.jtier.global;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Wraps a delegate {@link Callable} to provide a set of {@link ThreadGlobal} values.
 *
 * @param <V>  The type of value to be returned by {@link #call()}.
 */
public class GlobalPropagatingCallable<V> implements Callable<V> {

    private Callable<V>       target;
    private Map<String, ?> globalValues;

    /**
     * Constructor.
     *
     * @param target        The delegate callable.
     * @param globalValues  The values to put into the callable's {@link ThreadGlobal}.
     */
    public GlobalPropagatingCallable(Callable<V> target, Map<String, ?> globalValues) {
        this.target = target;
        this.globalValues = globalValues;
    }

    @Override
    public V call() throws Exception {
        for (Map.Entry<String, ?> entry: globalValues.entrySet()) {
            ThreadGlobal.value(entry.getKey(), Object.class).set(entry.getValue());
        }
        return target.call();
    }
}
