package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step0Service extends AbsDemoService {
    private static final Logger logger = LoggerFactory.getLogger(Step0Service.class);

    public Step0Service(KafkaManager kafkaManager) {
        super("step-0", kafkaManager);
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.subscribe(r -> {
            r.receiverOffset().acknowledge();
            logger.info("Read - {}", r.value());
        });
    }

    @Override
    public Flux<Integer> generateSource() {
        return Flux.range(1, 100)
                .doOnNext(i -> logger.info("Create - {}", i));
    }
}
