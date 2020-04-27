package hu.mas.core.util.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {

	private final AtomicInteger sequence;

	private final String threadNamePrefix;

	public CustomThreadFactory(String threadNamePrefix) {
		this.sequence = new AtomicInteger(1);
		this.threadNamePrefix = threadNamePrefix;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName(threadNamePrefix + "-thread-" + sequence.getAndIncrement());
		return thread;
	}

}
