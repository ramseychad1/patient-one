package com.hubaccess.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CaseNumberGenerator {

    private final AtomicLong counter = new AtomicLong(0);

    public String generate() {
        long next = counter.incrementAndGet();
        return String.format("HUB-%d-%05d", Year.now().getValue(), next);
    }

    public void setCounter(long value) {
        counter.set(value);
    }
}
