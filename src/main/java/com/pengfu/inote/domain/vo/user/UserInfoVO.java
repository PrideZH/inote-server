package com.pengfu.inote.domain.vo.user;

import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserInfoVO extends BaseVO {

    private String username;

    private String nickname;

    private String avatarUrl;

    private String profile;

}
