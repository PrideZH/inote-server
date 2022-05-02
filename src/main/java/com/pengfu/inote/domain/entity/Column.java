package com.pengfu.inote.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Column extends BaseEntity {

    private Long userId;

    private String name;

    private String desc;

}
