package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.metadata.MethodData;
import tendril.metadata.ParameterData;
import tendril.metadata.classes.AnnotationData;
import tendril.metadata.classes.ClassData;

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
    protected void processType(ClassData data) {
        System.out.println("2ND PASS: " + data.getFullyQualifiedName());
    }
    
    @Override
    protected void processMethod(ClassData classData, MethodData<?> methodData) {
        String signature = classData.getFullyQualifiedName() + "::" + methodData.getName() + "[" + methodData.getType().getSimpleName() + "](";
        for (ParameterData<?> d: methodData.getParameters()) {
            for (AnnotationData ad: d.getAnnotations()) {
                signature += "@" + ad.getClassName() + "[";
                for (MethodData<?> md: ad.getParameters())
                    signature += md.getName() + "=" + ad.getValue(md).getValue() + ", ";
                signature += "] ";
            }
            signature += d.getType().getSimpleName() + " " + d.getName() + ", ";
        }
        System.out.println("2ndPass: " + signature + ")");
    }
}
