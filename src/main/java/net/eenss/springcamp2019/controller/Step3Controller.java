package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step03Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step3")
public class Step3Controller extends DemoController {

    public Step3Controller(Step03Service service) {
        super(service);
    }
}
