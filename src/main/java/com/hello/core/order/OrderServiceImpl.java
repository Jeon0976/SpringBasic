package com.hello.core.order;

import com.hello.core.discount.DiscountPolicy;
import com.hello.core.member.Member;
import com.hello.core.member.MemberRepository;
import lombok.Getter;
//import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
// final 붙는걸 확인해서 직접 생성자를 만들어준다.
//@RequiredArgsConstructor
@Getter
public class OrderServiceImpl implements OrderService {

    // 객체의 final를 주입해서 생성자에서 무조건 생성하도록 명시할 수 있다.
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(
            MemberRepository memberRepository,
            DiscountPolicy discountPolicy
    ) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    /*
     필드 주입
        - 코드가 간결해서 많은 개발자들을 유혹하지만 외부에서 변경이 불가능해서 테스트 하기 힘들다는 치명적인 단점이 있다.
        - DI 프레임워크가 없으면 아무것도 할 수 없다.
        - 애플리케이션의 실제 코드와 관계 없는 테스트 코드
        - 스프링 설정을 목적으로 하는 @Configuration 같은 곳에서만 특별한 용도로 사용
        @Autowired private MemberRepository memberRepository;
        @Autowired private DiscountPolicy discountPolicy;
    */

    // setter라 불리는 필드의 값을 변경하는 수정자 메서드를 통해서 의존관계를 주입하는 방법이다.
    // - 선택, 변경 가능성이 있는 의존관계에 사용
    // - 자바 빈 프로퍼티 규약의 수정자 메서드 방식을 사용하는 방법이다.
    // setter로 생성하면 Sping에 등록되지 않는것도 선택적으로 생성 가능 @Autowired(required = false)
//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        System.out.println("memberRepository = " + memberRepository);
//        this.memberRepository = memberRepository;
//    }
//
//    @Autowired
//    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
//        System.out.println("discountPolicy = " + discountPolicy);
//        this.discountPolicy = discountPolicy;
//    }

    // 생성자가 딱 1개만 있으면 @Autowired를 생략해도 자동 주입 된다. 물론 스프링 빈에만 해당된다.
    // 생성자와 setter 생성에 대해선 순서가 보장되지않는다.
    /*
     되도록 생성자 주입을 선택하는 것이 좋다.
     - 과거에는 수정자 주입과 필드 주입을 많이 사용했지만, 최근에는 스프링을 포함한 DI 프레임워크 대부분이 생성자 주입을 권장한다.
     **불변**
        - 대부분의 의존관계 주입은 한번 일어나면 애플리케이션 종료시점까지 의존관계를 변경할 일이 없다. 오히려 대부분의 의존관계는 애플리케이션 종료 전까지 변하면 안된다. (불변해야 한다)
        - 수정자 주입을 사용하면, setXxx 메서드를 Public으로 열여두어야 한다.
        - 누군가 실수로 변경할 수 도 있고, 변경하면 안되는 메서드를 열어두는 것은 좋은 설계 방법이 아니다.
        - 생성자 주입은 객체를 생성할 때 딱 1번만 호출되므로 이후에 호출되는 일이 없다. 따라서 불변하게 설계할 수 있다.
     **누락**
        - 프레임워크 없이 순수한 자바 코드를 단위 테스트 하는 경우에 가짜 객체를 주입해서 사용이 가능하다.
        - 수정자로 생성해도 가짜 객체를 주입할 수 있지만 테스터 입장에서 보면 어떤 의존성이 주입되는지 쉽게 확인하기가 힘들다.
        - 그러므로 생성자로 지정해서 어떤 의존성이 필요한지 필수로 보여줄 수 있게 설계하는게 좋다.
    */
//    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
//        this.memberRepository = memberRepository;
//        this.discountPolicy = discountPolicy;
//    }

    // 테스트 용도
//    public MemberRepository getMemberRepository() {
//        return memberRepository;
//    }
}
