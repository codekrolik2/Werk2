package org.werk2.config.annotation.scan;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;

import org.scannotation.AnnotationDB;
import org.werk2.common.OutParam;
import org.werk2.common.Ret;
import org.werk2.common.TransitRet;
import org.werk2.common.WerkTypeMatcher;
import org.werk2.config.annotation.annotations.AnnoType;
import org.werk2.config.annotation.annotations.In;
import org.werk2.config.annotation.annotations.Out;
import org.werk2.config.annotation.annotations.WerkFunction;
import org.werk2.config.annotation.functions.AnnoDocEntry;
import org.werk2.config.annotation.functions.AnnoFunction;
import org.werk2.config.annotation.functions.AnnoFunctionParameter;
import org.werk2.config.annotation.functions.AnnoFunctionSignature;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.config.functions.ParameterPassing;
import org.werk2.config.functions.WerkParameterType;

public class WerkAnnotationScanner {
	protected List<URL> findClassPaths() throws MalformedURLException {
		List<URL> list = new ArrayList<>();
		String classpath = System.getProperty("java.class.path");
		StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);
		
		while (tokenizer.hasMoreTokens()) {
			String path = tokenizer.nextToken();
			File fp = new File(path);
			if (!fp.exists()) {
				throw new MalformedURLException("File in java.class.path doesn't exist: " + fp);
			} else {
				list.add(fp.toURI().toURL());
			}
		}
		
		return list;
	}

	protected List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
	    final List<Method> methods = new ArrayList<Method>();
	    Class<?> klass = type;
        for (final Method method : klass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }
	    return methods;
	}
	

	protected List<AnnoFunctionParameter> loadfunctionParameters(Method method, String physicalFunctionName) {
		List<AnnoFunctionParameter> parameters = new ArrayList<>();

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Parameter[] params = method.getParameters();
        
        for (int i = 0; i < params.length; i++) {
    		String name = null;
    		ParameterDirection direction = null;
    		WerkParameterType type = null;
    		Optional<String> runtimeType = null;
    		Optional<ParameterPassing> pass = null;
    		Optional<AnnoDocEntry> doc = null;

    		Parameter prm = params[i];
        	Type genericParameterType = genericParameterTypes[i];

    		//----------------------------
        	
    		In inAnno = prm.getAnnotation(In.class);
        	Out outAnno = prm.getAnnotation(Out.class);

			//Parameters must be marked as either @In or @Out, but not both
			if (inAnno == null && outAnno == null) {
				throw new AnnotationFormatError(
					String.format("WerkFunction parameters must be annotated as either @In or @Out. [%s(%s)]",
						physicalFunctionName, prm.getName()));
			} else if (inAnno != null && outAnno != null) {
				throw new AnnotationFormatError(
					String.format("WerkFunction parameters can't be annotated as both @In and @Out. [%s(%s)]",
						physicalFunctionName, prm.getName()));
    		} else if (inAnno != null) {
    			direction = ParameterDirection.IN;

    			if (!inAnno.name().trim().equals(""))
    				name = inAnno.name().trim();
    			else
    				name = prm.getName();
    			
    			if (inAnno.type() != AnnoType.AUTO)
    				type = AnnoType.toParameterType(inAnno.type());
    			else
    				type = WerkTypeMatcher.matchType(genericParameterType);
    			
    			if (!inAnno.runtimeType().trim().equals(""))
    				runtimeType = Optional.of(inAnno.runtimeType().trim());
    			else
    				runtimeType = Optional.of(genericParameterType.getTypeName());
    			
    			//This is the only parameter passing Java supports
    			pass = Optional.of(ParameterPassing.SYSTEM_DEFAULT);
    			
    			if (!inAnno.docTitle().trim().equals("") || !inAnno.docDescription().trim().equals("")) {
    			    Optional<String> title;
    			    Optional<String> description;
    			    
    			    if (!inAnno.docTitle().trim().equals(""))
    			    	title = Optional.of(inAnno.docTitle().trim());
    			    else
    			    	title = Optional.empty();
    			    
    			    if (!inAnno.docDescription().trim().equals(""))
    			    	description = Optional.of(inAnno.docDescription().trim());
    			    else
    			    	description = Optional.empty();

    			    doc = Optional.of(new AnnoDocEntry(title, description));
    			} else
    				doc = Optional.empty();
    		}

    		//----------------------------
    		
    		else if (outAnno != null) {
    			direction = ParameterDirection.OUT;

				//All @Out parameters must be of type OutParam
    			if (!prm.getType().equals(OutParam.class)) {
        			throw new AnnotationFormatError(
	        				String.format(
	        					"WerkFunction @Out parameters must be of type [%s]. [%s(%s)]", 
	        					OutParam.class, physicalFunctionName, prm.getName()
	        				)
	        			);
    			}

    			if (!outAnno.name().trim().equals(""))
    				name = outAnno.name().trim();
    			else
    				name = prm.getName();
    			
    			if (outAnno.type() != AnnoType.AUTO)
    				type = AnnoType.toParameterType(outAnno.type());
    			else {
    	            if (genericParameterType instanceof ParameterizedType){
    	                ParameterizedType aType = (ParameterizedType) genericParameterType;
    	                Type[] parameterArgTypes = aType.getActualTypeArguments();
    	                for (Type parameterArgType : parameterArgTypes){
    	                    type = WerkTypeMatcher.matchType(parameterArgType);
    	                    break;
    	                }
    	            } else
    	            	type = WerkTypeMatcher.matchType(Object.class);//RUNTIME
    			}
    			
    			if (!outAnno.runtimeType().trim().equals("")) {
    				runtimeType = Optional.of(outAnno.runtimeType().trim());
    			} else {
    	            if (genericParameterType instanceof ParameterizedType){
    	                ParameterizedType aType = (ParameterizedType) genericParameterType;
    	                Type[] parameterArgTypes = aType.getActualTypeArguments();
    	                for (Type parameterArgType : parameterArgTypes){
	        				runtimeType = Optional.of(parameterArgType.getTypeName());
    	                    break;
    	                }
    	            } else
        				runtimeType = Optional.of(Object.class.getTypeName());
    			}
    			
    			//This is the only parameter passing Java supports
    			pass = Optional.of(ParameterPassing.SYSTEM_DEFAULT);
    			
    			if (!outAnno.docTitle().trim().equals("") || !outAnno.docDescription().trim().equals("")) {
    			    Optional<String> title;
    			    Optional<String> description;
    			    
    			    if (!outAnno.docTitle().trim().equals(""))
    			    	title = Optional.of(outAnno.docTitle().trim());
    			    else
    			    	title = Optional.empty();
    			    
    			    if (!outAnno.docDescription().trim().equals(""))
    			    	description = Optional.of(outAnno.docDescription().trim());
    			    else
    			    	description = Optional.empty();

    			    doc = Optional.of(new AnnoDocEntry(title, description));
    			} else
    				doc = Optional.empty();
    		}

    		AnnoFunctionParameter aprm = new AnnoFunctionParameter(name, direction, type, runtimeType, pass, doc);
    		parameters.add(aprm);
        }
		
        return parameters;
	}
	
	protected void checkReturnType(Type returnType, String physicalFunctionName) {
		//Only status, empty message
		if (returnType.equals(int.class) || returnType.equals(Integer.class))
			return;

		if (returnType.equals(Ret.class) || returnType.equals(TransitRet.class))
			return;

		Class<?>[] interfaces = null;
		if (returnType instanceof ParameterizedType) {
			Type rawType = ((ParameterizedType)returnType).getRawType();
			if (rawType instanceof Class) {
				if (rawType.equals(Ret.class) || rawType.equals(TransitRet.class))
					return;
				
				interfaces = ((Class<?>)rawType).getInterfaces();
			}
		}
		
		if (returnType instanceof Class)
			interfaces = ((Class<?>)returnType).getInterfaces();

		if (interfaces != null)
		for (Class<?> intrf : interfaces)
			if (intrf.equals(Ret.class) || intrf.equals(TransitRet.class))
				return;

		throw new AnnotationFormatError(
				String.format(
					"WerkFunction must return %s, %s, %s or %s. Func: [%s(...)]", 
					int.class, Integer.class, Ret.class, TransitRet.class, 
					physicalFunctionName
				)
			);
	}
	
	public List<Function> loadRawFunctions() throws IOException, ClassNotFoundException {
		URL[] urls = findClassPaths().toArray(new URL[] {});

		AnnotationDB db = new AnnotationDB();
		db.scanArchives(urls);

		Map<String, List<FunctionSignature>> signaturesByPhysicalName = new HashMap<>();
		Map<String, Set<String>> signaturesByLogicalName = new HashMap<>();
		
		Set<String> classesWithWerkFunctions = db.getAnnotationIndex().get(WerkFunction.class.getName());
		if (classesWithWerkFunctions != null) {
			for (String className : classesWithWerkFunctions) {
				Class<?> klass = Class.forName(className);
				List<Method> methods = getMethodsAnnotatedWith(klass, WerkFunction.class);
				
				for (Method method : methods) {
					WerkFunction wf = method.getAnnotation(WerkFunction.class);

					String logicalFunctionName = wf.name();
					String physicalFunctionName = String.format("%s.%s", method.getDeclaringClass().getCanonicalName(), method.getName());

					//Non-static method can't be marked as a WerkFunction
					boolean isStatic = Modifier.isStatic(method.getModifiers());
					if (!isStatic)
    					throw new AnnotationFormatError(
		        				String.format(
		        					"WerkFunction must be static. [%s(...)]", 
		        					physicalFunctionName
		        				)
		        			);

					//make sure ReturnType is corect
					Type returnType = method.getGenericReturnType();
					checkReturnType(returnType, physicalFunctionName);
					
					//Parameters
					List<AnnoFunctionParameter> parameters = loadfunctionParameters(method, physicalFunctionName);
			        
			        AnnoDocEntry doc = null;
        			if (!wf.docTitle().trim().equals("") || !wf.docDescription().trim().equals("")) {
        			    Optional<String> title;
        			    Optional<String> description;
        			    
        			    if (!wf.docTitle().trim().equals(""))
        			    	title = Optional.of(wf.docTitle().trim());
        			    else
        			    	title = Optional.empty();
        			    
        			    if (!wf.docDescription().trim().equals(""))
        			    	description = Optional.of(wf.docDescription().trim());
        			    else
        			    	description = Optional.empty();

        			    doc = new AnnoDocEntry(title, description);
        			}

        			FunctionSignature signature = new AnnoFunctionSignature(
        					parameters.isEmpty() ? Optional.empty() : Optional.of(parameters),
        					doc == null ? Optional.empty() : Optional.of(doc));
					
					if (!logicalFunctionName.trim().equals("")) {
    					Set<String> logFuncs = signaturesByLogicalName.get(logicalFunctionName);
    					if (logFuncs == null) {
    						logFuncs = new HashSet<>();
    						signaturesByLogicalName.put(logicalFunctionName, logFuncs);
    					}
    					logFuncs.add(physicalFunctionName);
					}

					List<FunctionSignature> physFuncs = signaturesByPhysicalName.get(physicalFunctionName);
					if (physFuncs == null) {
						physFuncs = new ArrayList<>();
						signaturesByPhysicalName.put(physicalFunctionName, physFuncs);
					}
					physFuncs.add(signature);
				}
			}
		}
		
		//-------------------------------------------------------
		
    	List<Function> functions = new ArrayList<>();
    	
		//Logical function name can't point to more than one physical function name (overloads are allowed)
		//All function names, logical and physical should be unique
		for (Entry<String, Set<String>> ent : signaturesByLogicalName.entrySet()) {
			String functionName = ent.getKey();
			Set<String> physNames = ent.getValue();
			
			if (physNames.size() > 1) {
				StringBuilder b = new StringBuilder();
				b.append("Logical function name can't point to more than one physical function name.").
				append(" Logical: [").append(functionName).append("();] points to [");
				for (String phys : physNames)
					b.append(phys).append("(); ");
				b.append("]");

    			throw new AnnotationFormatError(b.toString());
			}
			
			if (signaturesByPhysicalName.containsKey(functionName))
    			throw new AnnotationFormatError(
    				String.format("All function names, logical and physical should be unique. "
    					+ "logical: [%s();] clashes with physical [%s(%s);]",
    					functionName, functionName, signaturesByPhysicalName.get(functionName)
    				)
    			);

			String physicalName = null;
			for (String phys : physNames)
				physicalName = phys;

			functions.add(new AnnoFunction(functionName, Optional.of(physicalName), 
    				signaturesByPhysicalName.get(physicalName), Optional.empty()));
		}

		for (Entry<String, List<FunctionSignature>> ent : signaturesByPhysicalName.entrySet()) {
			String functionName = ent.getKey();
			List<FunctionSignature> signatures = ent.getValue();

			functions.add(new AnnoFunction(functionName, Optional.of(functionName), signatures, Optional.empty()));
		}

		return functions;
    }
}
