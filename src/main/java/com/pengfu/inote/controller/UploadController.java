package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.manager.FileManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Api(tags = "上传")
@AllArgsConstructor
@RestController
@RequestMapping("/api/upload")
@Validated
public class UploadController {

    private FileManager fileManager;

    @ApiOperation("上传用户头像")
    @ApiResponse(code = 1001, message = "图片大小超过500KB")
    @SaCheckLogin
    @PostMapping("/avatar")
    public ResultVO<String> postAvatar(
            @NotNull(message = "缺少 file") @RequestParam("file") MultipartFile file) throws Exception {
        return ResultVO.success(fileManager.uploadAvatar(file));
    }

    @ApiOperation(value = "上传文章封面", notes = "图片大小不能超过5MB")
    @ApiResponse(code = 1001, message = "图片大小超过5MB")
    @SaCheckLogin
    @PostMapping("/cover")
    public ResultVO<String> postCover(
            @NotNull(message = "缺少 file") @RequestParam("file") MultipartFile file) throws Exception {
        return ResultVO.success(fileManager.uploadCover(file));
    }

}
