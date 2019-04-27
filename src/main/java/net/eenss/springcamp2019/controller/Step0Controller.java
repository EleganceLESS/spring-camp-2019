package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step0Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step0")
public class Step0Controller extends DemoController {

    public Step0Controller(Step0Service service) {
        super(service);
    }
}
