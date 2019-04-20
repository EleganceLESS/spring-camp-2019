package net.eenss.springcamp2019.service;

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

@Service
public class OldStep3Service implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(OldStep3Service.class);

    private KafkaManager kafkaManager;
    private Disposable disposable1;
    private Disposable disposable2;
    private SomeRepository repository;

    public OldStep3Service(KafkaManager kafkaManager, SomeRepository repository) {
        this.kafkaManager = kafkaManager;
        this.repository = repository;
    }

    public void consume() {
        disposable1 = kafkaManager.consumer("topic-3-1")
                .flatMap(this::recordToFirstMessage)
                .groupBy(this::groupByKey)
                .subscribe(groupedFlux ->
                    groupedFlux.sampleFirst(Duration.ofSeconds(5))
                            .flatMap(repository::saveItem)
                            .flatMap(repository::getReceivers)
                            .flatMap(t -> kafkaManager.producer(
                                Mono.just(SenderRecord.create(
                                    new ProducerRecord<>("topic-3-2", t.getT2(), t.getT1().toString()),
                                    t.getT1().toString())
                                )
                            ))
                            .subscribe()
                );

        disposable2 = kafkaManager.consumer("topic-3-2")
                .map(r -> {
                    logger.info("{} - {}", r.key(), r.value());
                    return r;
                })
                .flatMap(this::recordToSecondMessage)
                .groupBy(Tuple2::getT1)
                .subscribe(groupedFlux ->
                    groupedFlux.map(Tuple2::getT2)
                            .buffer(Duration.ofSeconds(10))
                            .flatMap(list -> repository.notifyMulti(Tuples.of(list, groupedFlux.key())))
                            .flatMap(repository::saveResult)
                            .subscribe()
                );
    }

    private Mono<String> recordToFirstMessage(ReceiverRecord<String, String> record) {
        return Mono.just(record.value());
    }

    private Mono<Tuple2<String, Integer>> recordToSecondMessage(ReceiverRecord<String, String> record) {
        return Mono.just(Tuples.of(record.key(), Integer.parseInt(record.value())));
    }

    private int groupByKey(final String s) {
        return Integer.parseInt(s) % 10;
    }


    @Override
    public Mono<String> start() {
        consume();
        produce();
        return Mono.just("START");
    }

    @Override
    public Mono<String> stop() {
        if (disposable1 != null && !disposable1.isDisposed()) {
            disposable1.dispose();
        }
        return Mono.just("STOP");
    }

    @Override
    public String getTopicName() {
        return "topic-3-1";
    }

    @Override
    public KafkaManager getKafkaManager() {
        return kafkaManager;
    }

    @Override
    public Flux<Integer> generateSource() {
        return Flux.range(1, 100)
                .delayElements(Duration.ofMillis(100));
    }
}
