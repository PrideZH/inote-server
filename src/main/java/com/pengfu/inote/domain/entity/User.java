package com.pengfu.inote.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName
public class User extends BaseEntity {

    private String username;

    private String password;

    private String nickname;

    private String avatarUrl;

    private String profile;

    /** 是否删除 */
    private Boolean delFlg;

    private LocalDateTime loginTime;

}
