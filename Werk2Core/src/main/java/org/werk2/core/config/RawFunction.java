package org.werk2.core.config;

import java.util.Map;

import org.werk2.config.functions.Function;
import org.werk2.config.functions.FunctionSignature;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RawFunction {
	protected Function function;
	protected Map<FunctionSignature, RawSignature> signatures;
}
