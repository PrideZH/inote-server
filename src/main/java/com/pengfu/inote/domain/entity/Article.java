package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pengfu.inote.domain.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName
public class Article extends BaseEntity {

    /** 所属用户 */
    private Long userId;

    /** 文章名 */
    private String title;

    /** 文章介绍 */
    private String summary;

    /** 文件内容 */
    private byte[] content;

    /** 封面 */
    private String coverUrl;

    /** 状态 */
    private StatusEnum status;

    /** 点赞数 */
    private Long likeCount;

    /** 点击量 */
    private Long hits;

}
