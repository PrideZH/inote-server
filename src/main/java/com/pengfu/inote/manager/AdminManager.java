package com.pengfu.inote.manager;

import com.pengfu.inote.domain.entity.Admin;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.mapper.AdminMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AdminManager {

    private AdminMapper adminMapper;

    /**
     * 通过 ID 获取管理员对象，若无该则抛出 NOT_FOUND 错误
     */
    public Admin getById(Long id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }
        return admin;
    }

}
