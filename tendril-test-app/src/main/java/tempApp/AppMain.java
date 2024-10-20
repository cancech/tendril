package tempApp;

import java.lang.annotation.Annotation;

import tendril.bean.qualifier.BeanId;
import tendril.bean.qualifier.EnumQualifier;

public class AppMain {
	
	public static void main(String[] args) {
		TempClass cls = new TempClass("abc123");
		System.err.println("USING: " + cls);
		System.err.println("MGR: " + new TempManager(cls).doSomething("", new TempClass("321"), 0));
		
		Annotation[] annos = cls.getClass().getAnnotations();
		for (Annotation a: annos) {
			System.err.println("Annotation: " + a);
			EnumQualifier pars = a.annotationType().getAnnotation(EnumQualifier.class);
			if (pars == null) {
				System.err.println(BeanId.class.getSimpleName() + " is not present");
			} else {
				try {
					BeanId id = (BeanId) a.getClass().getMethod("value").invoke(a);
					System.err.println("ID: " + id.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
