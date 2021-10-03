package org.werk2.core.config.cycles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class GraphNode {
	@NonNull protected String name;
	protected List<String> links = new ArrayList<>();

	public GraphNode(String name, List<String> links) {
		this.name = name;
		this.links.addAll(links);
	}

	public GraphNode(String name, String[] links) {
		this(name, Arrays.asList(links));
	}
}
