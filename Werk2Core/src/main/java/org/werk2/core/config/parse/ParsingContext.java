package org.werk2.core.config.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.werk2.config.entities.Event;
import org.werk2.config.entities.ListenerCall;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParsingContext {
	protected Stack<List<? extends ListenerCall>> projectedListeners = new Stack<>();
	protected Stack<String> ancestorNames = new Stack<>();
	
	public String getNamePrefix() {
		StringBuilder flowNameBuilder = new StringBuilder();
		for (String ancestor : getAncestorNames()) {
			flowNameBuilder.append(ancestor);
			flowNameBuilder.append(":");
		}
		return flowNameBuilder.toString();
	}
	
	public static List<ListenerCall> filterListeners(Event[] events, List<? extends ListenerCall> listeners) {
		Set<Event> eventSet = new HashSet<>();
		for (Event event : events)
			eventSet.add(event);
		
		return filterListeners(eventSet, listeners);
	}
	
	public static List<ListenerCall> filterListeners(Set<Event> eventSet, List<? extends ListenerCall> listeners) {
		List<ListenerCall> list = new ArrayList<>();
		for (ListenerCall listener : listeners) {
			for (Event event : listener.getEvents()) {
				if (eventSet.contains(event)) {
					list.add(listener);
					break;
				}
			}
		}
		
		return list;
	}
	
	public List<ListenerCall> filterListeners(Event[] events) {
		Set<Event> eventSet = new HashSet<>();
		for (Event event : events)
			eventSet.add(event);
		
		List<ListenerCall> filteredList = new ArrayList<>();

		for (List<? extends ListenerCall> list : projectedListeners)
			filteredList.addAll(filterListeners(eventSet, list));
		
		return filteredList;
	}
}
