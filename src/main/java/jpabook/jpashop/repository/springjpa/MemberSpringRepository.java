package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.MemberSpring;
import jpabook.jpashop.dto.springjpa.MemberSpringDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberSpringRepository extends JpaRepository<MemberSpring, Long> {

    //domain에 named가있는지 확인하고 우선순위로 적용한다. (MemberSpring.findByUsername) 없으면 Spring method 쿼리 생성으로 진행
    List<MemberSpring> findByUsername(@Param("username") String username);

    //JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음(매우 큰 장점!)
    @Query("select m from MemberSpring m where m.username = :username and m.age = :age")
    List<MemberSpring> findUser(@Param("username") String username, @Param("age") int age);

    //WHERE IN 절 사용가능
    @Query("select m from MemberSpring m where m.username in :names")
    List<MemberSpring> findByNames(@Param("names") List<String> names);

    @Query("select m.username from MemberSpring m")
    List<String> findUsernameList();

    @Query("select new jpabook.jpashop.dto.springjpa.MemberSpringDto(m.id, m.username, t.name) from MemberSpring m join m.team t")
    List<MemberSpringDto> findMemberSpringDto();


    //반환 타입종류
    //List<MemberSpring> findByUsername(String name);
    //MemberSpring findByUsername(String name);
    //Optional<MemberSpring> findByUsername(String name);

}
