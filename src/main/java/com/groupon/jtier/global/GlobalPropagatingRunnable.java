package com.groupon.jtier.global;

import java.util.Map;

/**
 * Wraps a delegate {@link Runnable} to provide a set of {@link ThreadGlobal} values.
 */
public class GlobalPropagatingRunnable implements Runnable {

    private Runnable       target;
    private Map<String, ?> globalValues;

    /**
     * Constructor.
     *
     * @param target        The delegate runnable.
     * @param globalValues  The values to put into the runnable's {@link ThreadGlobal}.
     */
    public GlobalPropagatingRunnable(Runnable target, Map<String, ?> globalValues) {
        this.target = target;
        this.globalValues = globalValues;
    }

    @Override
    public void run() {
        for (Map.Entry<String, ?> entry: globalValues.entrySet()) {
            ThreadGlobal.value(entry.getKey(), Object.class).set(entry.getValue());
        }
        target.run();
    }
}
