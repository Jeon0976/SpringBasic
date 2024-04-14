package com.hello.core.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

/*
 특징
 * 웹 스코프는 웹 환경에서만 동작한다.
 * 웹 스코프는 프로토타입과 다르게 스프링이 해당 스코프의 종료시점까지 관리한다.
 * 따라서 종료 메서드가 호출된다.

 종류
 * request : HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프, 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고, 관리된다.
 * session : HTTP Session과 동일한 생명주기를 가지는 스코프
 * application : 서블릿 컨텍스트(ServletContext)와 동일한 생명주기를 가지는 스코프
 * websocket : 웹 소켓과 동일한 생명주기를 가지는 스코프

 - 참고로 웹 관련 추가 설정이 추가되면 AnnotationConfigServletWebServerApplicationContext를 기반으로 애플리케이션이 구동한다.
 */


/*
 -  `proxyMode = ScopedProxyMode.TARGET_CLASS` 가 핵심이다.
 - 적용 대상이 인터페이스가 아닌 클래스이면 `TARGET_CLASS`를 선택
 - 적용 대상이 인터페이스이면 `INTERFACES`를 선택
 - 이렇게 하면 MyLogger의 가짜 프록시 클래스를 만들어두고 HTTP request와 상관 없이 가짜 프록시 클래스를 다른 빈에 미리 주입해 둘 수 있다.
 - @Scope의 proxyMode = ScopedProxyMode.TARGET_CLASS 를 설정하면 스프링 컨테이너는 CGLIB라는 바이트코드를 조작하는 라이브러리를 사용해서,
 - MyLogger를 상속받는 가짜 프록시 객체를 생성한다.

 동작 원리
 - CGLIB라는 라이브러리로 내 클래스를 상속 받는 가짜 프록시 객체를 만들어서 주입한다.
 - 이 가짜 프록시 객체는 실제 요청이 오면 그때 내부에서 실제 빈을 요청하는 위임 로직이 들어있다.
 - 가짜 프록시 객체는 실제 request scope와는 관계가 없다. 그냥 가짜이고, 내부에 단순한 위임 로직만 있고, 싱글톤 처럼 동작한다.

 특징 정리
 - 프록시 객체 덕분에 클라이언트는 마치 싱글톤 빈을 사용하듯이 편리하게 request scope를 사용할 수 있다.
 - 사실 Provider를 사용하든, 프록시를 사용하든 핵심 아이디어는 진짜 객체 조회를 꼭 필요한 시점까지 지연처리한다는 점이다.
 - 단지 애노테이션 설정 변경만으로 원본 객체를 프록시 객체로 대체할 수 있다. 이것이 바로 다형성과 DI 컨테이너가 가진 큰 장점이다.
 - 꼭 웹 스코프가 아니어도 프록시는 사용할 수 있다.

 주의점
 - 마치 싱글톤을 사용하는 것 같지만 다르게 동작하기 때문에 결국 주의해서 사용해야 한다.
 - 이런 특별한 scope는 꼭 필요한 곳에만 최소화해서 사용하는것이 좋다.

 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {
    private String uuid;
    private String requestURL;

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.println("[ " + uuid + " ]" + "[" + requestURL + "] " + message);
    }

    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString();
        System.out.println("[ " + uuid + " ] request scope bean create: " + this);
    }

    @PreDestroy
    public void close() {
        System.out.println("[ " + uuid + " ] request scope bean close: " + this);
    }
}
