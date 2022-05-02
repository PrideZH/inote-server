package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.article.ArticlePatchDTO;
import com.pengfu.inote.domain.dto.article.ArticlePostDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.enums.ArticleSortTypeEnum;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.article.ArticleInfoVO;
import com.pengfu.inote.domain.vo.article.ArticleOpenVO;
import com.pengfu.inote.domain.vo.article.ArticlePageVO;
import com.pengfu.inote.handler.NotControllerResLog;
import com.pengfu.inote.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "文章")
@AllArgsConstructor
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private ArticleService articleService;

    @ApiOperation("上传文章")
    @ApiResponse(code = 1001, message = "文章已上传")
    @SaCheckLogin
    @PostMapping("")
    public ResultVO<ArticleInfoVO> post(@RequestBody @Validated ArticlePostDTO articlePostDTO) throws Exception {
        articlePostDTO.setUserId(Long.valueOf(StpUtil.getLoginIdAsString()));
        return ResultVO.success(articleService.add(articlePostDTO));
    }

    @ApiOperation("获取当前用户文章列表")
    @GetMapping("/me")
    public ResultVO<IPage<ArticlePageVO>> listMe(@Validated PageDTO pageDTO, ArticleSortTypeEnum sortTypeEnum) {
        return ResultVO.success(articleService.getMeList(pageDTO, sortTypeEnum));
    }

    @ApiOperation("获取当前用户指定文章信息")
    @GetMapping("/{id:\\d+}/me")
    public ResultVO<ArticleInfoVO> getMe(@PathVariable Long id) {
        return ResultVO.success(articleService.getMe(id));
    }

    @ApiOperation("获取指定ID文章")
    @GetMapping("/{id:\\d+}")
    public ResultVO<ArticleOpenVO> get(@PathVariable Long id) throws Exception {
        return ResultVO.success(articleService.get(id));
    }

    @NotControllerResLog
    @ApiOperation("获取指定ID文章的指定章节内容")
    @GetMapping("/{id:\\d+}/content")
    public ResultVO<String> getContent(@PathVariable Long id, String section) throws Exception {
        return ResultVO.success(articleService.getContent(id, section));
    }

    @ApiOperation("获取文章列表")
    @GetMapping("")
    public ResultVO<IPage<ArticlePageVO>> list(
            @Validated PageDTO pageDTO,
            Long userId,
            @RequestParam(value = "keywords", required = false) List<String> keywords,
            @RequestParam(value = "tags", required = false) List<String> tags,
            ArticleSortTypeEnum sortTypeEnum) {
        return ResultVO.success(articleService.getList(pageDTO, userId, keywords, tags, sortTypeEnum));
    }

    @ApiOperation(value = "更新文章")
    @SaCheckLogin
    @PatchMapping("/{id:\\d+}")
    public ResultVO<ArticleInfoVO> patch(@PathVariable Long id,
                                         @RequestBody @Validated ArticlePatchDTO articlePatchDTO) throws Exception {
        articlePatchDTO.setId(id);
        return ResultVO.success(articleService.update(articlePatchDTO));
    }

    @ApiOperation("删除文章")
    @SaCheckLogin
    @DeleteMapping("/{id:\\d+}")
    public ResultVO<Void> delete(@PathVariable Long id) {
        articleService.del(id);
        return ResultVO.success();
    }

}

