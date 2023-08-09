package jpabook.jpashop.repository.springjpa.impl;

import jpabook.jpashop.domain.springjpa.MemberSpring;
import jpabook.jpashop.repository.springjpa.MemberSpringRepositoryCustom;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberSpringRepositoryImpl implements MemberSpringRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<MemberSpring> findMemberCustom() {
        return em.createQuery("select m from MemberSpring m").getResultList();
    }
}
