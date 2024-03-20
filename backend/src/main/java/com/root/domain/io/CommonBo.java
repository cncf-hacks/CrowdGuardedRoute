package com.root.domain.io;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonBo<T> {

    private T payload;

    public CommonBo(T payload) {
        this.payload = payload;
    }
}
