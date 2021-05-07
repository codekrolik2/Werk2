package org.werk2.config.xml;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.werk2.config.Doc;
import org.werk2.config.Werk2Config;
import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;
import org.werk2.config.xml.engine.XmlEngine;
import org.werk2.config.xml.entities.XmlExec;
import org.werk2.config.xml.entities.XmlFlow;
import org.werk2.config.xml.entities.XmlStep;
import org.werk2.config.xml.entities.XmlTransit;
import org.werk2.config.xml.functions.XmlFunction;

@SuppressWarnings({ "unchecked", "rawtypes" })
@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlWerk2Config implements Werk2Config {
	@XmlElement(required = false)
    public XmlEngine engine;
	@XmlElement(required = false)
    public List<XmlFlow> flow;
	@XmlElement(required = false)
    public List<XmlStep> step;
	@XmlElement(required = false)
    public List<XmlExec> exec;
	@XmlElement(required = false)
    public List<XmlTransit> transit;
	@XmlElement(required = false)
    public List<XmlFunction> rawFunction;

	@XmlElement(required = false)
    public XmlDocEntry doc;

	@Override
	public Optional<Doc> getDoc() {
		return doc == null ? Optional.empty() : Optional.of(doc);
	}

	@Override
	public Optional<Engine> getEngine() {
		return engine == null ? Optional.empty() : Optional.of(engine);
	}

	@Override
	public Optional<List<Flow>> getFlows() {
		return flow == null ? Optional.empty() : Optional.of((List<Flow>)(List)flow);
	}

	@Override
	public Optional<List<Step>> getSteps() {
		return step == null ? Optional.empty() : Optional.of((List<Step>)(List)step);
	}

	@Override
	public Optional<List<Exec>> getExecs() {
		return exec == null ? Optional.empty() : Optional.of((List<Exec>)(List)exec);
	}

	@Override
	public Optional<List<Transit>> getTransits() {
		return transit == null ? Optional.empty() : Optional.of((List<Transit>)(List)transit);
	}

	@Override
	public Optional<List<Function>> getRawFunctions() {
		return rawFunction == null ? Optional.empty() : Optional.of((List<Function>)(List)rawFunction);
	}
}
