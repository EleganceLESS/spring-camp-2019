package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class Step0Service implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(Step0Service.class);

    private KafkaManager kafkaManager;
    private Disposable disposable;

    public Step0Service(KafkaManager kafkaManager) {
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
        disposable = kafkaManager.consumer("topic-0")
                .doOnSubscribe(s -> logger.info("Consumer doOnSubscribe"))
                .doOnCancel(() -> logger.info("Consumer doOnCancel"))
                .doOnComplete(() -> logger.info("Consumer doOnComplete"))
                .subscribe(r -> logger.info("CONSUMER) [{}] {}:{}", r.offset(), r.key(), r.value()));
    }

    @Override
    public String getTopicName() {
        return "topic-0";
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
