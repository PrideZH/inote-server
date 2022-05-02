package com.pengfu.inote.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.entity.Folder;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.mapper.FolderMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FolderManager {

    private FolderMapper folderMapper;

    /**
     * 通过 ID 检测文件夹是否存在，若不存在抛出 NOT_FOUND 错误
     */
    public void checkExist(Long id) {
        getById(id);
    }

    /**
     * 通过 ID 获取文件夹，若无该则抛出 NOT_FOUND 错误
     */
    public Folder getById(Long id) {
        Folder folder = folderMapper.selectOne(new QueryWrapper<Folder>().lambda().eq(Folder::getId, id));
        if (folder == null) {
            throw new ServiceException(ResultCode.NOT_FOUND, "文件夹资源不存在");
        }
        return folder;
    }

}
