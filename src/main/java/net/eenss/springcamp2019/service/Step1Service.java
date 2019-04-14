package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.configure.KafkaConfigure;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

@Service
public class Step1Service {
    private static final Logger logger = LoggerFactory.getLogger(Step1Service.class);

    private KafkaConfigure configure;

    public Step1Service(KafkaConfigure configure) {
        this.configure = configure;
    }

    public void consume() {
        configure.consumer("topic-1")
                .subscribe(r -> logger.info("CONSUMER) [{}] {}:{}", r.offset(), r.key(), r.value()));
    }

    public void produce() {
        final Flux<SenderRecord<String, String, String>> records = Flux.range(1, 100)
                .map(Object::toString)
                .map(i -> SenderRecord.create(new ProducerRecord<>("topic-1", i, i), i));

        configure.producer(records)
                .subscribe(r -> logger.info("PRODUCER) [{}] {}", r.recordMetadata().offset(), r.correlationMetadata()));
    }
}
