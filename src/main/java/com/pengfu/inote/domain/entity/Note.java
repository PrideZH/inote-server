package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName
public class Note extends BaseEntity {

    private Long articleId;

    /** 笔记名 */
    private String name;

    /** 文件路径 */
    private String fileUrl;

    /** 所属用户 */
    private Long userId;

}