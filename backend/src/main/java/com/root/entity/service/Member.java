package com.root.entity.service;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    private String uniqueKey;

    private String uuid;

    private String pushKey;

    // Self Join
    private String rKey;

    @Builder
    private Member(String uniqueKey, String uuid, String pushKey, String relationKey ) {
        this.uniqueKey = uniqueKey;
        this.uuid = uuid;
        this.pushKey = pushKey;
        this.rKey = relationKey;
    }
}
