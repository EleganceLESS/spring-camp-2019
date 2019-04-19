package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step5Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step5")
public class Step5Controller extends DemoController {

    public Step5Controller(Step5Service service) {
        super(service);
    }
}
