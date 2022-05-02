package com.pengfu.inote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.vo.tag.TagPageVO;
import com.pengfu.inote.mapper.TagMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TagService {

    private TagMapper tagMapper;

    public IPage<TagPageVO> getList(PageDTO pageDTO) {
        return tagMapper.selectPageVo(new Page<TagPageVO>(pageDTO.getPage(), pageDTO.getSize()));
    }

}
