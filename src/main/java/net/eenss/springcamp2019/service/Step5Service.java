package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.HundredGenerator;
import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.core.RecordProcessor;
import net.eenss.springcamp2019.core.Step5Subscriber;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class Step5Service extends SubscriberDemoService implements RecordProcessor, HundredGenerator {

    public Step5Service(KafkaManager kafkaManager) {
        super("step-5", kafkaManager);
    }

    @Override
    protected BaseSubscriber<ReceiverRecord<String, String>> getSubscriber() {
        return new Step5Subscriber(this::justTrueWithRandDelay);
    }
}
