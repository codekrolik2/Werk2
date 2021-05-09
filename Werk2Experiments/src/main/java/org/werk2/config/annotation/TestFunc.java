package org.werk2.config.annotation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.werk2.common.OutParam;
import org.werk2.config.annotation.annotations.In;
import org.werk2.config.annotation.annotations.Out;
import org.werk2.config.annotation.annotations.WerkFunction;

public class TestFunc {
	@WerkFunction
	public static int func1(@In int i, @In char c, @In ArrayList<Boolean> lst1, @In Map<String, List<Integer>> mapx) {
		System.out.println(i + " : " + c);
		return -100;
	}

	@WerkFunction
	public static Integer func2(@In int a, @In Integer b, @In String c, @In InputStream d, @Out OutParam<String> out1, @Out OutParam<InputStream> out2) {
		return -100;
	}
}
