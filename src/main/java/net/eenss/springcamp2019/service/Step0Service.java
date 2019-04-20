package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.HundredGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step0Service extends DemoService implements RecordProcessor, HundredGenerator {

    public Step0Service(KafkaManager kafkaManager) {
        super("step-0", kafkaManager);
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.subscribe(this::commit);
    }
}
