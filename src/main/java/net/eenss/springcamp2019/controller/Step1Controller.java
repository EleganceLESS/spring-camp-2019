package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step01Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step1")
public class Step1Controller extends DemoController {

    public Step1Controller(Step01Service service) {
        super(service);
    }
}
