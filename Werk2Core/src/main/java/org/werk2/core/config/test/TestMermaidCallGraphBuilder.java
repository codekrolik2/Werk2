package org.werk2.core.config.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.werk2.common.WerkConfigException;
import org.werk2.config.Werk2Config;
import org.werk2.core.config.cycles.GraphNode;
import org.werk2.core.config.cycles.dfs.DFSCycleFinder;
import org.werk2.core.config.cycles.dfs.PathStructure;
import org.werk2.core.config.cycles.tarjan.Tarjan;
import org.werk2.core.config.parse.MermaidCallGraphBuilder;

public class TestMermaidCallGraphBuilder {
	public static void main(String[] args) throws WerkConfigException {
		TestConfigCreator configCreator = new TestConfigCreator();
		Werk2Config config = configCreator.buildConfig();
		
		MermaidCallGraphBuilder graphBuilder = new MermaidCallGraphBuilder(Arrays.asList(new Werk2Config[] {config}));
		
		StringBuilder builder = new StringBuilder();
		System.out.println("----------------------------------------------------------------");
		
		Map<String, GraphNode> graph = graphBuilder.buildEverything(Optional.of(builder));
		//Map<String, GraphNode> graph = graphBuilder.buildAllFlows(Optional.of(builder));
		//Map<String, GraphNode> graph = graphBuilder.buildFunction("Flow3", Optional.of(builder));
		//Map<String, GraphNode> graph = graphBuilder.buildFunction("Executor3", Optional.of(builder));
		//Map<String, GraphNode> graph = graphBuilder.buildFunction("ES_E1", Optional.of(builder));
		
		System.out.println(builder.toString());
		
		System.out.println("----------------------------------------------------------------");
		DFSCycleFinder cycleFinder = new DFSCycleFinder();
		Optional<PathStructure> ps = cycleFinder.findCyclePath(graph.values());
		System.out.println("CycleFound: " + !ps.isEmpty());
		if (!ps.isEmpty())
			System.out.println(ps.get());
		System.out.println("----------------------------------------------------------------");
		
		List<List<String>> sccs = new Tarjan().scc(graph);
		for (List<String> scc : sccs) {
			if (scc.size() > 1)
				System.out.println(scc);
		}
		
		System.out.println("----------------------------------------------------------------");
	}
}
