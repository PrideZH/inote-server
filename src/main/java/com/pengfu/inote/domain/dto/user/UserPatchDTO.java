package com.pengfu.inote.domain.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserPatchDTO {

    @ApiModelProperty(hidden = true)
    private Long id;

    @Length(min = 8, max = 16, message = "密码长度必须在8到16位")
    private String password;

    @Length(min = 2, max = 16, message = "昵称长度必须在2到16位")
    private String nickname;

    private String avatarUrl;

    private String profile;

}
