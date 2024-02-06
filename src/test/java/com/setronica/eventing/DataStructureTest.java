package com.setronica.eventing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaSystemException;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStructureTest {

    private static int a = 0;
    private static int b = 0;
    private static final Object lock = new Object();

    @Test
    void multi_thread_test_without_locks() throws InterruptedException {
        int count = 100000;
        var cdl = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                a++;
                b++;
                cdl.countDown();
            });
        }
        cdl.await();
        System.out.println(a);
        System.out.println(b);
    }

    @Test
    void multi_thread_test_with_synchronized_instruction() throws InterruptedException {
        int count = 100000;
        var cdl = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                synchronized (lock) {
                    a++;
                    b++;
                }
                cdl.countDown();
            });
        }

        cdl.await();
        System.out.println(a);
        System.out.println(b);

        executor.shutdown();
    }

    @Test
    void multi_thread_test_with_atomic_integer() throws InterruptedException {
        AtomicInteger a = new AtomicInteger(0);
        AtomicInteger b = new AtomicInteger(0);
        int count = 100000;
        var cdl = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                a.incrementAndGet();
                b.incrementAndGet();
                cdl.countDown();
            });
        }

        cdl.await();
        System.out.println(a);
        System.out.println(b);

        executor.shutdown();
    }

    @Test
    void multi_thread_test_work_with_a_list() throws InterruptedException {
        List<Integer> intList = new CopyOnWriteArrayList<>();
        Random ran = new Random();
        int count = 100000;
        var cdl = new CountDownLatch(count);
        var executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < count; i++) {
            executor.execute(() -> {
                intList.add(ran.nextInt());
                cdl.countDown();
            });
        }

        cdl.await();
        System.out.println(intList.size());

        executor.shutdown();
    }

    @Test
    void check_hashCode() {
        TestEvent event1 = new TestEvent(1, "title 1");
        TestEvent event2 = new TestEvent(1, "title 2");

        HashMap<TestEvent, String> map = new HashMap<>();
        map.put(event1, "test 1");
        map.put(event2, "test 2");

        Assertions.assertEquals(2, map.size());
    }

    @Test
    void exception_chain() {
        List<Exception> exceptionList = Collections.EMPTY_LIST;
        try {
            try {
                try {
                    throw new RuntimeException("first exception");
                } catch (Exception e) {
                    throw new JpaSystemException(new RuntimeException("second exception", e));
                }
            } catch (Exception e) {
                throw new RuntimeException("third exception", e);
            }
        } catch (Exception e) {
            exceptionList = Stream.iterate(e, Objects::nonNull, Throwable::getCause)
                    .filter(Exception.class::isInstance)
                    .map(Exception.class::cast)
                    .collect(Collectors.toList());

        }

        System.out.println(exceptionList.size());
    }

    private static class TestEvent {
        private final int id;

        private final String title;

        public TestEvent(int id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
