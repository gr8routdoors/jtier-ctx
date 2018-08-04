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
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Provides a {@link ExecutorService} that propagates {@link ThreadGlobal} values from the current thread to newly
 * spawned threads.
 */
public class GlobalPropagatingExecutorService extends ThreadPoolExecutor {

    public GlobalPropagatingExecutorService(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public GlobalPropagatingExecutorService(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,
                                            ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public GlobalPropagatingExecutorService(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,
                                            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public GlobalPropagatingExecutorService(int corePoolSize,
                                            int maximumPoolSize,
                                            long keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue,
                                            ThreadFactory threadFactory,
                                            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        Map<String, ?> globalValues = ThreadGlobal.dumpValues();
        Runnable wrapper = new GlobalPropagatingRunnable (runnable, globalValues);

        return super.newTaskFor(wrapper, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        Map<String, ?> globalValues = ThreadGlobal.dumpValues();
        Callable<T> wrapper = new GlobalPropagatingCallable<>(callable, globalValues);

        return super.newTaskFor(wrapper);
    }
}
