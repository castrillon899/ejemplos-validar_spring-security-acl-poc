package com.conductor.acl.poc.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGenerator {
    private static final AtomicLong lastId = new AtomicLong();

    public static long generateUniqueId() {
        long currentTime = System.nanoTime();
        long randomValue = (long) (Math.random() * Long.MAX_VALUE);
        return currentTime + randomValue + lastId.getAndIncrement();
    }
}