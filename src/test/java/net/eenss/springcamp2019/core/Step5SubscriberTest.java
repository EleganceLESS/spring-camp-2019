package net.eenss.springcamp2019.core;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

import static org.mockito.Mockito.*;

public class Step5SubscriberTest {
    @Test
    public void test() {
        Step5Subscriber subscriber = new Step5Subscriber(r -> Mono.just(true));

        ReceiverOffset offset = mock(ReceiverOffset.class);
        doNothing().when(offset).acknowledge();

        ReceiverRecord<String, String> record = mock(ReceiverRecord.class);
        when(record.key()).thenReturn("1");
        when(record.value()).thenReturn("1");
        when(record.offset()).thenReturn(0L);
        when(record.receiverOffset()).thenReturn(offset);

        subscriber.hookOnNext(record);
    }
}
