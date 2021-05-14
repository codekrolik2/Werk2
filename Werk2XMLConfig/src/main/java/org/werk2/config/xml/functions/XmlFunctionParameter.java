package org.werk2.config.xml.functions;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.ParameterDirection;
import org.werk2.config.functions.ParameterPassing;
import org.werk2.config.functions.WerkParameterType;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "prm")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunctionParameter extends XmlDocumented implements FunctionParameter {
	@XmlElement(required = true)
	public String name;
	@XmlElement(required = true)
	public XmlParameterDirection direction;
	@XmlElement(required = true)
	public TypeXml type;
	@XmlElement(required = false)
	public String runtimeType;
	@XmlElement(required = false, defaultValue = "SYSTEM_DEFAULT")
	public XmlParameterPassing pass;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ParameterDirection getDirection() {
		return XmlParameterDirection.toParameterDirection(direction);
	}

	@Override
	public WerkParameterType getType() {
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
