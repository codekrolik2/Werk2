package org.werk2.experiment1.annoconf;

import java.util.List;

import org.werk2.config.annotation.scan.WerkAnnotationScanner;
import org.werk2.config.functions.Function;

public class Experiment1 {
	public static void main(String[] args) throws Exception {
		List<Function> functions = new WerkAnnotationScanner().loadRawFunctions();
		for (Function f : functions) {
			System.out.println(f);
			System.out.println();
		}
	}
}
