package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName
public class Folder extends BaseEntity {

    private String name;

    /** 父级文件夹 0-根目录 */
    private Long parentId;

    /** 所属用户 */
    private Long userId;

}