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
package tendril.test.runner;

import tendril.context.launch.TendrilRunner;

/**
 * {@link TendrilRunner} implementation to use for the purpose of testing
 */
public class TestTendrilRunner3 implements TendrilRunner {
    
    /** Flag for whether or not an instance of the runner has been run */
    private static boolean isRun = false;
    
    /**
     * @see tendril.context.launch.TendrilRunner#run()
     */
    @Override
    public void run() {
        isRun = true;
    }

    /**
     * Check whether an instance of the runner has been run
     * 
     * @return boolean true if it has been run
     */
    public static boolean hasBeenRun() {
        return isRun;
    }
    
    /**
     * Reset the runner
     */
    public static void reset() {
        isRun = false;
    }
}
