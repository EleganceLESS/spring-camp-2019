package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step1Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/step1")
public class Step1Controller extends DemoController {

    //private Step1Service service;

    public Step1Controller(Step1Service service) {
        super(service);
    }


}
