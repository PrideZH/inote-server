package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("role__permission")
public class RolePermission extends BaseEntity {

    private Long RoleId;

    private Long permissionId;

}
