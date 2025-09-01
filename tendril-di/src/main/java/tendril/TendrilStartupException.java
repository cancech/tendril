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
package tendril;

/**
 * Exception denoting an issue attempting to start a Tendril application
 */
public class TendrilStartupException extends RuntimeException {
    /** Serialization UID */
    private static final long serialVersionUID = 6393255531293974360L;

    /**
     * CTOR
     * 
     * @param reason {@link String} cause of the exception
     */
    public TendrilStartupException(String reason) {
        super(reason);
    }
    
    /**
     * CTOR
     * 
     * @param cause {@link Exception} which is the cause of the exception
     */
    public TendrilStartupException(Exception cause) {
        super(cause);
    }
}
