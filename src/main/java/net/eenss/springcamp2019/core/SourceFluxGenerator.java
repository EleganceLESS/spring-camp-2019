package net.eenss.springcamp2019.core;

import reactor.core.publisher.Flux;

public interface SourceFluxGenerator {
    Flux<Integer> generateSource();
}
