package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.springjpa.MemberSpring;
import jpabook.jpashop.repository.springjpa.MemberSpringRepository;
import jpabook.jpashop.service.MemberService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){

        if(result.hasErrors()){
            return "members/createMemberForm";
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }


    //SpringJPA 사용법
    private final MemberSpringRepository memberSpringRepository;

    //도메인 컨버터
/*    @ResponseBody
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        MemberSpring member = memberSpringRepository.findById(id).get();
        return member.getUsername();
    }*/

    //위에 pathVariable을 이용하여 id를 불러오는 방식은 아래처럼 대체 가능합니다.
    @ResponseBody
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") MemberSpring member) {
        return member.getUsername();
    }

    //페이징과 정렬
    // /members/pager?page=0&size=3&sort=id,desc&sort=username,desc
    //@PageableDefault로 application.yml에 설정한 global 설정과 같이 할 수 있음
    @ResponseBody
    @GetMapping("/members/pager")
    public Page<MemberSpring> list( /*@PageableDefault(size=5, sort = "username", direction = Sort.Direction.DESC)*/ Pageable pageable) {
        Page<MemberSpring> page = memberSpringRepository.findAll(pageable);
        return page;
    }

    //테스트값
    @PostConstruct
    private void memberInsertData() {
        for(int i=0; i<100; i++) {
            MemberSpring memberSpring = new MemberSpring("유저 "+i,i    );
            memberSpringRepository.save(memberSpring);
        }
    }
}
