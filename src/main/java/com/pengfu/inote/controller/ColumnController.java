package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.vo.column.ColumnInfoPageVO;
import com.pengfu.inote.domain.vo.column.ColumnOpenVO;
import com.pengfu.inote.domain.vo.column.ColumnPageVO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.service.ColumnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@Api(tags = "专栏")
@AllArgsConstructor
@RestController
@RequestMapping("/api/column")
@Validated
public class ColumnController {

    private ColumnService columnService;

    @ApiOperation("获取当前用户所有专栏信息")
    @SaCheckLogin
    @GetMapping("/me")
    public ResultVO<IPage<ColumnInfoPageVO>> getMeList(@Validated PageDTO pageDTO) {
        return ResultVO.success(columnService.getMeList(pageDTO));
    }

    @ApiOperation("获取专栏列表")
    @GetMapping("")
    public ResultVO<IPage<ColumnPageVO>> getList(@Validated PageDTO pageDTO, Long userId) {
        return ResultVO.success(columnService.getList(pageDTO, userId));
    }

    @ApiOperation(value = "获取指定ID专栏", notes = "文章列表不包含被关闭的文章。")
    @GetMapping("/{id:\\d+}")
    public ResultVO<ColumnOpenVO> get(@Min(value = 0, message = "ID格式错误") @PathVariable Long id) {
        return ResultVO.success(columnService.get(id));
    }

}
