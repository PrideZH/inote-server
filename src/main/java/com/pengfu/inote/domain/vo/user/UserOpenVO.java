package com.pengfu.inote.domain.vo.user;

import com.pengfu.inote.domain.entity.User;
import com.pengfu.inote.domain.vo.common.BaseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserOpenVO extends BaseVO {

    private String username;

    private String nickname;

    private String avatarUrl;

    private String profile;

    public static UserOpenVO build(User user) {
        if (user == null) return null;

        UserOpenVO userOpenVO = new UserOpenVO();

        userOpenVO.setId(user.getId());
        userOpenVO.setCreateTime(user.getCreateTime());
        userOpenVO.setUpdateTime(user.getUpdateTime());

        userOpenVO.setUsername(user.getUsername());
        userOpenVO.setNickname(user.getNickname());
        userOpenVO.setAvatarUrl(user.getAvatarUrl());
        userOpenVO.setProfile(user.getProfile());

        return userOpenVO;
    }

}
