package com.pengfu.inote.domain.vo.article;

import com.pengfu.inote.domain.vo.common.BaseVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticlePageVO extends BaseVO {

    private Long noteId;

    private String title;

    private String summary;

    private String coverUrl;

    private List<String> tagNames;

    private Long userId;

    private UserPageVO author;

    private Long likeCount;

}
