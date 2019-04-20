package net.eenss.springcamp2019.repository;

import net.eenss.springcamp2019.core.RandomNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.List;

@Component
public class DummyRepository implements SomeRepository, RandomNumberGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DummyRepository.class);

    private Flux<Tuple2<Integer, String>> receiverRule;

    public DummyRepository() {
        this.receiverRule = Flux.just(
                Tuples.of(2, "막내"),
                Tuples.of(3, "대리님"),
                Tuples.of(4, "과장님"),
                Tuples.of(5, "차장님"),
                Tuples.of(6, "부장님")
        );
    }

    @Override
    public Mono<Integer> saveItem(int item) {
        final int delay = getRandomMilliSeconds();

        return Mono.just(item)
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(i -> logger.info("이벤트 [{}]의 발생 감지!", item));
    }

    @Override
    public Flux<Tuple2<Integer, String>> getReceivers(final int itemNo) {
        return receiverRule.filter(t -> itemNo % t.getT1() == 0)
                .map(t -> Tuples.of(itemNo, t.getT2()));
    }

    @Override
    public Mono<Tuple2<String, Boolean>> notify(Tuple2<Integer, String> notifyTarget) {
        final int delay = getRandomMilliSeconds();

        return Mono.just(Tuples.of(notifyTarget.getT2(), true))
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(t -> logger.info("[{}] 에게 이벤트 [{}] 발생을 알림!", notifyTarget.getT2(), notifyTarget.getT1()));
    }

    @Override
    public Mono<Tuple2<String, Boolean>> notifyMulti(Tuple2<List<Integer>, String> notifyTarget) {
        final int delay = getRandomMilliSeconds();

        return Mono.just(Tuples.of(notifyTarget.getT2(), true))
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(t -> logger.info("[{}] 에게 이벤트 {} 발생을 알림!", notifyTarget.getT2(), notifyTarget.getT1()));
    }

    @Override
    public Mono<Boolean> saveResult(Tuple2<String, Boolean> result) {
        final int delay = getRandomMilliSeconds();

        return Mono.just(true)
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(b -> logger.info("[{}] 에게 통지한 이력을 저장!", result.getT1()));
    }

    private int getRandomMilliSeconds() {
        return getRandomRange(500, 3000);
    }
}
