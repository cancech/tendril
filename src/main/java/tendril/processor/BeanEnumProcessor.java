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

import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

import tendril.bean.EnumProvider;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassFactory;
import tendril.codegen.field.JValueFactory;
import tendril.dom.type.core.ClassType;

@SupportedAnnotationTypes("tendril.bean.BeanEnum")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BeanEnumProcessor extends AbstractTendrilProccessor {

    @Override
    public void processType(ClassType data) {
        ClassType providerClass = data.generateFromClassSuffix("Provider");
        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(providerClass.getFullyQualifiedName());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.print(generateCode(providerClass, data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateCode(ClassType provider, ClassType sourceEnum) throws ClassNotFoundException {
        JClass cls = JClassFactory.createAnnotation(VisibilityType.PUBLIC, provider);
        cls.annotate(Retention.class, JValueFactory.from(RetentionPolicy.RUNTIME));
        cls.annotate(Target.class, JValueFactory.from(ElementType.METHOD, ElementType.TYPE));
        cls.annotate(EnumProvider.class);
        cls.buildMethod(sourceEnum, "value").setVisibility(VisibilityType.PUBLIC).build();
        return cls.generateCode();
    }
}
