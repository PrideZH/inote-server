package com.pengfu.inote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.tag.TagPageVO;
import com.pengfu.inote.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "标签")
@AllArgsConstructor
@RestController
@RequestMapping("/api/tag")
public class TagController {

    private TagService tagService;

    @ApiOperation("获取按文章数量排行的标签列表")
    @GetMapping("")
    public ResultVO<IPage<TagPageVO>> getList(@Validated PageDTO pageDTO) throws Exception {
        return ResultVO.success(tagService.getList(pageDTO));
    }

}
