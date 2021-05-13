package org.werk2.core.config;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RawSignature {
	protected Method method;
	protected ReturnType retType;
}
