package org.werk2.config.xml;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;

@XmlType(name = "doc")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlDocEntry implements Doc {
	@XmlElement(required = false)
    public String title;
	@XmlElement(required = false)
    public String description;
	
	@Override
	public Optional<String> getTitle() {
		return title == null ? Optional.empty() : Optional.of(title);
	}
	
	@Override
	public Optional<String> getDescription() {
		return description == null ? Optional.empty() : Optional.of(description);
	}
}
