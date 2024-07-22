/*
 * Copyright 2024 Jaroslav Bosak
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
package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.NamedType;
import tendril.codegen.field.type.ClassType;

@SupportedAnnotationTypes("tendril.bean.EnumProvider")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class EnumProviderProcessor extends AbstractTendrilProccessor {
    
    @Override
    protected void findAndProcessElements(TypeElement annotation, RoundEnvironment env) {
        findAndProcessElements(annotation, env, customAnnon -> {
            // Custom Annotation
            System.out.println("customAnnon: " + customAnnon);
            super.findAndProcessElements((TypeElement)customAnnon, FirstPassCollector.getInitialEnvironment(env), defaultConsumer());
        });
    }

    @Override
    protected void processType(ClassType data) {
        System.out.println("2ND PASS: " + data.getFullyQualifiedName());
    }
    
    @Override
    protected void processMethod(ClassType classData, JMethod<?> methodData) {
        String signature = classData.getFullyQualifiedName() + "::" + methodData.getName() + "[" + methodData.getType().getSimpleName() + "](";
        for (NamedType<?> d: methodData.getParameters()) {
            for (JAnnotation ad: d.getAnnotations()) {
                signature += ad.getName() + "[";
                for (JMethod<?> md: ad.getAttributes())
                    signature += md.getName() + "=" + ad.getValue(md).getValue() + ", ";
                signature += "] ";
            }
            signature += d.getType().getSimpleName() + " " + d.getName() + ", ";
        }
        System.out.println("2ndPass: " + signature + ")");
    }
}
