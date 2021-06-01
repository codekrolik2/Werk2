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
	protected boolean visited = false;
	protected boolean entered = false;

	public GraphNode(String name, String[] links) {
		this.name = name;
		this.links.addAll(Arrays.asList(links));
	}
}

