package com.pengfu.inote.domain.vo.article;

import com.pengfu.inote.domain.vo.common.BaseVO;
import com.pengfu.inote.domain.vo.user.UserOpenVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticleOpenVO extends BaseVO {

    private UserOpenVO author;

    private String title;

    private String summary;

    private List<String> tagNames;

    private List<String> sections;

    private Long viewCount;

    private Long likeCount;

    private Boolean active;

    private Long commentCount;

}
