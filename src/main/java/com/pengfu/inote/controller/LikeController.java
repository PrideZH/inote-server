package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.pengfu.inote.domain.dto.like.LikePostDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "点赞")
@AllArgsConstructor
@RestController
@RequestMapping("/api/like")
@Validated
public class LikeController {

    private LikeService likeService;

    @ApiOperation("点赞或取消")
    @SaCheckLogin
    @PostMapping("")
    public ResultVO<Void> post(@RequestBody @Validated LikePostDTO likePostDTO) {
        likeService.add(likePostDTO);
        return ResultVO.success();
    }

}
