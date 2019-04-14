package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/step3")
public class Step3Controller {

    private Step3Service service;

    public Step3Controller(Step3Service service) {
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
