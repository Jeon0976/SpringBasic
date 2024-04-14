package com.hello.core.scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.*;

public class SingletonWithPrototypeTest1 {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean prototypeBean1 = ac.getBean(PrototypeBean.class);
        prototypeBean1.addCount();
        assertThat(prototypeBean1.getCount()).isEqualTo(1);

        PrototypeBean prototypeBean2 = ac.getBean(PrototypeBean.class);
        prototypeBean2.addCount();
        assertThat(prototypeBean2.getCount()).isEqualTo(1);

        ac.close();
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac =
                new AnnotationConfigApplicationContext(
                        PrototypeBean.class,
                        ClientBean.class
                );

        ClientBean clientBean1 = ac.getBean(ClientBean.class);
        int count1 = clientBean1.logic();
        assertThat(count1).isEqualTo(1);

        ClientBean clientBean2 = ac.getBean(ClientBean.class);
        int count2 = clientBean2.logic();
        assertThat(count2).isEqualTo(1);
    }

    @Component
    static class ClientBean {

        // 무식한 방법
        // ApplicationContext를 client에서 가져와야 하는데 해당 코드는 지저분한 코드
//        @Autowired
//        ApplicationContext applicationContext;

//        private final PrototypeBean prototypeBean;

        // 호출할때 생성한다. -> lazy var 같은 느낌?
        // ObjectProvider
        // - ObjectFactory 상속, 옵션, 스트림 처리 등 편의 기능이 많고, 별도의 라이브러리 필요 없다.
        // - 하지만 스프링에 의존한다.
//        @Autowired
//        private ObjectProvider<PrototypeBean> prototypeBeanObjectProvider;

        // 싱글톤이 스프링빈에 등록될 때 prototype을 요청하고 만들어진 prototype을 싱글턴 객체 내부에서 저장하고 있기 때문에
        // 온전한 prototype 기능을 활용 할 수 없다.
//        @Autowired
//        public ClientBean(PrototypeBean prototypeBean) {
//            this.prototypeBean = prototypeBean;
//        }

        // 마지막 방법으로 JSR-330 자바 표준을 사용하는 방법이다.
        // - 스프링부트 3.0 이상은 `jakarta.inject:jakarta.inject-api:2.0.1` 라이브러리를 gradle에 추가해야 한다.
        // - `provider.get()`을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다.
        // - `provider`의 `get()`을 호출하면 내부에서는 스프링 컨테이너를 통해 해당 빈을 찾아서 반환한다. (DL)
        // - 자바 표준이고, 기능이 단순하므로 단위테스트를 만들거나 mock 코드를 만들기 훨씬 쉬어진다.
        // - `Provider`는 지금 딱 필요한 DL 정도의 기능만 제공한다.
        @Autowired
        private Provider<PrototypeBean> provider;

        public int logic() {
            // 이렇게 메서드 내부에서 만들면 되는거 아닌가? -> 무식한 방법
            // 아래 두 가지 방법의 차이는 Spring 컨테이너가 제공하는 이점이나 서비스를 이용하냐 마냐의 차이이다.
//            PrototypeBean prototypeBean = new PrototypeBean();
//            PrototypeBean prototypeBean = applicationContext.getBean(PrototypeBean.class);

//            PrototypeBean prototypeBean = prototypeBeanObjectProvider.getObject();
            PrototypeBean prototypeBean = provider.get();
            prototypeBean.addCount();

            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init = " + this);
        }

        @PreDestroy
        public void destory() {
            System.out.println("PrototypeBean.destroy = " + this);
        }
    }


}
