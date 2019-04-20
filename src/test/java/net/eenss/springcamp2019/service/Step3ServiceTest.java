package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class Step3ServiceTest {

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
        when(manager.consumer("step-3")).thenReturn(Flux.just(record));
        when(manager.consumer("step-3-2")).thenReturn(Flux.just(record));

        SomeRepository repository = mock(SomeRepository.class);
        when(repository.saveItem(1)).thenReturn(Mono.just(1));
        when(repository.getReceivers(1)).thenReturn(Flux.just(Tuples.of(1, "A")));
        when(repository.notifyMulti(any())).thenReturn(Mono.empty());

        Step3Service service = new Step3Service(manager, repository);
        StepVerifier.create(service.start())
                .expectNext("START")
                .verifyComplete();

        StepVerifier.create(service.stop())
                .expectNext("STOP")
                .verifyComplete();
    }
}
