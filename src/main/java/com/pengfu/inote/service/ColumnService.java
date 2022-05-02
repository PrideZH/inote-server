package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.column.ColumnPostDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.entity.Column;
import com.pengfu.inote.domain.vo.column.ColumnInfoPageVO;
import com.pengfu.inote.domain.vo.column.ColumnOpenVO;
import com.pengfu.inote.domain.vo.column.ColumnPageVO;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.mapper.ColumnMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ColumnService {

    private ColumnMapper columnMapper;

    public IPage<ColumnInfoPageVO> getMeList(PageDTO pageDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<Column> columnPage = columnMapper.selectPage(new Page<>(pageDTO.getPage(), pageDTO.getSize()),
                new QueryWrapper<Column>().lambda().eq(Column::getUserId, userId));
        IPage<ColumnInfoPageVO> columnInfoPageVOPage = new Page<>();
        BeanUtils.copyProperties(columnPage, columnInfoPageVOPage);
        columnInfoPageVOPage.setRecords(columnPage.getRecords().stream().map(column -> {
            ColumnInfoPageVO columnInfoPageVO = new ColumnInfoPageVO();
            BeanUtils.copyProperties(column, columnInfoPageVO);
            return columnInfoPageVO;
        }).toList());
        return columnInfoPageVOPage;
    }

    public IPage<ColumnPageVO> getList(PageDTO pageDTO, Long userId) {
        Page<Column> columnPage = columnMapper.selectPage(new Page<>(pageDTO.getPage(), pageDTO.getSize()),
                new QueryWrapper<Column>().lambda().eq(Column::getUserId, userId));
        IPage<ColumnPageVO> columnPageVOPage = new Page<>();
        BeanUtils.copyProperties(columnPage, columnPageVOPage);
        columnPageVOPage.setRecords(columnPage.getRecords().stream().map(column -> {
            ColumnPageVO columnPageVO = new ColumnPageVO();
            BeanUtils.copyProperties(column, columnPageVO);
            return columnPageVO;
        }).toList());
        return columnPageVOPage;
    }

    public ColumnOpenVO get(Long id) {
        Column column = columnMapper.selectById(id);
        if (column == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        ColumnOpenVO columnOpenVO = new ColumnOpenVO();
        BeanUtils.copyProperties(column, columnOpenVO);
        return columnOpenVO;
    }

}
