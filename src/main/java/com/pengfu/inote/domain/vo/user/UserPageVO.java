package com.pengfu.inote.domain.vo.user;

import com.pengfu.inote.domain.entity.User;
import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserPageVO extends BaseVO {

    private String nickname;

    private String avatarUrl;

    public static UserPageVO build(User user) {
        UserPageVO userPageVO = new UserPageVO();

        userPageVO.setId(user.getId());
        userPageVO.setCreateTime(user.getCreateTime());
        userPageVO.setUpdateTime(user.getUpdateTime());

        userPageVO.setNickname(user.getNickname());
        userPageVO.setAvatarUrl(user.getAvatarUrl());

        return userPageVO;
    }

}
