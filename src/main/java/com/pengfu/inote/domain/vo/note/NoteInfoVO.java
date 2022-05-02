package com.pengfu.inote.domain.vo.note;

import com.pengfu.inote.domain.vo.article.ArticleInfoVO;
import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NoteInfoVO extends BaseVO {

    /** 笔记名 */
    private String name;

    @ToString.Exclude
    private String content;

    private ArticleInfoVO article;

}
