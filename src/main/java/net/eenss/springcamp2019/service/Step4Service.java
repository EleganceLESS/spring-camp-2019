package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RandomNumberGenerator;
import net.eenss.springcamp2019.core.Step4Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;

@Service
public class Step4Service extends AbsDemoService implements RandomNumberGenerator {
    private static final Logger logger = LoggerFactory.getLogger(Step4Service.class);

    public Step4Service(KafkaManager kafkaManager) {
        super("step-4", kafkaManager);
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        Step4Subscriber subscriber = new Step4Subscriber(r -> {
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
