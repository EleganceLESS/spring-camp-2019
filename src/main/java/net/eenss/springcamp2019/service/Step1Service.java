package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.IntegerRecordReader;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;

@Service
public class Step1Service extends AbsDemoService implements IntegerRecordReader {
    private static final Logger logger = LoggerFactory.getLogger(Step1Service.class);

    private SomeRepository repository;

    public Step1Service(KafkaManager kafkaManager, SomeRepository repository) {
        super("step-1", kafkaManager);
        this.repository = repository;
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commitAndConvert)
                .flatMap(repository::saveItem)
                .flatMap(repository::getReceivers)
                .flatMap(repository::notify)
                .flatMap(repository::saveResult)
                .subscribe();
    }

    @Override
    public Flux<Integer> generateSource() {
        return Flux.range(1, 100)
                .map(i -> i % 10)
                .delayElements(Duration.ofMillis(80))
                .doOnNext(i -> logger.info("Create - {}", i));
    }
}
