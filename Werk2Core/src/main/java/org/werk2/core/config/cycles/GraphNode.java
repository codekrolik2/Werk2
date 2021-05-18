package org.werk2.core.config.cycles;

import java.util.ArrayList;
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
}

