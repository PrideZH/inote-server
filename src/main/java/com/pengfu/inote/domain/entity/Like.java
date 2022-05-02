package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pengfu.inote.domain.enums.LikeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("b_like")
public class Like extends BaseEntity {

    private Long targetId;

    private LikeTypeEnum type;

    private Long userId;

}