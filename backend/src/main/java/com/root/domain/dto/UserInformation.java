package com.root.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class UserInformation {

    @NotBlank(message = "Customer identification value is required.")
    @Length(max = 6, min = 6, message = "The customer identification key is 6 digits.")
    private String uniqueKey; // 고객 식별값

    @NotBlank(message = "The user machine identification value is required.")
    private String uuid;

    @NotBlank(message = "Push Key is required.")
    private String pushKey;

    private String relationKey;

    @Builder
    private UserInformation(String uniqueKey, String uuid, String pushKey, String relationKey) {
        this.uniqueKey = uniqueKey;
        this.uuid = uuid;
        this.pushKey = pushKey;
        this.relationKey = relationKey;
    }
}
