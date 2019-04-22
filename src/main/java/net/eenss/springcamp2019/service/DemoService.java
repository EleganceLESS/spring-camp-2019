package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.SourceFluxGenerator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;

public abstract class DemoService implements SourceFluxGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    KafkaManager kafkaManager;
    Disposable disposable;
    String serviceName;

    public DemoService(String serviceName, KafkaManager kafkaManager) {
        this.serviceName = serviceName;
        this.kafkaManager = kafkaManager;
    }

    public Mono<String> start() {
        consume();
        produce();
        return Mono.just("START");
    }

    public Mono<String> stop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        return Mono.just("STOP");
    }

    protected abstract void consume();

    private void produce() {
        final Flux<SenderRecord<String, String, String>> records = generateSource()
                .doOnNext(i -> logger.info("Create - {}", i))
                .map(Object::toString)
                .map(i -> SenderRecord.create(new ProducerRecord<>(serviceName, i, i), i));

        kafkaManager.producer(records)
                .subscribe();
    }
}
