package org.werk2.config.xml.calls;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.calls.InBinding;
import org.werk2.config.calls.OutBinding;
import org.werk2.config.xml.XmlDocEntry;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "call")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlCall implements Call {
	@XmlElement(required = true)
    public String functionName;
	@XmlElement(required = false)
    public List<XmlInBinding> inParameter;
	@XmlElement(required = false)
    public List<XmlOutBinding> outParameter;
	@XmlElement(required = false)
    public String outStatus;
	@XmlElement(required = false)
    public String outStatusMessage;
	@XmlElement(required = false)
    public String outTransitionStatus;
	@XmlElement(required = false, defaultValue = "SYNCHRONIZED")
    public XmlConcurrency concurrency;

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
	public Optional<List<InBinding>> getInParameters() {
		return inParameter == null ? Optional.empty() : Optional.of((List<InBinding>)(List)inParameter);
	}

	@Override
	public Optional<List<OutBinding>> getOutParameters() {
		return outParameter == null ? Optional.empty() : Optional.of((List<OutBinding>)(List)outParameter);
	}

	@Override
	public Optional<String> getOutStatusBinding() {
		return outStatus == null ? Optional.empty() : Optional.of(outStatus);
	}

	@Override
	public Optional<String> getOutStatusMessageBinding() {
		return outStatusMessage == null ? Optional.empty() : Optional.of(outStatusMessage);
	}

	@Override
	public Optional<String> getOutTransitionStatusBinding() {
		return outTransitionStatus == null ? Optional.empty() : Optional.of(outTransitionStatus);
	}

	@Override
	public Optional<Concurrency> getConcurrency() {
		return Optional.of(XmlConcurrency.toConcurrency(concurrency));
	}
}
