package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.IntegerRecordReader;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class Step3Service extends AbsDemoService implements IntegerRecordReader {
    private static final Logger logger = LoggerFactory.getLogger(Step3Service.class);

    private SomeRepository repository;

    private KafkaManager kafkaManager;
    private Disposable subDisposable;

    public Step3Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-3", kafkaManager);
        this.kafkaManager = kafkaManager;
        this.repository = repository;
    }

    @Override
    public Mono<String> stop() {
        if (subDisposable != null && !subDisposable.isDisposed()) {
            subDisposable.dispose();
        }

        return super.stop();
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        subDisposable = kafkaManager.consumer("step-3-2")
                .map(r -> {
                    r.receiverOffset().acknowledge();
                    return Tuples.of(r.key(), Integer.parseInt(r.value()));
                })
                .groupBy(Tuple2::getT1)
                .subscribe(groupedFlux ->
                        groupedFlux.map(Tuple2::getT2)
                                .buffer(Duration.ofSeconds(10))
                                .flatMap(list -> repository.notifyMulti(Tuples.of(list, groupedFlux.key())))
                                .flatMap(repository::saveResult)
                                .subscribe()
                );

        return consumerFlux.map(this::commitAndConvert)
                .groupBy(Function.identity())
                .subscribe(groupedFlux ->
                        groupedFlux.sampleFirst(Duration.ofSeconds(5))
                                .flatMap(repository::saveItem)
                                .flatMap(repository::getReceivers)
                                .flatMap(t -> kafkaManager.producer(
                                        Mono.just(SenderRecord.create(
                                                new ProducerRecord<>("step-3-2", t.getT2(), t.getT1().toString()),
                                                t.getT1().toString()
                                        ))
                                ))
                                .subscribe()
                );
    }

    @Override
    public Flux<Integer> generateSource() {
        return Flux.range(1, 100)
                .map(i -> i % 10)
                .delayElements(Duration.ofMillis(80))
                .doOnNext(i -> logger.info("Create - {}", i));
    }
}
