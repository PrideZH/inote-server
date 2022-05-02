package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.dto.note.NotePatchDTO;
import com.pengfu.inote.domain.dto.note.NotePostDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.note.NoteDirVO;
import com.pengfu.inote.domain.vo.note.NoteInfoVO;
import com.pengfu.inote.service.NoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "笔记")
@AllArgsConstructor
@RestController
@RequestMapping("/api/note")
public class NoteController {

    private NoteService noteService;

    @ApiOperation(value = "当前用户添加笔记")
    @ApiResponse(code = 1001, message = "文件创建失败")
    @SaCheckLogin
    @PostMapping("/me")
    public ResultVO<NoteDirVO> postMe(@RequestBody @Validated NotePostDTO notePostDTO) throws Exception {
        notePostDTO.setUserId(Long.valueOf(StpUtil.getLoginIdAsString()));
        return ResultVO.success(noteService.add(notePostDTO));
    }

    @ApiOperation("获取当前用户未分类笔记")
    @SaCheckLogin
    @GetMapping("/me/discrete")
    public ResultVO<List<NoteDirVO>> getMeDiscrete() {
        return ResultVO.success(noteService.getMeDiscrete());
    }

    @ApiOperation("获取当前用户最近修改笔记")
    @SaCheckLogin
    @GetMapping("/me/recentness")
    public ResultVO<List<NoteDirVO>> getMeRecentness(Integer days) {
        return ResultVO.success(noteService.getMeRecentness(days));
    }

    @ApiOperation("获取指定ID的笔记")
    @SaCheckLogin
    @GetMapping("/{id:\\d+}")
    public ResultVO<NoteInfoVO> get(@PathVariable Long id) throws Exception {
        return ResultVO.success(noteService.get(id));
    }

    @ApiOperation(value = "修改笔记")
    @SaCheckLogin
    @PatchMapping("/{id:\\d+}")
    public ResultVO<NoteInfoVO> patch(@PathVariable Long id, @RequestBody @Validated NotePatchDTO notePatchDTO)
            throws Exception {
        notePatchDTO.setId(id);
        return ResultVO.success(noteService.update(notePatchDTO));
    }

    @ApiOperation(value = "删除笔记", notes = "同时删除对应的关联和文章")
    @ApiResponse(code = 1001, message = "文件删除失败")
    @SaCheckLogin
    @DeleteMapping("/{id:\\d+}")
    public ResultVO<Void> delete(@PathVariable Long id) {
        noteService.del(id);
        return ResultVO.success();
    }

}
