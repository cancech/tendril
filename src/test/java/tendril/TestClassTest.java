//package tendril;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import tempApp.TempClass;
//import tempApp.TempManager;
//
//@ExtendWith(MockitoExtension.class)
//public class TestClassTest {
//	
//	@Mock
//	private TempClass mockClass;
//
//	@Test
//	void test() {
//		TempClass cls = new TempClass("sdsdf");
//		Assertions.assertEquals("sdsdf", cls.getValue());
//	}
//	
//	@Test
//	void testMock() {
//		when(mockClass.getValue()).thenReturn("myVal");
//		
//		TempManager mgr = new TempManager(mockClass);
//		Assertions.assertEquals("myVal", mgr.doSomething());
//		verify(mockClass).getValue();
//	}
//}
