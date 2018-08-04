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
package com.groupon.jtier;

import com.groupon.jtier.global.ThreadGlobal;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreadGlobalTest {
    @Test
    public void testSupportsMultipleTypesOfVariablesToBeSet() {
        ThreadGlobal.clear();
        ThreadGlobal<String> fooGlobal = ThreadGlobal.value("foo", String.class);
        fooGlobal.set("howdy");

        ThreadGlobal<Integer> barGlobal = ThreadGlobal.value("bar", Integer.class);
        barGlobal.set(1);

        assertThat(fooGlobal.get()).isEqualTo("howdy");
        assertThat(barGlobal.get()).isEqualTo(1);
    }

    @Test
    public void testSupportsNullValues() {
        ThreadGlobal.clear();
        ThreadGlobal<String> fooGlobal = ThreadGlobal.value("foo", String.class);
        fooGlobal.set(null);

        assertThat(fooGlobal.get()).isNull();
    }

    @Test
    public void testContains() {
        ThreadGlobal.clear();
        ThreadGlobal.value("foo", String.class).set(null);
        ThreadGlobal.value("baz", Integer.class).set(2);

        assertThat(ThreadGlobal.contains("foo")).isTrue();
        assertThat(ThreadGlobal.contains("baz")).isTrue();
        assertThat(ThreadGlobal.contains("bar")).isFalse();
    }

    @Test
    public void testSizeWithRepeatedElement() {
        ThreadGlobal.clear();
        ThreadGlobal.value("foo", String.class).set(null);
        ThreadGlobal.value("bar", String.class).set("hi");
        ThreadGlobal.value("foo", String.class).set("bye");
        ThreadGlobal.value("baz", Integer.class).set(2);

        assertThat(ThreadGlobal.size()).isEqualTo(3);
    }

    @Test
    public void testEmptySize() {
        ThreadGlobal.clear();

        assertThat(ThreadGlobal.size()).isEqualTo(0);
    }

    @Test
    public void testClear() {
        ThreadGlobal.clear();
        ThreadGlobal.value("bar", String.class).set("hi");
        ThreadGlobal.clear();

        assertThat(ThreadGlobal.size()).isEqualTo(0);
    }

    @Test
    public void testValueIsConsistentBetweenCallsForTheSameKey() {
        ThreadGlobal.clear();
        ThreadGlobal<String> fooGlobal = ThreadGlobal.value("foo", String.class);
        fooGlobal.set("howdy");

        assertThat(fooGlobal.get()).isEqualTo("howdy");
        assertThat(ThreadGlobal.value("foo", String.class).get()).isEqualTo("howdy");
    }

    @Test
    public void testValueDoesntCreateWhenRetreiving() {
        ThreadGlobal.clear();

        assertThat(ThreadGlobal.value("foo", String.class).get()).isNull();
        assertThat(ThreadGlobal.value("foo", String.class).get()).isNull();
    }

    @Test
    public void testValuesCanBeUpdatedWithinTheSameThread() {
        ThreadGlobal.clear();
        ThreadGlobal<String> global = ThreadGlobal.value("bar", String.class);
        global.set("hi");

        assertThat(global.get()).isEqualTo("hi");

        global.set("bye");
        assertThat(global.get()).isEqualTo("bye");
        assertThat(ThreadGlobal.value("bar", String.class).get()).isEqualTo("bye");
    }

    @Test
    public void testChildThreadsCanSeeValues() throws Exception {
        ThreadGlobal.clear();
        ThreadGlobal.value("foo", String.class).set("aw yeah");

        Callable<String> fooValueGetter = () -> ThreadGlobal.value("foo", String.class).get();

        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> future = es.submit(fooValueGetter);

        assertThat(future.get()).isEqualTo("aw yeah");
    }

    @Test
    public void testDumpValuesIsAccurate() {
        ThreadGlobal.clear();
        ThreadGlobal.value("foo", String.class).set("bye");
        ThreadGlobal.value("bar", String.class).set("hi");
        ThreadGlobal.value("baz", Integer.class).set(2);

        Map<String, ?> values = ThreadGlobal.dumpValues();

        assertThat(values.get("foo")).isEqualTo("bye");
        assertThat(values.get("bar")).isEqualTo("hi");
        assertThat(values.get("baz")).isEqualTo(2);
    }

    @Test
    public void testSupportsAutoCloseable() {
        ThreadGlobal.clear();
        ThreadGlobal.value("foo", String.class).set("bye");

        try (ThreadGlobal.AutoCloseable globals = new ThreadGlobal.AutoCloseable()) {
            assertThat(globals.value("foo", String.class).get()).isEqualTo("bye");
        } catch (Exception e) {
        }

        assertThat(ThreadGlobal.contains("foo")).isFalse();
    }
}
