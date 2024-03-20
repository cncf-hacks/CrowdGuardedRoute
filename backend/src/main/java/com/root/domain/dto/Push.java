package com.root.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Length;


public class Push {

    @Getter
    @NotBlank(message = "Customer identification value is required.")
    @Length(max = 6, min = 6, message = "The customer identification key is 6 digits.")
    private String uniqueKey; // 고객 식별값

    @Getter
    private String title;

    @Getter
    private String body;

    @JsonIgnore
    @Setter
    private String pushKey;


    @Builder
    public Push(String uniqueKey, String title, String body) {
        this.uniqueKey = uniqueKey;
        this.title = title;
        this.body = body;
    }

    @JsonIgnore
    public String getPushKey() {
        return pushKey;
    }
}
