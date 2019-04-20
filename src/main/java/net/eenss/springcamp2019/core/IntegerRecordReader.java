package net.eenss.springcamp2019.core;

import reactor.kafka.receiver.ReceiverRecord;

public interface IntegerRecordReader {
    default Integer commitAndConvert(ReceiverRecord<String, String> record) {
        record.receiverOffset().acknowledge();
        return Integer.parseInt(record.value());
    }
}
