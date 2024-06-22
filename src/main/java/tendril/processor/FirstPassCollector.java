package tendril.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class FirstPassCollector extends AbstractProcessor {
    
    private static RoundEnvironment firstPassEnvironment = null;
    
    public static RoundEnvironment getInitialEnvironment(RoundEnvironment defaultEnv) {
        if (firstPassEnvironment == null)
            return defaultEnv;
        
        return firstPassEnvironment;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (firstPassEnvironment == null)
            FirstPassCollector.firstPassEnvironment = roundEnv;
        return false;
    }

}
