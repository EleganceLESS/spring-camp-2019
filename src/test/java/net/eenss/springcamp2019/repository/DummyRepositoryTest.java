package net.eenss.springcamp2019.repository;

import org.junit.Test;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Arrays;

public class DummyRepositoryTest {

    private SomeRepository repository = new DummyRepository();

    @Test
    public void saveItemTest() {
        StepVerifier.withVirtualTime(() -> repository.saveItem(1))
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    public void getReceiversTest() {

        StepVerifier.create(repository.getReceivers(1))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(2))
                .expectNext(Tuples.of(2, "막내"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(3))
                .expectNext(Tuples.of(3, "대리님"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(4))
                .expectNext(Tuples.of(4, "막내"))
                .expectNext(Tuples.of(4, "과장님"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(5))
                .expectNext(Tuples.of(5, "차장님"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(6))
                .expectNext(Tuples.of(6, "막내"))
                .expectNext(Tuples.of(6, "대리님"))
                .expectNext(Tuples.of(6, "부장님"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(7))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(8))
                .expectNext(Tuples.of(8, "막내"))
                .expectNext(Tuples.of(8, "과장님"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(9))
                .expectNext(Tuples.of(9, "대리님"))
                .verifyComplete();

        StepVerifier.create(repository.getReceivers(10))
                .expectNext(Tuples.of(10, "막내"))
                .expectNext(Tuples.of(10, "차장님"))
                .verifyComplete();
    }

    @Test
    public void notifyTest() {
        StepVerifier.withVirtualTime(() -> repository.notify(Tuples.of(1, "홍길동")))
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(Tuples.of("홍길동", true))
                .verifyComplete();
    }

    @Test
    public void notifyMultiTest() {
        StepVerifier.withVirtualTime(() -> repository.notifyMulti(Tuples.of(Arrays.asList(1, 2), "홍길동")))
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(Tuples.of("홍길동", true))
                .verifyComplete();
    }

    @Test
    public void saveResultTest() {
        StepVerifier.withVirtualTime(() -> repository.saveResult(Tuples.of("홍길동", true)))
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(true)
                .verifyComplete();
    }
}
