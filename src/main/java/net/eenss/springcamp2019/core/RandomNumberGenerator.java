package net.eenss.springcamp2019.core;

import java.util.Random;

public interface RandomNumberGenerator {
    default int getRandom(int start, int end) {
        return new Random()
                .ints(start, end)
                .findFirst()
                .orElse(start);
    }
}
