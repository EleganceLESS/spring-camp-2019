package net.eenss.springcamp2019.core;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class Step4SubscriberTest {
    @Test
    public void test() {
        Step4Subscriber subscriber = new Step4Subscriber(r -> Mono.just(true));

        ReceiverOffset offset = mock(ReceiverOffset.class);
        doNothing().when(offset).acknowledge();

        ReceiverRecord<String, String> record = mock(ReceiverRecord.class);
        when(record.key()).thenReturn("1");
        when(record.value()).thenReturn("1");
        when(record.receiverOffset()).thenReturn(offset);

        subscriber.hookOnNext(record);
    }
}
