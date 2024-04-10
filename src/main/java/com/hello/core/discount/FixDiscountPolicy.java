package com.hello.core.discount;

import com.hello.core.member.Grade;
import com.hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/*
    **조회 빈이 2개 이상 문제 해결 방법**
    - @Autowired 필드 명, @Qualifier, @Primary
    1. @Autowired 필드 명 매칭
        * @Autowired는 타입 매칭을 시도하고, 이때 여러 빈이 있으면 필드 이름, 파라미터 이름으로 빈 이름을 추가 매칭한다.
        * 즉 생성할 때 필드명에 따라 같은 것을 찾아서 정상 주입한다.
        * 필드 명 매칭은 먼저 타입 매칭을 시도 하고 그 결과에 여러 빈이 있을 때 추가로 동작하는 기능이다.
    2. @Qualifier -> @Qualifier 끼리 매칭 -> 빈 이름 매칭
        * @Qualifier는 추가 구분자를 붙여주는 방법이다. 주입시 추가적인 방법을 제공하는 것이지 빈 이름을 변경하는 것은 아니다.
        * @Qualifier로 주입할 때 @Qualifer("xx")를 못 찾으면 해당 이름의 스프링 빈을 추가로 찾는다.
        * 하지만 @Qualifier는 @Qualifer를 찾는 용도로만 사용하는게 명확하고 좋다.
        * 1. @Qualifier 끼리 매칭 -> 2. 빈 이름 매칭 -> 3. `NoSuchBeanDefinitionException` 예외 발생
    3. @Primary 사용
        * @Primary는 우선순위를 정하는 방법이다. @Autowired 시에 여러 빈이 매칭되면 @Primary가 우선권을 가진다.
    - 우선순위
        * @Primary는 기본값 처럼 동작하는 것이고, @Qualifier는 매우 상세하게 동작한다.
        * 이런 경우 어떤 것이 우선권을 가져갈까? 스프링은 자동보다 수동이, 넓은 범위의 선택권 보다는 좁은 범위의 선택권이 우선 순위가 높다.
        * 따라서 여기서도 @Qualifier가 우선권이 높다.
 */

@Component
//@Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy {
    private int discountFixAmount = 1000;
    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return  discountFixAmount;
        } else {
            return 0;
        }
    }
}
