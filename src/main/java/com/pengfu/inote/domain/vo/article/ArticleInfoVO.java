package com.pengfu.inote.domain.vo.article;

import com.pengfu.inote.domain.enums.StatusEnum;
import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticleInfoVO extends BaseVO {

    private String title;

    private String summary;

    private String coverUrl;

    private StatusEnum status;

    private List<String> tagNames;

}
