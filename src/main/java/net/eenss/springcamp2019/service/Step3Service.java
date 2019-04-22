package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.DelayedRepeatTenGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderRecord;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.function.Function;

@Service
public class Step3Service extends OperatorDemoService<Disposable> implements RecordProcessor, DelayedRepeatTenGenerator {

    private SomeRepository repository;

    private Disposable subDisposable;

    public Step3Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-3", kafkaManager);
        this.repository = repository;
    }

    @Override
    public Mono<String> stop() {
        dispose(subDisposable);

        return super.stop();
    }

    @Override
    protected Flux<Disposable> consumer(Flux<ReceiverRecord<String, String>> consumerFlux) {
        subDisposable = kafkaManager.consumer("step-3-2")
                .map(this::commitAndConvertToTuple)
                .groupBy(Tuple2::getT1)
                .map(groupedFlux ->
                        groupedFlux.map(Tuple2::getT2)
                                .buffer(Duration.ofSeconds(10))
                                .flatMap(list -> repository.notifyMulti(Tuples.of(list, groupedFlux.key())))
                                .flatMap(repository::saveResult)
                                .subscribe())
                .subscribe();

        return consumerFlux.map(this::commitAndConvertToInteger)
                .groupBy(Function.identity())
                .map(groupedFlux ->
                        groupedFlux.sampleFirst(Duration.ofSeconds(5))
                                .flatMap(repository::saveItem)
                                .flatMap(repository::getReceivers)
                                .flatMap(t -> kafkaManager.producer(
                                        Mono.just(SenderRecord.create(
                                                new ProducerRecord<>("step-3-2", t.getT2(), t.getT1().toString()),
                                                t.getT1().toString()
                                        ))
                                ))
                                .subscribe());
    }
}
