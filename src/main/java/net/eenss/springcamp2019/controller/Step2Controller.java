package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step02Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step2")
public class Step2Controller extends DemoController {

    public Step2Controller(Step02Service service) {
        super(service);
    }
}
