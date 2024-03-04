package com.conductor.acl.poc.service;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private static final AtomicLong lastId = new AtomicLong();

    public static long generateUniqueId() {
        long currentTime = System.nanoTime();
        long randomValue = (long) (Math.random() * Long.MAX_VALUE);
        return currentTime + randomValue + lastId.getAndIncrement();
    }
}