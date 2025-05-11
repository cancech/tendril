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

import tendril.bean.qualifier.Descriptor;

/**
 * Exception to be thrown when a bean has failed to be created
 */
public class BeanCreationException extends Error {
    /** Serial ID */
    private static final long serialVersionUID = 2322041446032429263L;
    
    /**
     * Helper to assemble the message which is to be reported 
     * @param desc
     * @param msg
     * @return
     */
    private static String buildMessage(Descriptor<?> desc, String msg) {
        return msg + " [" + desc + "]";
    }

    /**
     * CTOR
     * 
     * @param desc {@link Descriptor} describing the bean that was attempted to be created
     * @param reason {@link String} indicating the reason of the failure
     */
    public BeanCreationException(Descriptor<?> desc, String reason) {
        super(buildMessage(desc, reason));
    }
    
    /**
     * CTOR
     * 
     * @param desc {@link Descriptor} describing the bean that was attempted to be created
     * @param reason {@link Exception} indicating the reason of the failure
     */
    public BeanCreationException(Descriptor<?> desc, Exception reason) {
        super(buildMessage(desc, "Failed to create bean"), reason);
    }
}
