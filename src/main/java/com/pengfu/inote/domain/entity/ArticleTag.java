package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("article__tag")
public class ArticleTag extends BaseEntity {

    private Long articleId;

    private Long TagId;

}
