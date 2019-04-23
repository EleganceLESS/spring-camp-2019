package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class Step0ServiceTest {

    @Test
    public void test() {
        ReceiverOffset offset = mock(ReceiverOffset.class);
        doNothing().when(offset).acknowledge();

        ReceiverRecord<String, String> record = mock(ReceiverRecord.class);
        when(record.key()).thenReturn("1");
        when(record.value()).thenReturn("1");
        when(record.receiverOffset()).thenReturn(offset);

        KafkaManager manager = mock(KafkaManager.class);
        when(manager.producer(any())).thenReturn(Flux.empty());
        when(manager.consumer("step-0")).thenReturn(Flux.just(record));

        Step0Service service = new Step0Service(manager);
        StepVerifier.create(service.start())
                .expectNext("START")
                .verifyComplete();

        StepVerifier.create(service.stop())
                .expectNext("STOP")
                .verifyComplete();
    }

    @Test
    public void consumerTest() {
        ReceiverOffset offset = mock(ReceiverOffset.class);
        doNothing().when(offset).acknowledge();

        ReceiverRecord<String, String> record1 = mock(ReceiverRecord.class);
        when(record1.key()).thenReturn("1");
        when(record1.value()).thenReturn("1");
        when(record1.receiverOffset()).thenReturn(offset);

        ReceiverRecord<String, String> record2 = mock(ReceiverRecord.class);
        when(record2.key()).thenReturn("2");
        when(record2.value()).thenReturn("2");
        when(record2.receiverOffset()).thenReturn(offset);

        Step0Service service = new Step0Service(null);
        StepVerifier.create(service.consumer(Flux.just(record1, record2)))
                .expectNext(true, true)
                .verifyComplete();
    }
}
