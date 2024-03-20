package com.root.service;

import lombok.RequiredArgsConstructor;
import com.root.entity.service.Member;
import com.root.repository.service.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public Member findByMemberId(Member member) {
        return memberRepository.findById(member.getUniqueKey()).orElseThrow(IllegalStateException::new);
    }

    public Member findByMemberId(String uniqueKey) {
        return memberRepository.findById(uniqueKey).orElseThrow(IllegalStateException::new);
    }
}

