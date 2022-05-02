package com.pengfu.inote.domain.vo.common;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;

@Data
public class TokenVO {

    private String token;

    private Long timeout;

    public static TokenVO build(SaTokenInfo tokenInfo) {
        TokenVO tokenVO = new TokenVO();
        tokenVO.setToken(tokenInfo.getTokenValue());
        tokenVO.setTimeout(tokenInfo.getTokenTimeout());
        return tokenVO;
    }

}
