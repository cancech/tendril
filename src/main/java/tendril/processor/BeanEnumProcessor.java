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
import tendril.metadata.classes.ClassData;

@SupportedAnnotationTypes("tendril.bean.BeanEnum")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BeanEnumProcessor extends AbstractTendrilProccessor {

    @Override
    public void processType(ClassData data) {
        ClassData providerClass = data.newClassWithNameSuffix("Provider");
        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(providerClass.getFullyQualifiedName());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.print(generateCode(providerClass, data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateCode(ClassData provider, ClassData sourceEnum) throws ClassNotFoundException {
        JClass cls = JClassFactory.createAnnotation(VisibilityType.PUBLIC, provider);
        cls.annotate(Retention.class, JValueFactory.from(RetentionPolicy.RUNTIME));
        cls.annotate(Target.class, JValueFactory.from(ElementType.METHOD, ElementType.TYPE));
        cls.annotate(EnumProvider.class);
        cls.addMethod(VisibilityType.PUBLIC, sourceEnum, "value");
        return cls.generateCode();
    }
}
