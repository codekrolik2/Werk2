package org.werk2.config.xml;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.Documented;

@XmlType(name = "documented")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class XmlDocumented implements Documented {
	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}
}
