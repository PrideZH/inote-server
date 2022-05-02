package com.pengfu.inote.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.entity.Note;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.mapper.NoteMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NoteManager {

    private NoteMapper noteMapper;

    /**
     * 通过 ID 检测文件是否存在，若不存在抛出 NOT_FOUND 错误
     */
    public void checkExist(Long id) {
        getById(id);
    }

    /**
     * 通过 ID 获取笔记，若无该则抛出 NOT_FOUND 错误
     */
    public Note getById(Long id) {
        Note note = noteMapper.selectOne(new QueryWrapper<Note>().lambda().eq(Note::getId, id));
        if (note == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }
        return note;
    }

}
