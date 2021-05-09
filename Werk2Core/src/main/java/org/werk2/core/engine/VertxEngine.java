package org.werk2.core.engine;

import java.util.Optional;

import org.werk2.config.engine.Engine;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class VertxEngine {
	//Engine parameters
	public static final String PRM_THREAD_COUNT = "THREAD_COUNT";
	protected Optional<Integer> threadCount = Optional.empty();
	
	protected Vertx vertx;

	public void init(Engine conf) {
		//conf.getParameters()
		//TODO: init thread count
	}
	
	public void start() {
		if (threadCount.isEmpty())
			vertx = Vertx.vertx();
		else
			vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(threadCount.get()));
	}
	
	public void stop() {
		vertx.close();
	}
}
