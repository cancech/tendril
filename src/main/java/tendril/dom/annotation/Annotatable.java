package tendril.dom.annotation;

import java.util.List;

/**
 * Represents an element which can be annotated
 */
public interface Annotatable {
    /**
     * Add an annotation instance to the element
     * 
     * @param data {@link AppliedAnnotation} with the details of the annotation applied to the element
     */
    void addAnnotation(AppliedAnnotation data);

    /**
     * Get all annotations applied to the element
     * 
     * @return {@link List} of {@link AppliedAnnotation} instances applied to the element
     */
    List<AppliedAnnotation> getAnnotations();
}
