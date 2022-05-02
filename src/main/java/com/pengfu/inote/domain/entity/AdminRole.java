package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("admin__role")
public class AdminRole extends BaseEntity {

    private Long AdminId;

    private Long roleId;

}
