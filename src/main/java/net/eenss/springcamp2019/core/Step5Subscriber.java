package net.eenss.springcamp2019.core;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.function.Function;

public class Step5Subscriber extends BaseSubscriber<ReceiverRecord<String, String>> {
    private static final Logger logger = LoggerFactory.getLogger(Step5Subscriber.class);

    private UnicastProcessor<ReceiverRecord<String, String>> offsetProcessor = UnicastProcessor.create();
    private FluxSink<ReceiverRecord<String, String>> offsetSink = offsetProcessor.sink();

    private Function<ReceiverRecord<String, String>, Mono<Boolean>> runner;

    public Step5Subscriber(Function<ReceiverRecord<String, String>, Mono<Boolean>> runner) {
        this.runner = runner;

        this.offsetProcessor.publish()
                .autoConnect()
                .reduce(-1L, (last, r) -> last < r.offset()
                        ? commit(r)
                        : last
                )
                .subscribe();
    }

    private long commit(ReceiverRecord<String, String> record) {
        logger.info("[COMMIT] {}", record.value());
        record.receiverOffset().acknowledge();
        return record.offset();
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        request(3);
    }

    @Override
    protected void hookOnNext(ReceiverRecord<String, String> record) {
        Mono.just(record)
                .flatMap(runner)
                .subscribe(r -> {
                    logger.info("Consume END - {}", record.value());
                    offsetSink.next(record);
                    request(1);
                });
    }
}
