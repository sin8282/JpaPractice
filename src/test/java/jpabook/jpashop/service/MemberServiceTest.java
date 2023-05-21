package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Test
    public void 회원가입(){
        Member member = new Member();
        member.setName("kim");

        Long saveId = memberService.join(member);

        Assertions.assertEquals(member, memberService.findOne(member.getId()));

    }

    @Test()
    public void 중복_예외(){
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);


        IllegalStateException thrown =
                Assertions.assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        Assertions.assertEquals("already joined",thrown.getMessage());
    }

}