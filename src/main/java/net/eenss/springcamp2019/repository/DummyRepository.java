package net.eenss.springcamp2019.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Random;

@Component
public class DummyRepository implements SomeRepository {
    private static final Logger logger = LoggerFactory.getLogger(DummyRepository.class);

    private Flux<Tuple2<Integer, String>> receiverRule;

    public DummyRepository() {
        this.receiverRule = Flux.just(
                Tuples.of(3, "A"),
                Tuples.of(5, "B"),
                Tuples.of(7, "C"),
                Tuples.of(11, "D"),
                Tuples.of(13, "E")
        );
    }

    @Override
    public Mono<Integer> saveItem(String item) {
        final int delay = getRandomMilliSeconds();
        logger.info("Delay Times {} for saving Item - {}", delay, item);

        return Mono.delay(Duration.ofMillis(delay))
                .flatMap(l -> Mono.just(Integer.parseInt(item)));
    }

    @Override
    public Flux<Tuple2<Integer, String>> getReceivers(final int itemNo) {
        return receiverRule.filter(t -> itemNo % t.getT1() == 0)
                .map(t -> Tuples.of(itemNo, t.getT2()));
    }

    @Override
    public Mono<Tuple3<Integer, String, Boolean>> notify(Tuple2<Integer, String> notifyTarget) {
        final int delay = getRandomMilliSeconds();
        logger.info("Delay Times {} for notify Item - {}", delay, notifyTarget);

        return Mono.delay(Duration.ofMillis(delay))
                .map(t -> Tuples.of(notifyTarget.getT1(), notifyTarget.getT2(), true));
    }

    @Override
    public Mono<Boolean> saveResult(Tuple3<Integer, String, Boolean> result) {
        final int delay = getRandomMilliSeconds();
        logger.info("Delay Times {} for saving Result - {}", delay, result);

        return Mono.delay(Duration.ofMillis(delay))
                .map(t -> true);
    }

    private int getRandomMilliSeconds() {
        return new Random().ints(1000, 5000).findFirst().orElse(0);
    }
}
