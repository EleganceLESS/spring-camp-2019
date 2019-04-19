package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;

public interface DemoService {
    Logger logger = LoggerFactory.getLogger(DemoService.class);

    Mono<String> start();
    Mono<String> stop();

    String getTopicName();
    KafkaManager getKafkaManager();
    Flux<Integer> generateSource();

    default void produce() {
        final Flux<SenderRecord<String, String, String>> records = generateSource()
                .map(Object::toString)
                .map(i -> SenderRecord.create(new ProducerRecord<>(getTopicName(), i, i), i));

        getKafkaManager().producer(records)
                .subscribe(r -> logger.info("PRODUCER) [{}] {}", r.recordMetadata().offset(), r.correlationMetadata()));
    }
}
