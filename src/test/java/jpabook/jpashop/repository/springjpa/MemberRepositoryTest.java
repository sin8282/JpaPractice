package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.ItemSpring;
import jpabook.jpashop.domain.springjpa.MemberSpring;
import jpabook.jpashop.dto.springjpa.MemberSpringDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.InitBinder;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberSpringRepository memberRepository;
    @Autowired
    EntityManager em;

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

    @Test
    void page() {
        memberRepository.save(new MemberSpring("member1",10));
        memberRepository.save(new MemberSpring("member2",10));
        memberRepository.save(new MemberSpring("member3",10));
        memberRepository.save(new MemberSpring("member4",10));
        memberRepository.save(new MemberSpring("member5",10));

        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC, "username"));
        Page<MemberSpring> page = memberRepository.findByAge(10, pageRequest);

        List<MemberSpring> contents = page.getContent(); // 조회데이터 리스트
        assertThat(contents.size()).isEqualTo(3); //조회된 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터수
        assertThat(page.getNumber()).isEqualTo(0); //페이지번호
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        Page<MemberSpring> countTune = memberRepository.findMemberAllCountBy(pageRequest);
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터수

        List<MemberSpring> top3 = memberRepository.findTop3By();

        //페이지를 유지하면서 DTO로 전환시 map을 이용가능
        Page<MemberSpringDto> dtoPage = page.map(m -> new MemberSpringDto(m.getId(), m.getUsername(), null));

    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new MemberSpring("member1",10));
        memberRepository.save(new MemberSpring("member2",20));
        memberRepository.save(new MemberSpring("member3",30));
        memberRepository.save(new MemberSpring("member4",15));
        memberRepository.save(new MemberSpring("member5",1));

        memberRepository.bulkAgePlus(20);

        memberRepository.findAll();
    }

    @Test
    void EntityGraphTest() {
        MemberSpring member1 = new MemberSpring("member1",10);
        MemberSpring member2 = new MemberSpring("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberSpring> findMembers = memberRepository.findMemberSpringEntityGraph();

        assertThat(findMembers.get(0).getUsername()).isEqualTo("member1");
        assertThat(findMembers.get(1).getUsername()).isEqualTo("member2");

    }

    @Test
    void queryHint(){
        memberRepository.save(new MemberSpring("member1", 10));
        em.flush();
        em.clear();

        MemberSpring member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");
        em.flush();

        MemberSpring member2 = memberRepository.findReadOnlyByUsername("member2");
        assertThat(member2).isNull();
    }


    @Test
    void customRepository() {
        memberRepository.findMemberCustom();
    }

    @Test
    void jpaBaseEntity() throws InterruptedException {
        MemberSpring member = new MemberSpring("member1"    );
        memberRepository.save(member);

        Thread.sleep(1000);
        member.setUsername("member2");

        em.flush();
        em.clear();

        MemberSpring findMember = memberRepository.findById(member.getId()).get();

        System.out.println("시간1 : " + findMember.getCreateDt());
        System.out.println("시간2 : " + findMember.getUpdateDt());
        System.out.println("생성자1 : " + findMember.getCreateBy());
        System.out.println("생성자2 : " + findMember.getUpdateBy());
    }

    @Test
    void persistableTest() {
        em.persist(new ItemSpring("aa"));
    }
}