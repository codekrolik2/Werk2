package org.werk2.core.config.test;

import org.werk2.config.Werk2Config;
import org.werk2.core.config.parse.MermaidCallGraphBuilder;

public class TestMermaidCallGraphBuilder {
	public static void main(String[] args) {
		TestConfigCreator configCreator = new TestConfigCreator();
		Werk2Config config = configCreator.buildConfig();
		
		MermaidCallGraphBuilder graphBuilder = new MermaidCallGraphBuilder();
		System.out.println("----------------------------------------------------------------");
		System.out.println(graphBuilder.build(config));
		System.out.println("----------------------------------------------------------------");
	}
}
