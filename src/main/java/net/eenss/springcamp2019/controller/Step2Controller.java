package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step2Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/step2")
public class Step2Controller {

    private Step2Service service;

    public Step2Controller(Step2Service service) {
        this.service = service;
    }

    @GetMapping("/consume")
    public Mono<String> consume() {
        service.consume();
        return Mono.just("CONSUME START");
    }

    @GetMapping("/produce")
    public Mono<String> produce() {
        service.produce();
        return Mono.just("PRODUCE START");
    }
}
