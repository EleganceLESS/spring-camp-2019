package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step1Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Step1Controller {

    private Step1Service service;

    public Step1Controller(Step1Service step1Service) {
        this.service = step1Service;
    }

    @GetMapping("/step1/start")
    public Mono<String> start() {
        service.consume();
        service.produce();
        return Mono.just("START");
    }

    @GetMapping("/step1/consume")
    public Mono<String> consume() {
        service.consume();
        return Mono.just("CONSUME START");
    }

    @GetMapping("/step1/produce")
    public Mono<String> produce() {
        service.produce();
        return Mono.just("PRODUCE START");
    }
}
