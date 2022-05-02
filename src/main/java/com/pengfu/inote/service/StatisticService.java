package com.pengfu.inote.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.entity.User;
import com.pengfu.inote.domain.vo.statistic.AnalysisVO;
import com.pengfu.inote.mapper.ArticleMapper;
import com.pengfu.inote.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@Service
public class StatisticService {

    private UserMapper userMapper;
    private ArticleMapper articleMapper;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取近七天日期
     */
    private static List<String> getSevenDate() {
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Date date = DateUtils.addDays(new Date(), -i);
            String formatDate = sdf.format(date);
            dateList.add(formatDate);
        }
        return dateList;
    }

    /**
     * 获取前几天 00:00 的时间戳
     */
    private static long timesTamp(int count) throws ParseException {
        Date date = new Date();
        Date parse = sdf.parse(sdf.format(date));
        if (count == 0){
            return parse.getTime() + 24L * 60 * 60 * 1000;
        }
        count--;
        return parse.getTime() - count * 24L * 60 * 60 * 1000;
    }

    public AnalysisVO getAnalysis() throws Exception {
        AnalysisVO analysisVO = new AnalysisVO();

        // 总人数
        analysisVO.setTotalUser(userMapper.selectCount(null));

        // 今日登录用户量
        analysisVO.setTodayUser(userMapper.selectCount(new QueryWrapper<User>().lambda()
                .gt(User::getLoginTime, new Date(timesTamp(1)))));

        List<String> sevenDate = getSevenDate();

        // 近7天新增用户量
        List<User> newUsers = userMapper.selectList(new QueryWrapper<User>().lambda()
                .gt(User::getCreateTime, new Date(timesTamp(7))));
        TreeMap<String, Long> newUsersCount = new TreeMap<>();
        for (String date : sevenDate) {
            newUsersCount.put(date, 0L);
        }
        for (User user : newUsers) {
            String datetime = dtf.format(user.getCreateTime());
            Long count = newUsersCount.get(datetime);
            newUsersCount.put(datetime, count == null ? 1 : count + 1);
        }
        analysisVO.setNewUsers(newUsersCount);

        // 近7天活跃用户量
        List<User> activeUsers = userMapper.selectList(new QueryWrapper<User>().lambda()
                .gt(User::getLoginTime, new Date(timesTamp(7))));
        TreeMap<String, Long> activeUsersCount = new TreeMap<>();
        for (String date : sevenDate) {
            activeUsersCount.put(date, 0L);
        }
        for (User user : activeUsers) {
            String datetime = dtf.format(user.getLoginTime());
            Long count = activeUsersCount.get(datetime);
            activeUsersCount.put(datetime, count == null ? 1 : count + 1);
        }
        analysisVO.setActiveUsers(activeUsersCount);

        // 总公开笔记量
        analysisVO.setTotalOpenNote(articleMapper.selectCount(null));

        return analysisVO;
    }

}
