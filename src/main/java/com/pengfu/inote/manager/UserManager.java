package com.pengfu.inote.manager;

import com.pengfu.inote.domain.entity.User;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.mapper.UserMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserManager {

    private UserMapper userMapper;

    /**
     * 通过 ID 获取用户，若无该则抛出 NOT_FOUND 错误
     */
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }
        return user;
    }

}
