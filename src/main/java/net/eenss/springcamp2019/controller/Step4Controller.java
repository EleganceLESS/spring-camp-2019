package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.Step04Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/step4")
public class Step4Controller extends DemoController {

    public Step4Controller(Step04Service service) {
        super(service);
    }
}
