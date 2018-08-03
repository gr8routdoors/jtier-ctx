package com.groupon.jtier.global;

import java.util.Map;
import java.util.WeakHashMap;


public class ThreadGlobal<T> extends InheritableThreadLocal<T> {

    private static InheritableThreadLocal<Map<ThreadGlobal<?>, ?>> globals = new InheritableThreadLocal<Map<ThreadGlobal<?>, ?>>() {
        @Override
        protected Map<ThreadGlobal<?>, ?> initialValue() {
            return new WeakHashMap<>();
        }

        @Override
        protected Map<ThreadGlobal<?>, ?> childValue(Map<ThreadGlobal<?>, ?> parentValue) {
            return new WeakHashMap<>(parentValue);
        }
    };

    @SuppressWarnings("unchecked cast")
    public static <T> ThreadGlobal<T> value(String key, Class<T> valueType) {
        return globals.get().containsKey(key) ? (ThreadGlobal<T>)globals.get().get(key) : new ThreadGlobal<T>();
    }

    public static void clear() {
        Map<ThreadGlobal<?>, ?> newGlobals = new WeakHashMap<>();
        globals.set(newGlobals);
    }

    public static Map<String, ?> dumpValues() {
        return null;
    }

    /**
     * Propagates the current thread's global values to a given runnable.
     *
     * @return   The wrapped runnable with global values attached.
     */
    public static Runnable propagate(Runnable target) {
        Map<String, ?> values = dumpValues();
        return new GlobalPropagatingRunnable (target, values);
    }

    private ThreadGlobal() {
        super();
    }

    @Override
    public void set(T value) {
        super.set(value);

        Map<ThreadGlobal<?>, ?> newGlobals = new WeakHashMap<>(globals.get());
        newGlobals.put(this, null);
        globals.set(newGlobals);
    }
}
