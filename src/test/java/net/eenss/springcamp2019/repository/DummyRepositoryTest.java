package net.eenss.springcamp2019.repository;

import org.junit.Test;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

public class DummyRepositoryTest {

    @Test
    public void getReceiversTest() {
        SomeRepository repository = new DummyRepository();

        StepVerifier.create(repository.getReceivers(1))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(2))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(3))
                .expectNext(Tuples.of(3, "A"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(4))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(5))
                .expectNext(Tuples.of(5, "B"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(15))
                .expectNext(Tuples.of(15, "A"))
                .expectNext(Tuples.of(15, "B"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(33))
                .expectNext(Tuples.of(33, "A"))
                .expectNext(Tuples.of(33, "D"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(35))
                .expectNext(Tuples.of(35, "B"))
                .expectNext(Tuples.of(35, "C"))
                .verifyComplete();

    }
}
