package org.werk2.config.xml.functions;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.functions.FunctionParameter;
import org.werk2.config.functions.FunctionSignature;
import org.werk2.config.xml.XmlDocumented;

@XmlType(name = "signature")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlFunctionSignature extends XmlDocumented implements FunctionSignature {
	@XmlElement(required = false)
    public List<XmlFunctionParameter> parameter;

	@Override
	public Optional<? extends List<? extends FunctionParameter>> getParameters() {
		return parameter == null ? Optional.empty() : Optional.of(parameter);
	}

	//Generated
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		return result;
	}

	//Generated
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlFunctionSignature other = (XmlFunctionSignature) obj;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		return true;
	}
}
