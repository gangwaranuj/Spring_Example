package com.workmarket.service.web;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WebRequestContextTest {
    @Test
    public void getRequestId_onIdSet_shouldHaveUniqueIdsPerThread() throws Exception {
        for (int i=0; i < 100; i++) {
            shouldHaveUniqueIdsPerThread();
        }
    }

    private void shouldHaveUniqueIdsPerThread() throws Exception {
        int threadCount = new Random().nextInt(9) + 2; // random number between 2 to 9 inclusive
        int requestCount = threadCount + 51;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        final Set<String> guids = Collections.synchronizedSet(new HashSet<String>());
        final CountDownLatch latch = new CountDownLatch(requestCount);

        for (int i=0; i < requestCount; i++) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    WebRequestContextProvider webRequestContextProvider = new WebRequestContextProvider();
                    guids.add(webRequestContextProvider.getRequestContext().getRequestId());
                    webRequestContextProvider.clear();
                    latch.countDown();
                }
            });
        }

        latch.await();
        pool.shutdown();
        assertEquals(requestCount, guids.size());
    }

}
