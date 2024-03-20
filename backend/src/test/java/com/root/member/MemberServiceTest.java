package com.root.member;

import com.root.entity.service.Member;
import com.root.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Test
    void insertTest() {
        Member build = Member.builder().uniqueKey("asbcdd")
                .uuid("qwdfb")
                .pushKey("sljdflsfd")
                .build();

        memberService.saveMember(build);
    }
}
