package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RandomNumberGenerator;
import net.eenss.springcamp2019.core.Step5Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;

@Service
public class Step5Service extends AbsDemoService implements RandomNumberGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Step5Service.class);

    public Step5Service(KafkaManager kafkaManager) {
        super("step-5", kafkaManager);
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        Step5Subscriber subscriber = new Step5Subscriber(r -> {
            logger.info("Consume START - {}", r.value());
            return Mono.just(true)
                    .delayElement(Duration.ofMillis(getRandom(500, 1500)));
        });

        consumerFlux.subscribe(subscriber);

        return subscriber;
    }

    @Override
    public Flux<Integer> generateSource() {
        return Flux.range(1, 100)
                .doOnNext(i -> logger.info("Create - {}", i));
    }
}
