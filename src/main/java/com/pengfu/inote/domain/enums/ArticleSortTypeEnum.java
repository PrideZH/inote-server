package com.pengfu.inote.domain.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ArticleSortTypeEnum implements IEnum<Integer> {

    NEW(0, "最新发布"),
    WATCH(1, "最多观看"),
    LIKE(2, "最多点赞");

    @JsonValue
    private final Integer value;
    private final String desc;

    @Override
    public Integer getValue() {
        return value;
    }

}
