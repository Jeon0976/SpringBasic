package com.hello.core;

import com.hello.core.member.MemberRepository;
import com.hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
// @ComponentScan은 @Component가 붙은 모든 클래스를 스프링 빈으로 등록한다.
// 이때 스w프링 빈의 기본 이름은 클래스명을 사용하되 맨 앞글자만 소문자를 사용한다.
// 수동으로 등록한 AppConfig 제외하기 위한 필터링 처리
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = Configuration.class
        )
)
public class AutoAppConfig {
        // 수동 등록 빈이 우선권을 갖는다.
//        @Bean(name = "memoryMemberRepository")
//        MemberRepository memberRepository() {
//                return new MemoryMemberRepository();
//        }
}
