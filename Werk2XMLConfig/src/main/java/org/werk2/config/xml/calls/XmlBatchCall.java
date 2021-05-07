package org.werk2.config.xml.calls;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.werk2.config.Doc;
import org.werk2.config.calls.BatchCall;
import org.werk2.config.calls.Call;
import org.werk2.config.calls.Concurrency;
import org.werk2.config.xml.XmlDocEntry;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlType(name = "batchCall")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlBatchCall implements BatchCall {
	@XmlElement(required = false)
    public List<XmlCall> call;
	@XmlElement(required = false)
    public List<XmlBatchCall> batch;
	@XmlElement(required = false, defaultValue = "SYNCHRONIZED")
    public XmlConcurrency concurrency;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public Optional<List<Call>> getCalls() {
		return call == null ? Optional.empty() : Optional.of((List<Call>)(List)call);
	}

	@Override
	public Optional<List<BatchCall>> getBatches() {
		return batch == null ? Optional.empty() : Optional.of((List<BatchCall>)(List)batch);
	}

	@Override
	public Optional<Concurrency> getConcurrency() {
		return Optional.of(XmlConcurrency.toConcurrency(concurrency));
	}
}
