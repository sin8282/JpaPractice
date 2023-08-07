package jpabook.jpashop.dto.springjpa;

import lombok.Data;

@Data
public class MemberSpringDto {
    private Long id;
    private String username;
    private String teamName;
    public MemberSpringDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
