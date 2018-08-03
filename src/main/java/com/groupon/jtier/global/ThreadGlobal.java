package com.groupon.jtier.global;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Provides a bag of global variables for a given thread, with support for dumping values so that they may be exported
 * to other threads without concern of non-deterministic behavior.  See the classes mentioned below if you're looking
 * to export global values across thread boundaries.
 *
 * The intention of the class is for passing values across library boundaries where a formal interface cannot be
 * established (for whatever reason).  Typically, this is an anti-pattern so use sparingly!
 *
 * Example code:
 * <pre><code>
 * ThreadGlobal fooGlobal = ThreadGlobal.value("foo", String.class);
 * fooGlobal.set("howdy");
 * // OR
 * ThreadGlobal.value("bar", Integer.class).set(1);
 *
 * assert fooGlobal.get() == "howdy"
 * assert ThreadGlobal.value("foo", String.class) == fooGlobal
 * assert ThreadGlobal.value("bar", Integer.class).get() == 1
 * </code></pre>
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

    private String name;

    /**
     * Get a reference to a global variable that may or may not have a value set.
     *
     * @param key       The name of the variable.
     * @param valueType The type of value it contains.
     * @param <T>       The value type.
     * @return          A container for getting or setting the variable's value.
     */
    @SuppressWarnings("unchecked cast")
    public static <T> ThreadGlobal<T> value(String key, Class<T> valueType) {
        return globals.get().containsKey(key) ? (ThreadGlobal<T>)globals.get().get(key) : new ThreadGlobal<T>(key);
    }

    /** Remove all global variables. */
    public static void clear() {
        Map<String, ThreadGlobal<?>> newGlobals = new WeakHashMap<>();
        globals.set(newGlobals);
    }

    /**
     * Get the number of variables set.
     *
     * @return   The size.
     */
    public static int size() {
        return globals.get().size();
    }

    /**
     * Dump the global variables to a structure that can be shared across threads.
     *
     * @return   The variable values, hashed by name.
     */
    public static Map<String, ?> dumpValues() {
        Map<String, Object> values = new HashMap<>(globals.get().size());
        for (Map.Entry<String, ThreadGlobal<?>> globalEntry: globals.get().entrySet()) {
            values.put(globalEntry.getKey(), globalEntry.getValue().get());
        }

        return values;
    }

    /**
     * Constructor.
     *
     * @param name   The variable name.
     */
    private ThreadGlobal(String name) {
        super();
        this.name = name;
    }

    /**
     * Get the name of this variable.
     *
     * @return    The name.
     */
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
