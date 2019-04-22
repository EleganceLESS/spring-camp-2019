package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.DelayedRepeatTenGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.function.Function;

@Service
public class Step2Service extends OperatorDemoService<Disposable> implements RecordProcessor, DelayedRepeatTenGenerator {

    private SomeRepository repository;

    public Step2Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-2", kafkaManager);
        this.repository = repository;
    }

    @Override
    protected Flux<Disposable> consumer(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commitAndConvertToInteger)
                .groupBy(Function.identity())
                .map(groupedFlux ->
                        groupedFlux.sampleFirst(Duration.ofSeconds(5))
                                .flatMap(repository::saveItem)
                                .flatMap(repository::getReceivers)
                                .flatMap(repository::notify)
                                .flatMap(repository::saveResult)
                                .subscribe());
    }

}
