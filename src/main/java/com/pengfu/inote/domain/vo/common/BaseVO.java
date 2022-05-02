package com.pengfu.inote.domain.vo.common;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseVO implements Serializable {

    protected Long id;

    protected LocalDateTime createTime;

    protected LocalDateTime updateTime;

}
