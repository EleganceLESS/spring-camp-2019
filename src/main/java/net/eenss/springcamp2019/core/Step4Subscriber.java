package net.eenss.springcamp2019.core;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.function.Function;

public class Step4Subscriber extends BaseSubscriber<ReceiverRecord<String, String>> {
    private static final Logger logger = LoggerFactory.getLogger(Step4Subscriber.class);

    private Function<ReceiverRecord<String, String>, Mono<Boolean>> runner;

    public Step4Subscriber(Function<ReceiverRecord<String, String>, Mono<Boolean>> runner) {
        this.runner = runner;
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
                    record.receiverOffset().acknowledge();
                    request(1);
                });
    }
}
