package com.pengfu.inote.domain.vo.tag;

import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TagPageVO extends BaseVO {

    private String name;

    private Long articleCount;

}
