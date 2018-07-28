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

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CtxKeyTest {
    @Test
    public void testSimpleSetGet() throws Exception {
        final Ctx.Key key = Ctx.key("greeting", String.class);
        Ctx.empty().attach();

        key.set("hola");
        assertThat(key.get()).isEqualTo(Optional.of("hola"));
    }

    @Test
    public void testCtxAfterSetIsPeerOfOriginal() throws Exception {
        final Ctx.Key key = Ctx.key("greeting", String.class);
        Ctx root = Ctx.empty().attach();

        Ctx peer = key.set("hola");
        assertThat(key.get()).isEqualTo(Optional.of("hola"));

        assertThat(Ctx.fromThread()).isPresent();

        root.cancel();
        assertThat(peer.isCancelled()).isTrue();
    }
}
