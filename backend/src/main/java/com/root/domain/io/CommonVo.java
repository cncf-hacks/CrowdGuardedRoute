package com.root.domain.io;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonVo<T> {

    private final String message;

    private final T payload;

    @Builder
    private CommonVo(String message, T payload) {
        this.message = message;
        this.payload = payload;
    }
}
