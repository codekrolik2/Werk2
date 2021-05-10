package org.werk2.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.javatuples.Pair;
import org.junit.Test;

public class TestWerkGenerics<I, J> {
	class MyGen<T> {
		class Inn<G> { }		
	}
	
	public void parseMap(Map<Integer, TestWerkGenerics<String, Boolean>.MyGen<byte[]>.Inn<Long>> map) {
		
	}
	
	public void parseMap2(Map<Integer, TestWerkGenerics<String, Boolean>.MyGen<byte[]>> map) {
		
	}
	
	public void parseMap3(Map<Integer, TestWerkGenerics<String, Boolean>.MyGen<Boolean>.Inn<Long>> map) {
		
	}
	
	public void parseMap4(Map<Integer, TestWerkGenerics<String, Boolean>.MyGen<byte[]>.Inn<Double>> map) {
		
	}
	
	public void parseMap5(Map<Integer, TestWerkGenerics<String, Integer>.MyGen<byte[]>.Inn<Long>> map) {
		
	}
	
	public void parseMap6(Map<Integer, TestWerkGenerics<Integer, Boolean>.MyGen<byte[]>.Inn<Long>> map) {
		
	}

	@Test
	public void test() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		WerkParameterizedTypeParser p = new WerkParameterizedTypeParser();
		Pair<String, String> hie1 = p.splitOwnerHierarchy("T1<T2, T3<T4, T5>.T6<T7>>.T8<T9, T10<T11, T12.T13>.T14>.T10<T11>");
		assertEquals(hie1.getValue0(), "T1<T2, T3<T4, T5>.T6<T7>>");
		assertEquals(hie1.getValue1(), "T8<T9, T10<T11, T12.T13>.T14>.T10<T11>");
		
		Pair<String, String> hie2 = p.splitOwnerHierarchy("T1<T2, T3<T4, T5>.T6<T7>>");
		assertEquals(hie2.getValue0(), "T1<T2, T3<T4, T5>.T6<T7>>");
		assertNull(hie2.getValue1());
		
		Method method1 = TestWerkGenerics.class.getMethod("parseMap", Map.class);
		Method method2 = TestWerkGenerics.class.getMethod("parseMap2", Map.class);
		Method method3 = TestWerkGenerics.class.getMethod("parseMap3", Map.class);
		Method method4 = TestWerkGenerics.class.getMethod("parseMap4", Map.class);
		Method method5 = TestWerkGenerics.class.getMethod("parseMap5", Map.class);
		Method method6 = TestWerkGenerics.class.getMethod("parseMap6", Map.class);

		String hier = 
				"java.util.Map<java.lang.Integer, "
				+ "org.werk2.generics.TestWerkGenerics<java.lang.String, java.lang.Boolean>"
				+ ".MyGen<byte[]>.Inn<java.lang.Long>>";
		Type t = p.parse(hier);
		
		assertEquals(t, method1.getGenericParameterTypes()[0]);
		assertNotEquals(t, method2.getGenericParameterTypes()[0]);
		assertNotEquals(t, method3.getGenericParameterTypes()[0]);
		assertNotEquals(t, method4.getGenericParameterTypes()[0]);
		assertNotEquals(t, method5.getGenericParameterTypes()[0]);
		assertNotEquals(t, method6.getGenericParameterTypes()[0]);

		String hier2 = 
				"java.util.Map<java.lang.Integer, "
				+ "org.werk2.generics.TestWerkGenerics<java.lang.Integer, java.lang.Boolean>"
				+ "$MyGen<[B>$Inn<java.lang.Long>>";
		Type t2 = p.parse(hier2);

		assertNotEquals(t2, method1.getGenericParameterTypes()[0]);
		assertNotEquals(t2, method2.getGenericParameterTypes()[0]);
		assertNotEquals(t2, method3.getGenericParameterTypes()[0]);
		assertNotEquals(t2, method4.getGenericParameterTypes()[0]);
		assertNotEquals(t2, method5.getGenericParameterTypes()[0]);
		assertEquals(t2, method6.getGenericParameterTypes()[0]);
	}
}
