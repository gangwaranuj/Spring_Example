package com.workmarket.service.infra.business;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import rx.functions.Action1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A way to register things to run around the times the transaction commits.
 */
@Component
public class CommitHook extends TransactionSynchronizationAdapter {
	private static final Logger logger = LoggerFactory.getLogger(CommitHook.class);
	private static final ThreadLocal<List<Action1<Boolean>>> PRECOMMIT_RUNNABLES =
		new ThreadLocal<List<Action1<Boolean>>>();
	private static final ThreadLocal<List<Action1<Integer>>> ONCOMPLETE_RUNNABLES =
		new ThreadLocal<List<Action1<Integer>>>();
	private final Meter exceptionMeter;
	private AtomicBoolean registered = new AtomicBoolean(false);

	@Autowired
	CommitHook(final MetricRegistry registry) {
		this.exceptionMeter = registry.meter("commithook.afterfail");
	}

	/**
	 * Runs before the transaction commits.  If any of them throw, it will cause the whole transaction to fail.
	 *
	 * @param runnable an @{link Action1} that takes a boolean stating whether or not it was a write.
	 */
	void executePreCommit(final Action1<Boolean> runnable) {
		logger.info("Submitting new runnable {} to run before commit", runnable);
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			logger.info("Transaction synchronization is NOT ACTIVE. Executing runnable right now {}", runnable);
			runnable.call(true); // assume it's a write, can't guess anything else really
			return;
		}
		getPreCommitRunnables().add(runnable);
	}

	/**
	 * Runs after the transaction commits.  If a runnable throws, it will be logged, but the rest will continue
	 * to run.
	 *
	 * @param runnable an @{link Action1} that takes an integer from {@link TransactionSychronization} indicating commit
	 *                 state -- whether it committed or rolled back.
	 */
	void executeOnComplete(final Action1<Integer> runnable) {
		logger.info("Submitting new runnable {} to run on complete", runnable);
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			logger.info("Transaction synchronization is NOT ACTIVE. Executing runnable right now {}", runnable);
			runnable.call(STATUS_COMMITTED); // assume it committed, can't do much else
			return;
		}
		getOnCompleteRunnables().add(runnable);
	}


	private List<Action1<Boolean>> getPreCommitRunnables() {
		return getRunnables(PRECOMMIT_RUNNABLES);
	}

	private List<Action1<Integer>> getOnCompleteRunnables() {
		return getRunnables(ONCOMPLETE_RUNNABLES);
	}

	private <T> List<Action1<T>> getRunnables(ThreadLocal<List<Action1<T>>> runnables) {
		if (runnables.get() == null) {
			runnables.set(new ArrayList<Action1<T>>());
			registerWithManager();
		}
		return runnables.get();
	}

	private void registerWithManager() {
		if (!registered.compareAndSet(false, true)) {
			TransactionSynchronizationManager.registerSynchronization(this);
		}
	}

	@Override
	public void beforeCommit(final boolean wasWrite) {
		final List<Action1<Boolean>> threadRunnables = PRECOMMIT_RUNNABLES.get();
		if (threadRunnables == null) {
			return;
		}
		logger.info("Transaction just about to commit, executing {} runnables", threadRunnables.size());

		for (final Action1<Boolean> runnable : threadRunnables) {
			logger.info("Executing runnable {}", runnable);
			runnable.call(wasWrite); // Don't catch exceptions.  If we blow up we want to fail.
		}
	}

	@Override
	public void afterCompletion(final int status) {
		logger.info("Transaction completed with status {}", status == STATUS_COMMITTED ? "COMMITTED" : "ROLLED_BACK");

		final List<Action1<Integer>> threadRunnables = ONCOMPLETE_RUNNABLES.get();
		if (threadRunnables == null) {
			return;
		}
		logger.info("executing {} runnables", threadRunnables.size());

		for (final Action1<Integer> runnable : threadRunnables) {
			logger.info("Executing runnable {}", runnable);
			try {
				runnable.call(status);
			} catch (final Exception e) {
				logger.error("failed executing action after completion with completion status " + status, e);
				exceptionMeter.mark();
			}
		}

		ONCOMPLETE_RUNNABLES.remove();
		PRECOMMIT_RUNNABLES.remove();
	}
}
