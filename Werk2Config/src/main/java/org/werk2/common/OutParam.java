package org.werk2.common;

public class OutParam<T> {
    protected T value = null;

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}
}