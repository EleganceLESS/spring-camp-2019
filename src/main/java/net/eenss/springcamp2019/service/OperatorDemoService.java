package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.SourceFluxGenerator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderRecord;

public abstract class OperatorDemoService<T> extends DemoService {

    public OperatorDemoService(String serviceName, KafkaManager kafkaManager) {
        super(serviceName, kafkaManager);
    }

    protected abstract Flux<T> consumer(Flux<ReceiverRecord<String, String>> consumerFlux);

    @Override
    protected void consume() {
        disposable = kafkaManager.consumer(serviceName)
                .compose(this::consumer)
                .subscribe();
    }
}
