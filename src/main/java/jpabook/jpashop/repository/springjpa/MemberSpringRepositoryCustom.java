package jpabook.jpashop.repository.springjpa;

import jpabook.jpashop.domain.springjpa.MemberSpring;

import java.util.List;

public interface MemberSpringRepositoryCustom {
    List<MemberSpring> findMemberCustom();
}
