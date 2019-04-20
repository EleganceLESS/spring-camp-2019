package net.eenss.springcamp2019.controller;

import net.eenss.springcamp2019.service.*;
import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerTest {

    @Test
    public void startAndStopTest() {
        Step0Service service = mock(Step0Service.class);
        when(service.start()).thenReturn(Mono.just("START"));
        when(service.stop()).thenReturn(Mono.just("STOP"));

        Step0Controller controller = new Step0Controller(service);

        WebTestClient testClient = WebTestClient.bindToController(controller)
                .build();

        testClient.get().uri("/step0/start")
                .exchange()
                    .expectBody(String.class)
                    .isEqualTo("START");

        testClient.get().uri("/step0/start")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("Already Running");

        testClient.get().uri("/step0/stop")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("STOP");

        testClient.get().uri("/step0/stop")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("Not Running Now");
    }

    @Test
    public void step1ControllerTest() {
        Step1Service service = mock(Step1Service.class);
        when(service.start()).thenReturn(Mono.just("START"));

        DemoController controller = new Step1Controller(service);
        controllerTest(1, controller);
    }

    @Test
    public void step2ControllerTest() {
        Step2Service service = mock(Step2Service.class);
        when(service.start()).thenReturn(Mono.just("START"));

        DemoController controller = new Step2Controller(service);
        controllerTest(2, controller);
    }

    @Test
    public void step3ControllerTest() {
        Step3Service service = mock(Step3Service.class);
        when(service.start()).thenReturn(Mono.just("START"));

        DemoController controller = new Step3Controller(service);
        controllerTest(3, controller);
    }

    @Test
    public void step4ControllerTest() {
        Step4Service service = mock(Step4Service.class);
        when(service.start()).thenReturn(Mono.just("START"));

        DemoController controller = new Step4Controller(service);
        controllerTest(4, controller);
    }

    @Test
    public void step5ControllerTest() {
        Step5Service service = mock(Step5Service.class);
        when(service.start()).thenReturn(Mono.just("START"));

        DemoController controller = new Step5Controller(service);
        controllerTest(5, controller);
    }


    private void controllerTest(int step, DemoController controller) {
        WebTestClient testClient = WebTestClient.bindToController(controller)
                .build();

        testClient.get().uri("/step" + step + "/start")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("START");
    }
}
