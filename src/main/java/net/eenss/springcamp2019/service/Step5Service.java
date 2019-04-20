package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.HundredGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.core.Step5Subscriber;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step5Service extends DemoService implements RecordProcessor, HundredGenerator {

    public Step5Service(KafkaManager kafkaManager) {
        super("step-5", kafkaManager);
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        Step5Subscriber subscriber = new Step5Subscriber(this::justTrueWithDelay);

        consumerFlux.subscribe(subscriber);

        return subscriber;
    }
}
