package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.MemberSpring;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberSpringRepository memberRepository;

    @Test
    public void testMember() {
        MemberSpring member = new MemberSpring("memberA");
        MemberSpring saveMember = memberRepository.save(member);

        MemberSpring findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCURD() {
        MemberSpring member1 = new MemberSpring("member1");
        MemberSpring member2 = new MemberSpring("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회
        MemberSpring findMember1 = memberRepository.findById(member1.getId()).get();
        MemberSpring findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<MemberSpring> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    void queryMember(){
        MemberSpring member1 = new MemberSpring("member1",10);
        MemberSpring member2 = new MemberSpring("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //findByUsername으로 Spring JPA가 알아서 매서드 이름보고 매핑해줌
        List<MemberSpring> findMember1 = memberRepository.findByUsername("member1");
        assertThat(findMember1.get(0).getUsername()).isEqualTo(member1.getUsername());
        assertThat(findMember1.size()).isEqualTo(1);


        List<MemberSpring> findMember2 = memberRepository.findByNames(Arrays.asList(new String[]{"member1", "member2"}));
        assertThat(findMember2.size()).isEqualTo(2);


        List<MemberSpring> findMember3 = memberRepository.findUser("member1",10 );
        assertThat(findMember3.get(0)).isEqualTo(member1);

    }
}