/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
