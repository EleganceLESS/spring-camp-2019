package net.eenss.springcamp2019.core;

import reactor.core.publisher.Flux;

public interface HundredGenerator extends SourceFluxGenerator {
    @Override
    default Flux<Integer> generateSource() {
        return Flux.range(1, 100);
    }
}
