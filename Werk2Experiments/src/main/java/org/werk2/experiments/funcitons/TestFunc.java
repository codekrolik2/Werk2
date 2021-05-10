package org.werk2.experiments.funcitons;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.werk2.common.OutParam;
import org.werk2.common.TransitRet;
import org.werk2.common.TransitionStatus;
import org.werk2.common.TransitionType;
import org.werk2.config.annotation.annotations.In;
import org.werk2.config.annotation.annotations.Out;
import org.werk2.config.annotation.annotations.WerkFunction;
import org.werk2.experiment2.invoke.TransitRetProto;
import org.werk2.experiment2.invoke.TransitionStatusProto;

public class TestFunc {
	@WerkFunction
	public static TransitRet func1(
			@In(name="i") int i, 
			@In(name="c") char c, 
			@In(name="lst1") List<Boolean> lst1, 
			@In(name="mapx") Map<String, List<Integer>> mapx,
			@Out(name="out1") OutParam<String> out1) {
		System.out.println("i: " + i + " c: " + c);

		System.out.println("LST1: size " + lst1.size());
		for (Boolean b : lst1)
			System.out.println("	" + b);
			
		System.out.println("MAPX: size " + mapx.size());
		for (Entry<String, List<Integer>> e : mapx.entrySet())
			System.out.println("	" + e.getKey() + " : " + e.getValue());

		out1.set("Hello Werk2");
		
		TransitionStatus ts = new TransitionStatusProto(TransitionType.FINISH);
		return new TransitRetProto(-100, "RETVAL", ts);
	}

	@WerkFunction
	public static Integer func2(@In int a, @In Integer b, @In String c, @In InputStream d, @Out OutParam<String> out1, @Out OutParam<InputStream> out2) {
		return -100;
	}
}
