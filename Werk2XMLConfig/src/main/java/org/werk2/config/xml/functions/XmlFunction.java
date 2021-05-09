package org.werk2.config.xml.functions;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "function")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunction extends XmlDocumented implements Function {
	@XmlElement(required = true)
	public String functionName;
	@XmlElement(required = false)
	public String physicalName;
	@XmlElement(required = true)
	public List<XmlFunctionSignature> signature;

	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public List<? extends FunctionSignature> getSignatures() {
		return signature;
	}

	@Override
	public Optional<String> getPhysicalName() {
		return physicalName == null ? Optional.empty() : Optional.of(physicalName);
	}
}
