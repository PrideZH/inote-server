package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.pengfu.inote.domain.dto.folder.FolderPatchDTO;
import com.pengfu.inote.domain.dto.folder.FolderPostDTO;
import com.pengfu.inote.domain.dto.noteFolder.NoteFolderPatchDTO;
import com.pengfu.inote.domain.dto.noteFolder.NoteFolderPostDTO;
import com.pengfu.inote.domain.vo.common.DirectoryVO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.folder.FolderDirVO;
import com.pengfu.inote.domain.vo.note.NoteDirVO;
import com.pengfu.inote.service.FolderService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "文件夹")
@AllArgsConstructor
@RestController
@RequestMapping("/api/folder")
@Validated
public class FolderController {

    private FolderService folderService;

    @ApiOperation("创建文件夹")
    @ApiResponse(code = 1001, message = "文件夹名重复")
    @SaCheckLogin
    @PostMapping("/me")
    public ResultVO<FolderDirVO> post(@RequestBody @Validated FolderPostDTO folderPostDTO) {
        return ResultVO.success(folderService.addMe(folderPostDTO));
    }

    @ApiOperation("添加文件关联")
    @SaCheckLogin
    @PostMapping("/relevance")
    public ResultVO<NoteDirVO> postRelevance(@RequestBody @Validated NoteFolderPostDTO noteFolderPostDTO) {
        return ResultVO.success(folderService.addRelevance(noteFolderPostDTO));
    }

    @ApiOperation(value = "获取指定文件夹")
    @SaCheckLogin
    @GetMapping("/{id:\\d+}")
    public ResultVO<DirectoryVO> get(@Min(value = 1, message = "文件夹ID格式错误") @PathVariable Long id) {
        return ResultVO.success(folderService.get(id));
    }

    @ApiOperation(value = "获取当前用户指定文件目录信息", notes = "id为0表示根路径")
    @SaCheckLogin
    @GetMapping("/{id:\\d+}/me/directory")
    public ResultVO<List<DirectoryVO>> getMeDir(@Min(value = 0, message = "文件夹ID格式错误") @PathVariable Long id) {
        return ResultVO.success(folderService.getMeDir(id));
    }

    @ApiOperation(value = "修改文件夹")
    @SaCheckLogin
    @PatchMapping("/{id:\\d+}")
    public ResultVO<FolderDirVO> patch(@PathVariable Long id, @RequestBody @Validated FolderPatchDTO folderPatchDTO) {
        folderPatchDTO.setId(id);
        return ResultVO.success(folderService.update(folderPatchDTO));
    }

    @ApiOperation(value = "修改关联文件")
    @SaCheckLogin
    @PatchMapping("/relevance/{id:\\d+}")
    public ResultVO<NoteDirVO> patchRelevance(
            @PathVariable Long id, @RequestBody @Validated NoteFolderPatchDTO noteFolderPatchDTO) {
        noteFolderPatchDTO.setId(id);
        return ResultVO.success(folderService.updateRelevance(noteFolderPatchDTO));
    }

    @ApiOperation(value = "删除文件夹", notes = "同时删除所有子文件夹和关联笔记")
    @SaCheckLogin
    @DeleteMapping("/{id:\\d+}")
    public ResultVO<Void> delete(@NotNull @PathVariable Long id) {
        folderService.del(id);
        return ResultVO.success();
    }

    @ApiOperation("删除文件关联")
    @SaCheckLogin
    @DeleteMapping("/relevance/{relevanceId:\\d+}")
    public ResultVO<Void> deleteRelevance(@NotNull @PathVariable Long relevanceId) {
        folderService.delRelevance(relevanceId);
        return ResultVO.success();
    }

}
