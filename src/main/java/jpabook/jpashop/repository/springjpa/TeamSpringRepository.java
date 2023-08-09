package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.TeamSpring;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamSpringRepository extends JpaRepository<TeamSpring, Long> {
}
