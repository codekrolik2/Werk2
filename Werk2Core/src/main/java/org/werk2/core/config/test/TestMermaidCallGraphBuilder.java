package org.werk2.core.config.test;

import java.util.Arrays;

import org.werk2.config.Werk2Config;
import org.werk2.core.config.parse.MermaidCallGraphBuilder;

public class TestMermaidCallGraphBuilder {
	public static void main(String[] args) {
		TestConfigCreator configCreator = new TestConfigCreator();
		Werk2Config config = configCreator.buildConfig();
		
		MermaidCallGraphBuilder graphBuilder = new MermaidCallGraphBuilder(Arrays.asList(new Werk2Config[] {config}));
		System.out.println("----------------------------------------------------------------");
		//System.out.println(graphBuilder.buildAllFlows());
		System.out.println(graphBuilder.buildFlow("Flow1"));
		System.out.println("----------------------------------------------------------------");
	}
}
