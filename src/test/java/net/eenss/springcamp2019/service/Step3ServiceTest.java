package net.eenss.springcamp2019.service;

import net.eenss.springcamp2019.core.KafkaManager;
import net.eenss.springcamp2019.repository.SomeRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;

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


    @Test
    public void step3ConsumerTest() {

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

        SenderResult<String> result = mock(SenderResult.class);

        KafkaManager kafkaManager = mock(KafkaManager.class);
        when(kafkaManager.producer(any())).thenReturn(Flux.just(result));

        Step3Service service = new Step3Service(kafkaManager, repository);
        StepVerifier.create(service.consumer(Flux.just(record1, record2)))
                .expectNext(result)
                .verifyComplete();
    }

    @Test
    public void samplingTest() {
        Step3Service service = new Step3Service(null, null);

        StepVerifier.withVirtualTime(() -> Flux.just(1, 1, 1, 1, 1)
                .groupBy(Function.identity())
                .flatMap(service::sampling))
                .thenAwait(Duration.ofSeconds(5))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void step3NotifyConsumerTest() {

        ReceiverOffset offset = mock(ReceiverOffset.class);
        doNothing().when(offset).acknowledge();

        ReceiverRecord<String, String> record1 = mock(ReceiverRecord.class);
        when(record1.key()).thenReturn("A");
        when(record1.value()).thenReturn("1");
        when(record1.receiverOffset()).thenReturn(offset);

        ReceiverRecord<String, String> record2 = mock(ReceiverRecord.class);
        when(record2.key()).thenReturn("B");
        when(record2.value()).thenReturn("2");
        when(record2.receiverOffset()).thenReturn(offset);

        SomeRepository repository = mock(SomeRepository.class);
        when(repository.notifyMulti(any())).thenReturn(Mono.just(Tuples.of("", true)));
        when(repository.saveResult(Tuples.of("", true))).thenReturn(Mono.just(true));

        Step3Service service = new Step3Service(null, repository);
        StepVerifier.create(service.notifyConsumer(Flux.just(record1, record2)))
                .expectNext(true, true)
                .verifyComplete();
    }

    @Test
    public void bufferingTest() {
        SomeRepository repository = mock(SomeRepository.class);
        when(repository.notifyMulti(Tuples.of(Arrays.asList(1, 2, 3, 4, 5), "A"))).thenReturn(Mono.just(Tuples.of("A", true)));
        when(repository.saveResult(Tuples.of("", true))).thenReturn(Mono.just(true));

        Step3Service service = new Step3Service(null, repository);

        StepVerifier.withVirtualTime(() -> Flux.just(
                Tuples.of("A", 1),
                Tuples.of("A", 2),
                Tuples.of("A", 3),
                Tuples.of("A", 4),
                Tuples.of("A", 5))
                .groupBy(Tuple2::getT1)
                .flatMap(service::buffering))
                .thenAwait(Duration.ofSeconds(10))
                .expectNext(Tuples.of("A", true))
                .verifyComplete();
    }
}
