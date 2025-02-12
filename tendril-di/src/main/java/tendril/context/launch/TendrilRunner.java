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
package tendril.context.launch;

import tendril.context.ApplicationContext;

/**
 * Interface that must be applied to the main entry point class annotated as @{@link Runner}. It should only be employed with @{@link Runner} annotated classes. The goal of
 * these classes is to act as the main entry point into the execution of the {@link ApplicationContext}, leveraging the beans that as made available within to drive the
 * application logic. As such, the run() method is called after the class has been created and initialized, to allow for the purpose of the application to be started.
 * 
 * The concern of this main entry point is purely assembling and triggering the application logic (i.e.: pulling in the disparate beans and starting their respective behaviors
 * and such), whilst the {@link ApplicationContext} can be relied upon to perform the assembly of any/all dependencies that the triggered logic relies upon.
 */
public interface TendrilRunner {

    /**
     * Called to start the application execution within the {@link ApplicationContext}
     */
    void run();
}
