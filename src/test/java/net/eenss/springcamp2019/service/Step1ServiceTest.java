package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.repository.SomeRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import static org.mockito.Mockito.*;

public class Step1ServiceTest {

    @Test
    public void step1ConsumerTest() {
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

        SomeRepository repository = mock(SomeRepository.class);
        when(repository.saveItem(1)).thenReturn(Mono.just(1));
        when(repository.getReceivers(1)).thenReturn(Flux.empty());

        when(repository.saveItem(2)).thenReturn(Mono.just(2));
        when(repository.getReceivers(2)).thenReturn(Flux.just(Tuples.of(2, "A")));
        when(repository.notify(Tuples.of(2, "A"))).thenReturn(Mono.just(Tuples.of("A", true)));
        when(repository.saveResult(Tuples.of("A", true))).thenReturn(Mono.just(true));

        Step1Service service = new Step1Service(null, repository);
        StepVerifier.create(service.consumer(Flux.just(record1, record2)))
                .expectNext(true)
                .verifyComplete();
    }
}
