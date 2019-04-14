package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DemoController {
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    private DemoService service;
    private AtomicBoolean running;

    public DemoController(DemoService service) {
        logger.info("DemoController Init");
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
