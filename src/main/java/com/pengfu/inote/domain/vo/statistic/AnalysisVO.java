package com.pengfu.inote.domain.vo.statistic;

import lombok.Data;

import java.util.Map;

/**
 * 后台管理系统分析页数据
 */
@Data
public class AnalysisVO {

    // 总用户量
    private Long totalUser;

    // 今日登录用户量
    private Long todayUser;

    // 近7天新增用户量
    private Map<String, Long> newUsers;

    // 近7天活跃用户量
    private Map<String, Long> activeUsers;

    // 总公开笔记量
    private Long totalOpenNote;

}
