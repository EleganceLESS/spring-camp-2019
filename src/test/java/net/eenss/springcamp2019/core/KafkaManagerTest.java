package net.eenss.springcamp2019.core;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;
import reactor.test.StepVerifier;

import java.util.Objects;

public class KafkaManagerTest {

    @Test
    public void embeddedKafkaTest() {
        KafkaManager manager = new KafkaManager();

        StepVerifier.create(manager.producer(Flux.range(1, 5)
                        .map(Objects::toString)
                        .map(i -> SenderRecord.create(new ProducerRecord<>("test", i, i), i))))
                .expectNextMatches(r -> r.correlationMetadata().equals("1"))
                .expectNextMatches(r -> r.correlationMetadata().equals("2"))
                .expectNextCount(3)
                .verifyComplete();

        StepVerifier.create(manager.consumer("test").take(5))
                .expectNextMatches(r -> r.value().equals("1"))
                .expectNextMatches(r -> r.value().equals("2"))
                .expectNextMatches(r -> r.value().equals("3"))
                .expectNextMatches(r -> r.value().equals("4"))
                .expectNextMatches(r -> r.value().equals("5"))
                .verifyComplete();
    }
}
