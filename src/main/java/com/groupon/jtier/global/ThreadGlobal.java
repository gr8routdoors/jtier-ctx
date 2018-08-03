package com.groupon.jtier.global;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Provides a bag of global variables for a given thread, with support for dumping values so that they may be exported
 * to other threads without concern of non-deterministic behavior.  See the classes mentioned below if you're looking
 * to export global values across thread boundaries.
 *
 * @see   GlobalPropagatingExecutorService
 * @see   GlobalPropagatingCallable
 * @see   GlobalPropagatingRunnable
 * @param <T>   The value type.
 */
public class ThreadGlobal<T> extends InheritableThreadLocal<T> {

    private static InheritableThreadLocal<Map<String, ThreadGlobal<?>>> globals = new InheritableThreadLocal<Map<String, ThreadGlobal<?>>>() {
        @Override
        protected Map<String, ThreadGlobal<?>> initialValue() {
            return new WeakHashMap<>();
        }

        @Override
        protected Map<String, ThreadGlobal<?>> childValue(Map<String, ThreadGlobal<?>> parentValue) {
            return new WeakHashMap<>(parentValue);
        }
    };

    @SuppressWarnings("unchecked cast")
    public static <T> ThreadGlobal<T> value(String key, Class<T> valueType) {
        return globals.get().containsKey(key) ? (ThreadGlobal<T>)globals.get().get(key) : new ThreadGlobal<T>(key);
    }

    public static void clear() {
        Map<String, ThreadGlobal<?>> newGlobals = new WeakHashMap<>();
        globals.set(newGlobals);
    }

    public static Map<String, ?> dumpValues() {
        Map<String, Object> values = new HashMap<>(globals.get().size());
        for (Map.Entry<String, ThreadGlobal<?>> globalEntry: globals.get().entrySet()) {
            values.put(globalEntry.getKey(), globalEntry.getValue().get());
        }

        return values;
    }

    private String name;

    /**
     * Constructor.
     *
     * @param name   The variable name.
     */
    private ThreadGlobal(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void set(T value) {
        super.set(value);

        Map<String, ThreadGlobal<?>> newGlobals = new WeakHashMap<>(globals.get());
        newGlobals.put(this.getName(), this);
        globals.set(newGlobals);
    }
}
