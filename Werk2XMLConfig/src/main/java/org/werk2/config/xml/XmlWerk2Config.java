package org.werk2.config.xml;

import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.werk2.config.Werk2Config;
import org.werk2.config.engine.Engine;
import org.werk2.config.entities.Exec;
import org.werk2.config.entities.ExtendedFlow;
import org.werk2.config.entities.ExtendedStep;
import org.werk2.config.entities.Flow;
import org.werk2.config.entities.Step;
import org.werk2.config.entities.Transit;
import org.werk2.config.functions.Function;
import org.werk2.config.xml.engine.XmlEngine;
import org.werk2.config.xml.entities.XmlExec;
import org.werk2.config.xml.entities.XmlExtendedFlow;
import org.werk2.config.xml.entities.XmlExtendedStep;
import org.werk2.config.xml.entities.XmlFlow;
import org.werk2.config.xml.entities.XmlStep;
import org.werk2.config.xml.entities.XmlTransit;
import org.werk2.config.xml.functions.XmlFunction;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlWerk2Config extends XmlDocumented implements Werk2Config {
	@XmlElement(required = false)
    public XmlEngine engine;
	@XmlElement(required = false)
    public List<XmlFlow> flow;
	@XmlElement(required = false)
    public List<XmlStep> step;
	@XmlElement(required = false)
    public List<XmlExtendedFlow> extendedFlow;
	@XmlElement(required = false)
    public List<XmlExtendedStep> extendedStep;
	@XmlElement(required = false)
    public List<XmlExec> exec;
	@XmlElement(required = false)
    public List<XmlTransit> transit;
	@XmlElement(required = false)
    public List<XmlFunction> rawExecFunction;
	@XmlElement(required = false)
    public List<XmlFunction> rawTransitFunction;

	@Override
	public Optional<Engine> getEngine() {
		return engine == null ? Optional.empty() : Optional.of(engine);
	}

	@Override
	public Optional<List<? extends Flow>> getFlows() {
		return flow == null ? Optional.empty() : Optional.of(flow);
	}

	@Override
	public Optional<List<? extends Step>> getSteps() {
		return step == null ? Optional.empty() : Optional.of(step);
	}

	@Override
	public Optional<? extends List<? extends ExtendedFlow>> getExtendedFlows() {
		return extendedFlow == null ? Optional.empty() : Optional.of(extendedFlow);
	}

	@Override
	public Optional<? extends List<? extends ExtendedStep>> getExtendedSteps() {
		return extendedStep == null ? Optional.empty() : Optional.of(extendedStep);
	}

	@Override
	public Optional<List<? extends Exec>> getExecs() {
		return exec == null ? Optional.empty() : Optional.of(exec);
	}

	@Override
	public Optional<List<? extends Transit>> getTransits() {
		return transit == null ? Optional.empty() : Optional.of(transit);
	}

	@Override
	public Optional<? extends List<? extends Function>> getRawExecFunctions() {
		return rawExecFunction == null ? Optional.empty() : Optional.of(rawExecFunction);
	}

	@Override
	public Optional<? extends List<? extends Function>> getRawTransitFunctions() {
		return rawTransitFunction == null ? Optional.empty() : Optional.of(rawTransitFunction);
	}
}
