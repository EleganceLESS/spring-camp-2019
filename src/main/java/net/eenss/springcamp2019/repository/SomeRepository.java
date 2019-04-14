package net.eenss.springcamp2019.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

public interface SomeRepository {
    Mono<Integer> saveItem(String item);
    Flux<Tuple2<Integer, String>> getReceivers(int itemNo);
    Mono<Tuple3<Integer, String, Boolean>> notify(Tuple2<Integer, String> target);
    Mono<Boolean> saveResult(Tuple3<Integer, String, Boolean> result);
}
