package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.dto.reply.ReplyPostDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.reply.ReplyPageVO;
import com.pengfu.inote.service.ReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "回复")
@AllArgsConstructor
@RestController
@RequestMapping("/api/reply")
public class ReplyController {

    private ReplyService replyService;

    @ApiOperation("回复")
    @SaCheckLogin
    @PostMapping("")
    public ResultVO<ReplyPageVO> post(@RequestBody @Validated ReplyPostDTO replyPostDTO) {
        return ResultVO.success(replyService.add(replyPostDTO));
    }

    @ApiOperation("获取指定评论回复")
    @GetMapping("")
    public ResultVO<IPage<ReplyPageVO>> getByComment(@Validated PageDTO pageDTO, Long commentId) {
        return ResultVO.success(replyService.getByComment(pageDTO, commentId));
    }

    @ApiOperation("删除回复")
    @SaCheckLogin
    @DeleteMapping("/{id:\\d+}")
    public ResultVO<Void> delete(@PathVariable Long id) {
        replyService.del(id);
        return ResultVO.success();
    }

}
