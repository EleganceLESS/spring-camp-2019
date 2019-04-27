package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step2Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step2")
public class Step2Controller extends DemoController {

    public Step2Controller(Step2Service service) {
        super(service);
    }
}
