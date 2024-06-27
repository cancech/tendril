package tendril.dom.annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for providing the necessary implementation of {@link Annotatable}.
 */
public class AnnotationHandler implements Annotatable {

    /** List of annotations that have been applied to the named type */
    private List<AppliedAnnotation> annotations = new ArrayList<>();

    /**
     * @see tendril.dom.annotation.Annotatable#addAnnotation(tendril.dom.annotation.AppliedAnnotation)
     */
    @Override
    public void addAnnotation(AppliedAnnotation data) {
        annotations.add(data);
    }

    /**
     * @see tendril.dom.annotation.Annotatable#getAnnotations()
     */
    @Override
    public List<AppliedAnnotation> getAnnotations() {
        return annotations;
    }
}
