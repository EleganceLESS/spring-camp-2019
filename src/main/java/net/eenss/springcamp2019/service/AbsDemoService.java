package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderRecord;

public abstract class AbsDemoService {

    private String serviceName;
    private KafkaManager kafkaManager;
    private Disposable disposable;

    public AbsDemoService(String serviceName, KafkaManager kafkaManager) {
        this.serviceName = serviceName;
        this.kafkaManager = kafkaManager;
    }

    public Mono<String> start() {
        disposable = consume(kafkaManager.consumer(serviceName));
        produce();
        return Mono.just("START");
    }

    public Mono<String> stop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        return Mono.just("STOP");
    }

    protected abstract Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux);

    private void produce() {
        final Flux<SenderRecord<String, String, String>> records = generateSource()
                .map(Object::toString)
                .map(i -> SenderRecord.create(new ProducerRecord<>(serviceName, i, i), i));

        kafkaManager.producer(records)
                .subscribe();
    }

    public abstract Flux<Integer> generateSource();
}
