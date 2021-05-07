package org.werk2.config.xml.functions;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.xml.XmlDocEntry;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "function")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunction implements Function {
	@XmlElement(required = true)
	public String functionName;
	@XmlElement(required = true)
	public List<XmlFunctionSignature> signature;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public String getFunctionName() {
		return functionName;
	}

	@Override
	public List<FunctionSignature> getSignatures() {
		return (List<FunctionSignature>)(List)signature;
	}
}
