package org.werk2.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class WerkParameterizedTypeParser {
	/**
	 * Will accept primitive types names ("int", "char", etc.) and "byte[]"
	 * For arrays of other types do the folowing:
	 * To determine array class name, run, e.g.:
	 * 		System.out.println(int[].class.getName());
	 * Thus,
	 * "[I" is int[].class's
	 * "[Ljava.lang.String" for String[]
	 * "[Lpacket.to.YourClass" for YourClass[]
	 * 
	 * @param name Full class name
	 * @return Class instance
	 * @throws ClassNotFoundException Class not found
	 */
	public static Class<?> classForName(String name) throws ClassNotFoundException {
		name = name.trim();
		
		if (name.equals("byte[]"))
			return byte[].class;

		if (name.equals("byte"))
			return byte.class;
		if (name.equals("short"))
			return short.class;
		if (name.equals("int"))
			return int.class;
		if (name.equals("long"))
			return long.class;
		if (name.equals("float"))
			return float.class;
		if (name.equals("double"))
			return double.class;
		if (name.equals("boolean"))
			return boolean.class;
		if (name.equals("char"))
			return char.class;

		int ind = name.indexOf('<');
		if (ind > 0)
			name = name.substring(0, ind);

		return Class.forName(name);
	}

	protected Pair<String, String> splitOwnerHierarchy(String typeStr) {
		int genericNesting = 0;
		boolean genericEncountered = false;
		int i = 0;
		for (i = 0; i < typeStr.length(); i++) {
			if (typeStr.charAt(i) == '<') {
				genericNesting++;
				genericEncountered = true;
			}
			if (typeStr.charAt(i) == '>')
				genericNesting--;
			
			if (genericEncountered && genericNesting == 0) {
				if (typeStr.charAt(i) == '.' || typeStr.charAt(i) == '$') {
					break;
				}
			}
		}
		
		if (i < typeStr.length())
			return new Pair<>(typeStr.substring(0, i), typeStr.substring(i+1));
		else
			return new Pair<>(typeStr, null);
	}
	
	protected List<String> splitGenerics(String typeStr) {
		List<String> lst = new ArrayList<>();
		
		int genericNesting = 0;
		int prev = 0, i = 0;
		for (i = 0; i < typeStr.length(); i++) {
			if (typeStr.charAt(i) == '<')
				genericNesting++;
			if (typeStr.charAt(i) == '>')
				genericNesting--;
			
			if (genericNesting == 0)
				if (typeStr.charAt(i) == ',') {
					lst.add(typeStr.substring(prev, i));
					prev = i+1;
				}
		}
		
		lst.add(typeStr.substring(prev, i));
		return lst;
	}
	
	protected Type parseGeneric(String typeStr, Type ownerType) throws ClassNotFoundException {
		typeStr = typeStr.trim();
		int genStart = typeStr.indexOf('<');
		
		String ownerTypeName = ownerType == null ? "" : 
			(ownerType instanceof ParameterizedType) ? 
					((ParameterizedType)ownerType).getRawType().getTypeName() + "$" : 
					ownerType.getTypeName() + "$";
		
		if (genStart < 0) {
			return classForName(ownerTypeName + typeStr);
		} else {
			String rawTypeStr = typeStr.substring(0, genStart);
			Class<?> rawType = classForName(ownerTypeName + rawTypeStr);
			
			int genEnd = typeStr.lastIndexOf('>');
			List<String> genericStrs = splitGenerics(typeStr.substring(genStart + 1, genEnd));
			Type[] genericTypes = new Type[genericStrs.size()];
			for (int i = 0; i < genericStrs.size(); i++)
				genericTypes[i] = parse(genericStrs.get(i));
			
			return WerkParameterizedTypeImpl.make(rawType, genericTypes, ownerType);
		}
	}

	protected Type parse(String typeStr, Type ownerType) throws ClassNotFoundException {
		typeStr = typeStr.trim();

		Pair<String, String> ownershipPair = splitOwnerHierarchy(typeStr);
		Type newType = parseGeneric(ownershipPair.getValue0(), ownerType);
		
		if (ownershipPair.getValue1() == null)
			return newType;
		else
			return parse(ownershipPair.getValue1(), newType);			
	}

	/**
	 * @param typeStr E.g.: T1<T2, T3<T4, T5>.T6<T7>>.T8<T9, T10<T11, T12.T13>.T14>
	 * @return raw Type or ParameterizedType
	 */
	public Type parse(String typeStr) throws ClassNotFoundException {
		return parse(typeStr, null);
	}
}
