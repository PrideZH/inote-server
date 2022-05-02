package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.comment.CommentPostDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.enums.SortEnum;
import com.pengfu.inote.domain.vo.comment.CommentOutlineVO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Api(tags = "评论")
@AllArgsConstructor
@RestController
@RequestMapping("/api/comment")
@Validated
public class CommentController {

    private CommentService commentService;

    @ApiOperation("评论")
    @SaCheckLogin
    @PostMapping("")
    public ResultVO<CommentOutlineVO> post(@RequestBody @Validated CommentPostDTO commentPostDTO) {
        return ResultVO.success(commentService.add(commentPostDTO));
    }

    @ApiOperation("获取指定文章评论")
    @GetMapping("")
    public ResultVO<IPage<CommentOutlineVO>> getByArticle(
            @Validated PageDTO pageDTO,
            @NotNull Long articleId,
            @ApiParam(value = "默认HOT") SortEnum sort) {
        if (sort == null) sort = SortEnum.HOT;
        return ResultVO.success(commentService.getByArticle(pageDTO, articleId, sort));
    }

    @ApiOperation("删除评论")
    @SaCheckLogin
    @DeleteMapping("/{id:\\d+}")
    public ResultVO<Void> delete(@PathVariable Long id) {
        commentService.del(id);
        return ResultVO.success();
    }

}
