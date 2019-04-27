package net.eenss.springcamp2019.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;

public interface RecordProcessor extends RandomNumberGenerator {
    Logger logger = LoggerFactory.getLogger(RecordProcessor.class);

    default Boolean commit(ReceiverRecord<String, String> record) {
        logger.info("Read - {}", record.value());
        record.receiverOffset().acknowledge();
        return true;
    }

    default Integer commitAndConvertToInteger(ReceiverRecord<String, String> record) {
        record.receiverOffset().acknowledge();
        return Integer.parseInt(record.value());
    }

    default Tuple2<String, Integer> commitAndConvertToTuple(ReceiverRecord<String, String> record) {
        record.receiverOffset().acknowledge();
        return Tuples.of(record.key(), Integer.parseInt(record.value()));
    }

    default Mono<Boolean> justTrue(ReceiverRecord<String, String> record) {
        logger.info("Consume START - {}", record.value());
        return Mono.just(true);
    }

    default Mono<Boolean> justTrueWithDelay(ReceiverRecord<String, String> record) {
        return justTrue(record)
                .delayElement(Duration.ofSeconds(1));
    }

    default Mono<Boolean> justTrueWithRandDelay(ReceiverRecord<String, String> record) {
        return justTrue(record)
                .delayElement(Duration.ofMillis(getRandomRange(500, 1500)));
    }
}
