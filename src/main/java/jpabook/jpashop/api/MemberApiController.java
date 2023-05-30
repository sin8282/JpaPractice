package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 안좋은 방법 : v1
 * 좋은 방법 : v2
 */
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty // DTO에 따로 Validation을 관리하자
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        @NotEmpty
        private String name;
    }

}
