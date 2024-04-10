package com.hello.core.autowiredTest;

import com.hello.core.AutoAppConfig;
import com.hello.core.discount.DiscountPolicy;
import com.hello.core.member.Grade;
import com.hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class AllBeanTest {

    @Test
    void findAllBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);


        DiscountService discountService = ac.getBean(DiscountService.class);

        Member member = new Member(1L, "userA", Grade.VIP);

        int fixDiscountPrice = discountService.discount(member, 10000, "fixDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(fixDiscountPrice).isEqualTo(1000);

        int rateDiscountPrice = discountService.discount(member, 20000, "rateDiscountPolicy");

        assertThat(discountService).isInstanceOf(DiscountService.class);
        assertThat(rateDiscountPrice).isEqualTo(2000);

    }

    /*
        - 비즈니스 로직은 거의 대부분 자동 등록으로 하지만 다형성을 적극 활용할 때는 수동으로 등록해주는 것이 좋다.
        - `DiscountService`가 의존관계 자동 주입으로 `Map<String, DiscountPolicy>`에 주입으 받는 상황을 생각해보자
        - 여기에 어떤 빈들이 주입될 지, 각 빈들의 이름은 무엇일지 코드만 보고 한번에 쉽게 파악할 수 있을까? 내가 개발했으니 크게 관계가 없지만,
        - 만약 이 코드를 다른 개발자가 개발해서 나에게 준 것이라면 어떨까?
        - 자동 등록을 사용하고 있기 때문에 파악하려면 여러 코드를 찾아봐야 한다.
        - 이런 경우 수동 빈으로 등록하거나 또는 자동으로하면 특정 패키지에 같이 묶어두는게 좋다.
        ``` java
            @configuration
            public class DiscountPolicyConfig {
                @Bean
                public DiscountPolicy rateDiscountPolicy() {
                    return new RateDiscountPolicy();
                }

                @Bean
                public DiscountPolicy fixDiscountPolicy() {
                    return new FixDiscountPolicy();
                }
            }
        ```
    */
    @Service
    static class DiscountService {
        private final Map<String, DiscountPolicy> policyMap;

        @Autowired
        public DiscountService(Map<String, DiscountPolicy> policyMap) {
            System.out.println("policyMap = " + policyMap);

            this.policyMap = policyMap;
        }

        public int discount(Member member, int price, String discountCode) {
            DiscountPolicy discountPolicy = policyMap.get(discountCode);

            System.out.println("discountPolicy = " + discountPolicy);
            System.out.println("discountCode = " + discountCode);

            return discountPolicy.discount(member, price);
        }
    }
}
