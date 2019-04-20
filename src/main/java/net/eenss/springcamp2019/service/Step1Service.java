package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.DelayedRepeatTenGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step1Service extends DemoService implements RecordProcessor, DelayedRepeatTenGenerator {

    private SomeRepository repository;

    public Step1Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-1", kafkaManager);
        this.repository = repository;
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commitAndConvertToInteger)
                .flatMap(repository::saveItem)
                .flatMap(repository::getReceivers)
                .flatMap(repository::notify)
                .flatMap(repository::saveResult)
                .subscribe();
    }
}
