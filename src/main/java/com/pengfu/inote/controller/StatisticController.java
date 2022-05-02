package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.statistic.AnalysisVO;
import com.pengfu.inote.service.StatisticService;
import com.pengfu.inote.utils.StpAdminUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "统计数据")
@AllArgsConstructor
@RestController
@RequestMapping("/api/statistic")
public class StatisticController {

    private StatisticService statisticService;

    @ApiOperation("返回分析页面数据 (admin)")
    @SaCheckPermission(value = "statistic-get", orRole = "admin", type = StpAdminUtil.TYPE)
    @GetMapping("analysis")
    public ResultVO<AnalysisVO> getAnalysis() throws Exception {
        return ResultVO.success(statisticService.getAnalysis());
    }

}
