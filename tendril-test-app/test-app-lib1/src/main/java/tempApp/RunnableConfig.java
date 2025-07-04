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
package tempApp;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;
import tendril.bean.requirement.RequiresNotEnv;

/**
 * 
 */
@Configuration
public class RunnableConfig {
    
    @Bean
    @Singleton
    @Option2
    Runnable first() {
        return () -> System.out.println("First");
    }

    @Bean
    @Singleton
    @Named("second")
    Runnable second() {
        return () -> System.out.println("Second");
    }
    
    @Bean
    @Singleton
    @NotUpperCase
    Runnable notUppercase() {
        return () -> System.out.println("NOT AN UPPERCASE ENVIRONMENT");
    }
    
    @Bean
    @Singleton
    @Named("notenv")
    @RequiresNotEnv("lowercase")
    Runnable notLowercase() {
        return () -> System.out.println("not a lowercase environment");
    }
}
