package jpabook.jpashop.domain.springjpa;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class TeamSpring {
    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "team")
    List<MemberSpring> members = new ArrayList<>();
    public TeamSpring(String name) {
        this.name = name;
    }
}