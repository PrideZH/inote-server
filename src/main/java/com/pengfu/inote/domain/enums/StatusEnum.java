package com.pengfu.inote.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum implements IEnum<Integer> {

    CLOSE(0, "关闭"),
    OPEN(1, "打开");

    @JsonValue
    private final Integer value;

    StatusEnum(Integer value, String desc) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}
