package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.HundredGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step0Service extends OperatorDemoService<Boolean> implements RecordProcessor, HundredGenerator {

    public Step0Service(KafkaManager kafkaManager) {
        super("step-0", kafkaManager);
    }

    @Override
    protected Flux<Boolean> consumer(Flux<ReceiverRecord<String, String>> consumerFlux) {
        return consumerFlux.map(this::commit);
    }
}
