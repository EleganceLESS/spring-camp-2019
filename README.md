# 개요

이 프로젝트는 Spring Camp 2019의 세션 중 하나인 **'정확하고, 우아하게! Reactive를 품은 Kafka 메시지'** 에서 DEMO를 진행하기 위해 만들어 졌습니다.

슬라이드에서 소개했던 코드가 실제로는 어떻게 작성되는지 보고, 어떤 흐름으로 진행되는지 직접 체험해 보면 더욱 많은 도움이 될 수 있을 것으로 기대됩니다.

# 설치 및 실행

1. git clone을 통해 프로젝트를 내려받습니다.
```
$ git clone https://github.com/EleganceLESS/spring-camp-2019.git
```
2. 프로젝트 디렉토리로 이동한 후, Maven을 이용해 프로젝트를 빌드하고 실행합니다.
```
$ mvn spring-boot:run
```
3. curl 또는 Postman 등의 HTTP Client 도구를 사용하여 명령을 전달합니다.
```
$ curl http://localhost:8080/step0/start
$ curl http://localhost:8080/step0/stop
```
4. 프로세스의 표준 출력 또는 `/tmp/reactor-kafka.log` 파일의 로그로 진행 상황을 확인 할 수 있습니다.

# Dependency

- `spring-boot-starter-webflux`
    - DEMO는 HTTP를 통한 명령으로 차례대로 진행됩니다.
    - 간단한 웹 애플리케이션을 만들기 위해 Spring WebFlux를 사용하였습니다.
- `reactor-kafka`
    - 본 세션에서 주로 다루게 될 라이브러리입니다. 
    - Apache Kafka의 Reactive Driver 이며, 많은 API들을 Mono 또는 Flux로 제공하고 있습니다.
- `spring-kafka-test`
    - 이 프로젝트에서는 Embedded Kafka를 사용하고 있습니다. 
    - 일반적으로는 Kafka 인스턴스를 직접 설치하고 ZooKeeper와 Kafka를 모두 실행해야 하지만, 배포와 실행 편의를 위해 Embedded Kafka를 선택하였습니다.
    - 프로세스가 시작되면 Kafka도 함께 가동됩니다. 프로세스가 종료되면 데이터는 모두 사라집니다.
    - Embedded Kafka는 보통 테스트를 위해 사용되기에 pom.xml의 Dependency 규칙에 scope를 `test`로 설정하는 것이 대부분이나, 여기서는 실행 시점에도 사용되어야 하므로 해당 설정은 제외하였습니다.
- `spring-boot-starter-test`
    - Spring Boot의 테스트 도구입니다. JUnit과 Mockito 등을 테스트 코드에서 사용하고 있습니다.
- `reactor-test`
    - Reactor로 구현된 코드를 테스트 할 수 있는 도구입니다. 
    - Mono나 Flux를 반환하는 함수를 `StepVerifier` 로 검증하며, 특히 시간을 다루는 부분에서 `withVirtualTime()` 을 사용하고 있습니다.

# 프로젝트 구조

- controller
    - DemoController
        - 모든 Controller에서 상속받는 추상 클래스입니다. 
        - start / stop 요청이 들어오면 각 Step의 Service에 명령을 전달합니다.
    - StepNController
        - Step 별로 RequestMapping을 하기 위한 Controller입니다. 
        - 0부터 5까지 준비되어 있습니다.
- service
    - DemoService
        - 모든 Service에서 상속받는 추상 클래스입니다. 
        - `start` 메소드는 Kafka Topic에 대해 consume과 produce를 시작하게 합니다.
        - `stop` 메소드는 Kafka Topic에 대한 consume을 중단시킵니다.
        - `produce` 메소드는 Kafka 메시지를 생성하여 각 Topic에 발행합니다.
        - `consume` 메소드는 추상 메소드이고, Topic에 대한 구독과 처리 방법을 하위 클래스에서 구현해야 합니다.
    - OperatorDemoService
        - DemoService를 상속받고, Step0 ~ 3에서 상속받는 추상 클래스입니다.
        - consume 메소드에서 각 Step별로 지정된 Topic을 구독하고, 각 레코드의 Flux를 consumer 메소드와 합성하는 작업을 수행합니다.
        - `consumer` 메소드는 추상 메소드이고, 각 메시지의 처리 방법을 하위 클래스에서 구현해야 합니다.
    - SubscriberDemoService
        - DemoService를 상속받고, Step4 ~ 5에서 상속받는 추상 클래스입니다.
        - consume 메소드에서 각 Step별로 지정된 Topic을 구독하되, 구독 시 적용 할 Subscriber는 하위 클래스에서 `getSubscriber` 메소드를 통해 구현하여야 합니다.
    - StepNService
        - Step 별로 Topic 구독 및 레코드 사용 방법을 정의합니다.
- repository
    - SomeRepository
        - 외부 의존성(File, DB, Rest API 등의 외부 Read/Write가 필요한 작업)이 걸린 메소드를 정의하였습니다.
    - DummyRepository
        - SomeReposiory 인터페이스의 구현체로, 대부분 input 값 그대로 output을 돌려주거나, 고정된 값을 반환합니다.
        - 외부 시스템에 대한 접근을 가정하는 메소드들은 0.5 ~ 3초 사이의 지연시간을 적용하여 값을 반환합니다.
        - `Flux<Tuple2<Integer, String>> getReceivers(int itemNo)`
            - 주어진 itemNo에 대한 담당자들을 Flux로 반환하는 함수입니다.
            - 이 예제에서는 5명의 담당자가 존재하고, 각 담당자는 아래의 아이템들을 처리합니다.
                - 조조 : itemNo가 2의 배수
                - 유비 : itemNo가 3의 배수
                - 손권 : itemNo가 4의 배수
                - 원소 : itemNo가 5의 배수
                - 여포 : itemNo가 6의 배수
            - 만약 itemNo가 6인 경우, 담당자는 **[조조, 유비, 여포]** 가 됩니다.
- core
    - KafkaManager
        - Embedded Kafka 인스턴스를 생성합니다.
        - Consumer와 Producer의 옵션 객체를 생성합니다.
        - `consumer` 메소드를 통해 Kafka Topic에서 메시지를 구독하고,
        - `producer` 메소드를 통해 Kafka Topic에 메시지를 발행합니다.
    - SourceFluxGenerator
        - 각 Step별로 발행 할 메시지의 생성 규칙을 정의합니다.
        - HundredGenerator
            - 1부터 100까지 숫자를 차례대로 생성합니다.
        - DelayedRepeatTenGenerator
            - 1부터 10까지 숫자를 차례대로 10번 생성합니다. 0.08초 간격으로 생성됩니다.
    - RandomNumberGenerator
        - 숫자를 무작위로 생성하는 메소드가 제공됩니다.
        - 이 예제에서는 주어진 범위에 속하는 임의의 수를 반환하는 함수만 만들었습니다.
    - RecordProcessor
        - Topic에서 받아온 Record를 다루는 메소드가 제공됩니다.
        - 대부분 commit 후 형변환을 하거나, 처리 중 지연시간을 생성하는 과정을 수행합니다.
    - StepNSubscriber
        - Step N에서 사용할 Subscriber를 정의합니다. BaseSubscriber를 상속받아 구현하였습니다.

# Step 별 동작

1. Step 0
    - 1부터 100까지의 숫자를 차례대로 Kafka로 발행합니다.
    - Consumer는 받아온 수를 즉시 출력합니다.
2. Step 1
    - 1부터 10까지의 숫자를 10번 반복하여 총 100개의 숫자를 차례대로 Kafka로 발행합니다.
    - Consumer는 모든 수에 대해 **"감지"** 메시지를 출력하고,
    - 각 숫자에 대해 통지를 해야 할 인물을 가져 온 후,
    - 각 인물과 숫자의 **"알림"** 메시지를 출력합니다.
    - 처리가 완료되면 해당 인물의 **"발송 이력 저장"** 메시지를 출력합니다.
3. Step 2
    - 1부터 10까지의 숫자를 10번 반복하여 총 100개의 숫자를 차례대로 Kafka로 발행합니다.
    - Consumer는 각 숫자 별로 5초의 기간 동안 가장 먼저 들어온 항목에 대해서만 **"감지"** 메시지를 출력하고,
    - 그 숫자에 대해 통지를 해야 할 인물을 가져 온 후,
    - 각 인물과 숫자의 **"알림"** 메시지를 출력합니다.
    - 처리가 완료되면 해당 인물의 **"발송 이력 저장"** 메시지를 출력합니다.
4. Step 3
    - 1부터 10까지의 숫자를 10번 반복하여 총 100개의 숫자를 차례대로 Kafka로 발행합니다.
    - Consumer는 각 숫자 별로 5초의 기간 동안 가장 먼저 들어온 항목에 대해서만 **"감지"** 메시지를 출력하고,
    - 그 숫자에 대해 통지를 해야 할 인물을 가져 온 후,
    - 각 인물과 숫자 정보를 Kafka로 발행합니다.
    - 두 번째 Consumer는 각 인물별로 10초의 기간 동안 들어오는 모든 숫자 정보를 모으다가 제한 시간이 도래하면,
    - 각 인물과 숫자 목록의 **"알림"** 메시지를 출력합니다.
    - 처리가 완료되면 해당 인물의 **"발송 이력 저장"** 메시지를 출력합니다.
5. Step 4
    - 1부터 100까지의 숫자를 차례대로 Kafka로 발행합니다.
    - Consumer는 아이템을 최대 3개까지만 동시에 처리합니다.
    - 각 아이템은 1초의 처리 시간이 걸립니다.
6. Step 5
    - 1부터 100까지의 숫자를 차례대로 Kafka로 발행합니다.
    - Consumer는 아이템을 최대 5개까지만 동시에 처리합니다.
    - 각 아이템은 0.5초에서 1.5초 사이의 처리 시간이 걸립니다.
    - 각 레코드의 오프셋이 증가하는 경우에만 commit을 수행합니다.

## Q&A
- 문의사항이 있으시면 본 프로젝트의 Issue에 글을 남겨주시면 답변을 드리도록 하겠습니다.
- 만약 질문 내용에 공개하기 어려운 부분이 있으시다면 alchemistless@gmail.com 으로 메일을 보내주시길 바랍니다.
- 감사합니다.