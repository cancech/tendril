package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.dom.annotation.AppliedAnnotation;
import tendril.dom.method.MethodElement;
import tendril.dom.type.NamedTypeElement;
import tendril.dom.type.core.ClassType;

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
    protected void processMethod(ClassType classData, MethodElement<?> methodData) {
        String signature = classData.getFullyQualifiedName() + "::" + methodData.getName() + "[" + methodData.getType().getSimpleName() + "](";
        for (NamedTypeElement<?> d: methodData.getParameters()) {
            for (AppliedAnnotation ad: d.getAnnotations()) {
                signature += "@" + ad.getClassName() + "[";
                for (MethodElement<?> md: ad.getParameters())
                    signature += md.getName() + "=" + ad.getValue(md).getValue() + ", ";
                signature += "] ";
            }
            signature += d.getType().getSimpleName() + " " + d.getName() + ", ";
        }
        System.out.println("2ndPass: " + signature + ")");
    }
}
