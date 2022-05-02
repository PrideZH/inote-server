package com.pengfu.inote.mapper.dynaSql;

import org.apache.ibatis.jdbc.SQL;

public class NoteDynaSqlProvider {

    public String selectPageVo(Long userId, String keyword) {
        return new SQL() {
            {
                SELECT("*");
                FROM("note");
                StringBuilder sb = new StringBuilder();
                sb.append(" user_id = ").append(userId);
                if (keyword != null) {
                    sb.append(" AND name LIKE '%").append(keyword).append("%'");
                }
                WHERE(sb.toString());
            }
        }.toString();
    }

}
