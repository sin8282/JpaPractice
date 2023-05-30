package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //@NotEmpty 엔티티 단위에서는 Validation 사용안하는게 좋다 하고 싶다면 DTO를 만들어서 해라
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();


}
