package com.pengfu.inote.domain.vo.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 文件目录数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DirectoryVO extends BaseVO {

    protected String dirId;

    protected String name;

    protected Long parentId;

    protected Boolean note;

}
