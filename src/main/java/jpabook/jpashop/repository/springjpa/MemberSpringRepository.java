package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.MemberSpring;
import jpabook.jpashop.dto.springjpa.MemberSpringDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberSpringRepository extends JpaRepository<MemberSpring, Long> {
    //기본 사용법----------------------------------------------------------
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


    //반환 타입종류----------------------------------------------------------
    //List<MemberSpring> findByUsername(String name);
    //MemberSpring findByUsername(String name);
    //Optional<MemberSpring> findByUsername(String name);

    //Spring JPA Paging 정리

    Page<MemberSpring> findByAge(int age, Pageable pageable);

    // 만약 쿼리문이 join 컬럼으로 이루어져있으면 매우 복잡하게 count 쿼리가 날라간다.
    // 사실 우리는 MemberSpring의 count만 알면 되기때문에 다음과 같이 간단하게 튜닝해서 복잡한 쿼리가 나가는것을 방지한다.
    @Query(value = "select m from MemberSpring m", countQuery = "select count(m.username) from MemberSpring m")
    Page<MemberSpring> findMemberAllCountBy(Pageable pageable);


    //top3 쌉가능
    List<MemberSpring> findTop3By();

    //Page<MemberSpring> findByUsername(String name, Pageable pageable); //Page는 count 쿼리를 포함함
    //Slice<MemberSpring> findByUsername(String name, Pageable pageable); //Slice는 count 쿼리를 날리지 않음, 또한 hasNext() 단순히 다음이 있는지 빠르게 확인가능
    //List<MemberSpring> findByUsername(String name, Pageable pageable); //count 사용안함
    //List<MemberSpring> findByUsername(String name, Sort sort);


    //벌크 수정----------------------------------------------------------
    //clearAutomatically와 flushAutomatically를 사용할 수 있다 기본값 false;
    //Jpa는 기본적으로 영속성 컨텐츠에 한번 실행한 값을 캐시로 남긴다. 다만, update와 같이 자료를 수정하는 명령문에 기존 값을 가지고 있는것은 추후 자료를 불러올때 실제 데이터와 상이할 수 있다.
    //따라서 clear() 를 시켜주고 해당 데이터를 쓸일이 생기면, 다시 조회하겠금 유도하는것이다.
    @Modifying(clearAutomatically = true)
    @Query("update MemberSpring m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    //EntityGraph----------------------------------------------------------
    //사실상 그냥 fetch join을 어노테이션으로 선언할 수 있게 하는 방법임.
    @EntityGraph(attributePaths = {"team"})
    List<MemberSpring> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from MemberSpring m")
    List<MemberSpring> findMemberSpringEntityGraph();

    //@EntityGraph(attributePaths = {"team"})
    //List<MemberSpring> findByUsername(String username);

    //Entity에 선언되어있는 @NamedEntityGraph()를 읽어서 사용도 가능하다.
    @EntityGraph("MemberSpring.all")
    @Query("select m from MemberSpring m")
    List<MemberSpring> findMemberEntityGraph();

    //QueryHints----------------------------------------------------------
    //QueryHint를 통해 readOnly 옵션을 줄 수 있다. pageable도 사용가능 기본값이 ture
    //@Transaction(readOnly=true)는 트랜잭션 커밋 시점에 flush를 하지 않기 때문에 이로 인한 dirty checking 비용이 들지 않습니다. 따라서 cpu가 절약
    //@QueryHint의 readOnly는 스냅샷을 만들지 않기 때문에, 메모리가 절약
    @QueryHints(value = @QueryHint(name= "org.hibernate.readOnly", value = "true"))
    MemberSpring findReadOnlyByUsername(String name);
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"), forCounting = true)
    Page<MemberSpring> findByUsername(String name, Pageable pageable);

    //Lock----------------------------------------------------------
    //동시성 문제를 해결하기 위해서 사용, 그냥 SQL에서의 Lock원리랑 완전 동일
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MemberSpring m where m.username = :username")
    List<MemberSpring> findByUsernameLock(@Param("username") String name);
}
