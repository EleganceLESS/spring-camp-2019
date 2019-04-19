package net.eenss.springcamp2019.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

public interface SomeRepository {
    Mono<Integer> saveItem(int item);
    Mono<Integer> saveItem(String item);
    Flux<Tuple2<Integer, String>> getReceivers(int itemNo);
    Mono<Tuple2<String, Boolean>> notify(Tuple2<Integer, String> target);
    Mono<Tuple2<String, Boolean>> notifyMulti(Tuple2<List<Integer>, String> target);
    Mono<Boolean> saveResult(Tuple2<String, Boolean> result);
}
