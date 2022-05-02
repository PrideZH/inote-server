package com.pengfu.inote.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SortEnum implements IEnum<Integer> {

    NEW(0, "最新"),
    HOT(1, "最热");

    @JsonValue
    private final Integer value;

    SortEnum(Integer value, String desc) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}