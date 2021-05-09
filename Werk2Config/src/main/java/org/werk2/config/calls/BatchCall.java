package org.werk2.config.calls;

import java.util.List;
import java.util.Optional;

import org.werk2.config.Documented;

/**
 * Batch call is a set of function calls and nested batch calls that are running in
 * the context of a batch.
 * Batch is processed as follows:
 * 1) Execution of SYNCHRONIZED calls one by one, synchronously;
 * 2) Launch all BLOCKING and NON_BLOCKING calls simultaneously;
 * 3) Wait for all BLOCKING calls to terminate
 * 4) Exit
 * @author jamirov
 *
 */
public interface BatchCall extends Documented {
	/**
	 * @return Function calls
	 */
    public Optional<? extends List<? extends Call>> getCalls();
    /**
     * @return Nested Batches
     */
    public Optional<? extends List<? extends BatchCall>> getBatches();
    /**
     * default SYNCHRONIZED 
     * @return concurrency level
     */
    public Optional<Concurrency> getConcurrency();
}
