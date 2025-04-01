/*
 * Copyright 2025 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tempApp.test;

import org.junit.jupiter.api.Test;

import tendril.context.ApplicationContext;

/**
 * Test which launches the application, allowing for it to be used as part of a unit test suite
 */
public class LaunchTest {

    @Test
    public void test() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.start();
    }
}
