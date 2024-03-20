package com.root.controller;

import lombok.RequiredArgsConstructor;
import com.root.domain.dto.Push;
import com.root.domain.io.CommonBo;
import com.root.domain.io.CommonVo;
import com.root.entity.service.Member;
import com.root.service.MemberService;
import com.root.service.PushService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location/push")
public class PushController {

    private final MemberService memberService;

    private final PushService pushService;

    @PostMapping
    public CommonVo<String> send(@RequestBody @Validated CommonBo<Push> push) {

        Member find = memberService.findByMemberId(
                Member.builder()
                    .uniqueKey(push.getPayload().getUniqueKey())
                    .build()
        );

        String relationKey = find.getRKey();

        if(!relationKey.isBlank()) {
            Push send = Push.builder()
                    .uniqueKey(find.getUniqueKey())
                    .title(push.getPayload().getTitle())
                    .body(push.getPayload().getBody())
                    .build();

            Member byMemberId = memberService.findByMemberId(relationKey);

            send.setPushKey(byMemberId.getPushKey());
            pushService.sendPushMessage(send);
        }

        return CommonVo.<String>builder()
                .payload("")
                .message(relationKey.isBlank() ? "relation user not found" : "push send complete")
                .build();
    }
}

