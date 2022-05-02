package com.pengfu.inote.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LikeTypeEnum implements IEnum<Integer> {

    ARTICLE(0, "文章"),
    COMMENT(1, "评论"),
    REPLY(2, "回复");

    @JsonValue
    private final Integer value;

    LikeTypeEnum(Integer value, String desc) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}
