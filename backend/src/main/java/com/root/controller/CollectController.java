package com.root.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.root.service.GeoService;
import com.root.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.root.domain.dto.Location;
import com.root.domain.dto.UserInformation;
import com.root.domain.io.CommonBo;
import com.root.domain.io.CommonVo;
import com.root.domain.type.Meta;
import com.root.entity.service.Member;
import com.root.handler.JsonHandler;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/location/collect")
public class CollectController {

    private final GeoService geoService;
    private final MemberService memberService;
    private final HashOperations<String, String, Object> hashOperations;

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonVo<String> validCheck(IllegalStateException e) {

        log.error("ValidException " , e);

        return CommonVo.<String>builder()
                .payload("")
                .message("relation user not found")
                .build();
    }

    @PostMapping("/v1/createUserDeviceInfo")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonVo<Map<String, String>> createUserDeviceInfo(
            @RequestBody @Validated CommonBo<UserInformation> userInformation
    ) {

        String relationKey = userInformation.getPayload().getRelationKey();

        if( !relationKey.isBlank() ) {
            Member byMemberId = memberService.findByMemberId(relationKey);
            Optional.ofNullable(byMemberId).orElseThrow(IllegalStateException::new);
        }

        memberService.saveMember(
                Member.builder()
                        .uniqueKey(userInformation.getPayload().getUniqueKey())
                        .uuid(userInformation.getPayload().getUuid())
                        .pushKey(userInformation.getPayload().getPushKey())
                        .relationKey(userInformation.getPayload().getRelationKey())
                        .build()
        );

        return CommonVo.<Map<String, String>>builder()
                .payload(new HashMap<>())
                .message("User initializing success")
                .build();
    }

    @PostMapping("/v1/createUserGeoInfo")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonVo<Map<String, String>> createUserGeoInfo(@RequestBody @Validated CommonBo<Location> location) {

        Location payload = location.getPayload();
        ObjectMapper objectMapper = JsonHandler.objectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Location.class);

        Object peopleHistory = hashOperations.get("NEARBY_PEOPLE_SPEED", payload.getUniqueKey());

        List<Location> historys;

        if (!Objects.isNull(peopleHistory)) {
            payload.setRegistry();
            historys = objectMapper.convertValue(peopleHistory, javaType);
            historys.add(payload);
        } else {
            payload.setRegistry();
            historys = new ArrayList<>();
            historys.add(payload);
        }

        // User information is temporarily uploaded separately and processed into buckets.
        hashOperations.put("NEARBY_PEOPLE_SPEED", payload.getUniqueKey(), historys);
        hashOperations.put("PEOPLE_REGISTER_TIME", payload.getUniqueKey(), LocalDateTime.now());
        geoService.add(Meta.PEOPLE, payload);

        return CommonVo.<Map<String, String>>builder()
                .payload(new HashMap<>())
                .message("User creating Success")
                .build();
    }

    @PostMapping("/v1/updateUserLightInfo")
    public CommonVo<String> updateUserLightInfo(@RequestBody @Validated CommonBo<Location> location) {

        Location payload = location.getPayload();
        ObjectMapper objectMapper = JsonHandler.objectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Location.class);

        Object peopleHistory = hashOperations.get("NEARBY_PEOPLE_LIGHT", payload.getUniqueKey());

        List<Location> historys;

        if (!Objects.isNull(peopleHistory)) {
            payload.setRegistry();
            historys = objectMapper.convertValue(peopleHistory, javaType);
            historys.add(payload);
        } else {
            payload.setRegistry();
            historys = new ArrayList<>();
            historys.add(payload);
        }

        // User information is temporarily uploaded separately and processed into buckets.
        hashOperations.put("NEARBY_PEOPLE_LIGHT", location.getPayload().getUniqueKey(), historys);
        hashOperations.put("PEOPLE_REGISTER_TIME", payload.getUniqueKey(), LocalDateTime.now());
        geoService.add(Meta.PEOPLE, payload);

        return CommonVo.<String>builder()
                .payload("")
                .message("User creating success")
                .build();
    }
}
