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
