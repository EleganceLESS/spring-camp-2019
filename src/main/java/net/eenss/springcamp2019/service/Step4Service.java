package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.HundredGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.core.Step4Subscriber;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step4Service extends DemoService implements RecordProcessor, HundredGenerator {

    public Step4Service(KafkaManager kafkaManager) {
        super("step-4", kafkaManager);
    }

    @Override
    protected Disposable consume(Flux<ReceiverRecord<String, String>> consumerFlux) {
        Step4Subscriber subscriber = new Step4Subscriber(this::justTrueWithDelay);

        consumerFlux.subscribe(subscriber);

        return subscriber;
    }
}
