package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.Step4Subscriber;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.function.Function;

@Service
public class Step4Service implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(Step4Service.class);

    private KafkaManager kafkaManager;
    private Disposable disposable;

    public Step4Service(KafkaManager kafkaManager) {
        this.kafkaManager = kafkaManager;
    }

    @Override
    public Mono<String> start() {
        consume();
        produce();
        return Mono.just("START");
    }

    @Override
    public Mono<String> stop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        return Mono.just("STOP");
    }

    public void consume() {
        Step4Subscriber subscriber = new Step4Subscriber(r -> {
            logger.info("CONSUMER) [{}] {}:{}", r.offset(), r.key(), r.value());
            return Mono.delay(Duration.ofSeconds(1)).map(l -> true);
        });

        kafkaManager.consumer("topic-4")
                .subscribe(subscriber);
                //.flatMap(r -> Mono.delay(Duration.ofSeconds(1)).map(l -> r))
                //.subscribe(r -> logger.info("CONSUMER) [{}] {}:{}", r.offset(), r.key(), r.value()));

        disposable = subscriber;
    }

    @Override
    public String getTopicName() {
        return "topic-4";
    }

    @Override
    public KafkaManager getKafkaManager() {
        return kafkaManager;
    }

    @Override
    public Flux<Integer> generateSource() {
        return Flux.range(1, 100);
    }
}
