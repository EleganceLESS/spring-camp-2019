package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.Step5Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class OldStep5Service implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(OldStep5Service.class);

    private KafkaManager kafkaManager;
    private Disposable disposable;

    public OldStep5Service(KafkaManager kafkaManager) {
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
        Step5Subscriber subscriber = new Step5Subscriber(r -> {
            logger.info("CONSUMER) [{}] {}:{}", r.offset(), r.key(), r.value());
            return Mono.delay(Duration.ofSeconds(1)).map(l -> true);
        });

        kafkaManager.consumer("topic-5")
                .subscribe(subscriber);
                //.flatMap(r -> Mono.delay(Duration.ofSeconds(1)).map(l -> r))
                //.subscribe(r -> logger.info("CONSUMER) [{}] {}:{}", r.offset(), r.key(), r.value()));

        disposable = subscriber;
    }

    @Override
    public String getTopicName() {
        return "topic-5";
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
