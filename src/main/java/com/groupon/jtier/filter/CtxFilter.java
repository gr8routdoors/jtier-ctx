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
package com.groupon.jtier.filter;

import com.groupon.jtier.Ctx;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * Provides a Servlet filter that automatically attaches a context to the current request thread for so that it can
 * be consumed by downstream libraries with {@link Ctx#fromThread()}.
 *
 * @version 1.0
 */
public class CtxFilter implements Filter {

    private final Ctx defaultCtx;

    /**
     * Constructor that allows for
     *
     * @param defaultCtx   The default context to share between requests.
     */
    public CtxFilter(final Ctx defaultCtx) {
        this.defaultCtx = defaultCtx;
    }

    /**
     * Default constructor that creates an empty default context.
     */
    public CtxFilter() {
        this(Ctx.empty());
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {

        final Ctx requestCtx = defaultCtx.newRoot();
        requestCtx.attachToThread();

        try {
            chain.doFilter(servletRequest, response);
        }
        finally {
            requestCtx.cancel();
        }
    }

    @Override
    public void destroy() {

    }
}
