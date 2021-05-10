package org.werk2.experiment3.parameterizedtype;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.werk2.generics.WerkParameterizedTypeParser;

public class Experiment3<I, J> {
	class MyGen<T> {
		class Inn<G> { }		
	}
	
	public void parseMap(Map<Integer, Experiment3<String, Boolean>.MyGen<Character>.Inn<Long>> map) {
		
	}
	
	public void parseMap2(Map<Integer, Experiment3<String, Boolean>.MyGen<Character>> map) {
		
	}
	
	public void parseMap3(Map<Integer, Experiment3<String, Boolean>.MyGen<Boolean>.Inn<Long>> map) {
		
	}
	
	public void parseMap4(Map<Integer, Experiment3<String, Boolean>.MyGen<Character>.Inn<Double>> map) {
		
	}
	
	public void parseMap5(Map<Integer, Experiment3<String, Integer>.MyGen<Character>.Inn<Long>> map) {
		
	}
	
	public void parseMap6(Map<Integer, Experiment3<Integer, Boolean>.MyGen<Character>.Inn<Long>> map) {
		
	}

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		WerkParameterizedTypeParser p = new WerkParameterizedTypeParser();

		String hier = 
				"java.util.Map<java.lang.Integer, "
				+ "org.werk2.experiment3.parameterizedtype.Experiment3<java.lang.String, java.lang.Boolean>"
				+ ".MyGen<java.lang.Character>.Inn<java.lang.Long>>";
		Type t = p.parse(hier);
		System.out.println(t.toString());
		
		Method method1 = Experiment3.class.getMethod("parseMap", Map.class);
		System.out.println(method1.getGenericParameterTypes()[0]);
		
		Method method2 = Experiment3.class.getMethod("parseMap2", Map.class);
		System.out.println(method2.getGenericParameterTypes()[0]);
		
		Method method3 = Experiment3.class.getMethod("parseMap3", Map.class);
		System.out.println(method3.getGenericParameterTypes()[0]);
		
		Method method4 = Experiment3.class.getMethod("parseMap4", Map.class);
		System.out.println(method4.getGenericParameterTypes()[0]);
		
		Method method5 = Experiment3.class.getMethod("parseMap5", Map.class);
		System.out.println(method5.getGenericParameterTypes()[0]);
		
		Method method6 = Experiment3.class.getMethod("parseMap6", Map.class);
		System.out.println(method6.getGenericParameterTypes()[0]);
		
		System.out.println(t.equals(method1.getGenericParameterTypes()[0]));
		System.out.println(t.equals(method2.getGenericParameterTypes()[0]));
		System.out.println(t.equals(method3.getGenericParameterTypes()[0]));
		System.out.println(t.equals(method4.getGenericParameterTypes()[0]));
		System.out.println(t.equals(method5.getGenericParameterTypes()[0]));
		System.out.println(t.equals(method6.getGenericParameterTypes()[0]));
	}
}
