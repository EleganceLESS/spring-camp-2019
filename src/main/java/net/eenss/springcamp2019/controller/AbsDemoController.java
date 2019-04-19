package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.AbsDemoService;
import net.eenss.springcamp2019.service.Step03Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/abs")
public class AbsDemoController {
    private AbsDemoService service;
    private AtomicBoolean running;

    public AbsDemoController(Step03Service service) {
        this.service = service;
        this.running = new AtomicBoolean(false);
    }

    @GetMapping("/start")
    public Mono<String> start() {
        return running.compareAndSet(false, true)
                ? service.start()
                : Mono.just("Already Running");
    }

    @GetMapping("/stop")
    public Mono<String> stop() {
        return running.compareAndSet(true, false)
                ? service.stop()
                : Mono.just("Not Running Now");
    }
}
