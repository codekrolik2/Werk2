package org.werk2.config.xml.functions;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.ParameterPassing;
import org.werk2.config.functions.ParameterType;
import org.werk2.config.xml.XmlDocEntry;

@XmlType(name = "prm")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunctionParameter implements FunctionParameter {
	@XmlElement(required = true)
	public String name;
	@XmlElement(required = true)
	public TypeXml type;
	@XmlElement(required = false)
	public String runtimeType;
	@XmlElement(required = false, defaultValue = "BY_REF")
	public XmlParameterPassing pass;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParameterType getType() {
		return TypeXml.toParameterType(type);
	}

	@Override
	public Optional<String> getRuntimeType() {
		return runtimeType == null ? Optional.empty() : Optional.of(runtimeType);
	}

	@Override
	public Optional<ParameterPassing> getPassing() {
		return Optional.of(XmlParameterPassing.toParameterPassing(pass));
	}
}
