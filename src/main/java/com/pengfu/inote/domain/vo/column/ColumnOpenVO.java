package com.pengfu.inote.domain.vo.column;

import com.pengfu.inote.domain.vo.article.ArticlePageVO;
import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ColumnOpenVO extends BaseVO {

    private String name;

    private String desc;

    private List<ArticlePageVO> articles;

}
