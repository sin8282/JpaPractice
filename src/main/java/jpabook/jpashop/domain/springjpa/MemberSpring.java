package jpabook.jpashop.domain.springjpa;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
//@NamedQuery(name = "MemberSpring.findByUsername", query = "select m from Member m where m.username = :username")
//@NamedQuery로 Repository에서 불러와 사용할 순 있지만, 그냥 SpringJPA사용하자.
//@NamedEntityGraph(name = "MemberSpring.all", attributeNodes = @NamedAttributeNode("team"))
//Repository에서 해당값을 불러와 사용가능 그냥 fetch join이나 @EntityGraph 이용하자.
public class MemberSpring {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private TeamSpring team;
    public MemberSpring(String username) {
        this(username, 0);
    }
    public MemberSpring(String username, int age) {
        this(username, age, null);
    }
    public MemberSpring(String username, int age, TeamSpring team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }
    public void changeTeam(TeamSpring team) {
        this.team = team;
        team.getMembers().add(this);
    }
}