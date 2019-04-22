package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.DelayedRepeatTenGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.function.Function;

@Service
public class Step2Service extends OperatorDemoService<Flux<Boolean>> implements RecordProcessor, DelayedRepeatTenGenerator {

    private SomeRepository repository;

    public Step2Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-2", kafkaManager);
        this.repository = repository;
    }

    @Override
    protected Flux<Flux<Boolean>> consumer(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commitAndConvertToInteger)
                .groupBy(Function.identity())
                .map(this::sampling)
                .doOnNext(Flux::subscribe);
    }

    protected Flux<Boolean> sampling(GroupedFlux<Integer, Integer> groupedFlux) {
        return groupedFlux.sampleFirst(Duration.ofSeconds(5))
                .flatMap(repository::saveItem)
                .flatMap(repository::getReceivers)
                .flatMap(repository::notify)
                .flatMap(repository::saveResult);
    }
}
