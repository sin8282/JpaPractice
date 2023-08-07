package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.MemberSpring;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSpringRepository extends JpaRepository<MemberSpring, Long> {
}
