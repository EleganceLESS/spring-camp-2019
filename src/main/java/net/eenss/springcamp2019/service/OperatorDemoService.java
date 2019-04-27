package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

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
