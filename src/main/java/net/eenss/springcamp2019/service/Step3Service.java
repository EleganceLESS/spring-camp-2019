package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.DelayedRepeatTenGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.function.Function;

@Service
public class Step3Service extends OperatorDemoService<SenderResult<String>> implements RecordProcessor, DelayedRepeatTenGenerator {

    private SomeRepository repository;

    private Disposable notifyDisposable;
    private String notifyServiceName;

    public Step3Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-3", kafkaManager);
        this.repository = repository;
        this.notifyServiceName = "step-3-2";
    }

    @Override
    protected void consume() {
        notifyDisposable = kafkaManager.consumer(notifyServiceName)
                .compose(this::notifyConsumer)
                .subscribe();

        super.consume();
    }

    @Override
    public Mono<String> stop() {
        dispose(notifyDisposable);

        return super.stop();
    }

    @Override
    protected Flux<SenderResult<String>> consumer(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commitAndConvertToInteger)
                .groupBy(Function.identity())
                .flatMap(this::sampling)
                .flatMap(repository::saveItem)
                .flatMap(repository::getReceivers)
                .flatMap(t -> kafkaManager.producer(
                        Mono.just(SenderRecord.create(
                                new ProducerRecord<>(notifyServiceName, t.getT2(), t.getT1().toString()),
                                t.getT1().toString()
                        ))
                ));
    }

    Flux<Integer> sampling(GroupedFlux<Integer, Integer> groupedFlux) {
        return groupedFlux.sampleFirst(Duration.ofSeconds(5));
    }

    Flux<Boolean> notifyConsumer(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commitAndConvertToTuple)
                .groupBy(Tuple2::getT1)
                .flatMap(this::buffering)
                .flatMap(repository::saveResult);
    }

    Flux<Tuple2<String, Boolean>> buffering(GroupedFlux<String, Tuple2<String, Integer>> groupedFlux) {
        return groupedFlux.map(Tuple2::getT2)
                .buffer(Duration.ofSeconds(10))
                .flatMap(list -> repository.notifyMulti(Tuples.of(list, groupedFlux.key())));
    }
}
