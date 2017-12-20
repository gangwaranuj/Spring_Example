package com.workmarket.vault.services;

import com.workmarket.common.exceptions.BadRequestException;
import com.workmarket.common.exceptions.ServiceUnavailableException;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import com.workmarket.vault.models.VaultKeyValuePair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class VaultServerServiceLoadTest extends BaseServiceIT {

	@Autowired @Qualifier("vaultServerServiceImpl") VaultServerService vaultServerService;

	@Test
	@Ignore
	public void run() throws Exception {
		int prev = 1;
		for (int i = 1; i <= 800; i += prev) {
			runMany(i+prev);
			prev = i;
		}
	}

	public void runMany(int threadCount) throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		final CountDownLatch ready = new CountDownLatch(threadCount);
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(threadCount);
		final AtomicInteger errorCount = new AtomicInteger(0);
		final List<Long> runningTimes = Collections.synchronizedList(new ArrayList<Long>());
		for (int i = 0; i < threadCount; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					ready.countDown();
					long startNanos = 0;
					try {
						start.await();
						startNanos = System.nanoTime();
						vaultServerService.post(new VaultKeyValuePair(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						errorCount.incrementAndGet();
					} catch (ServiceUnavailableException e) {
						errorCount.incrementAndGet();
					} catch (BadRequestException e) {
						errorCount.incrementAndGet();
					} finally {
						done.countDown();
						runningTimes.add((System.nanoTime() - startNanos)/1000000);
					}
				}
			});
		}
		ready.await();
		long startNanos = System.nanoTime();
		start.countDown();
		done.await();

		Collections.sort(runningTimes);
		Long sum = 0L;
		for (Long value : runningTimes) {
			sum += value;
		}
		System.out.println("Number of threads: " + threadCount);
		System.out.println("Number of errors: " + errorCount);
		System.out.println("Min run time: " + runningTimes.get(0));
		System.out.println("Max run time: " + runningTimes.get(runningTimes.size() - 1));
		System.out.println("Average run time: " + sum/runningTimes.size());
		System.out.println("Total time to complete: " + String.valueOf((System.nanoTime() - startNanos) / 1000000));
	}
}

