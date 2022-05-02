package com.pengfu.inote.mapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * IPage<T> 转 IPage<R>
 */
public class PageMapping {

    public static <T, R> IPage<R> mapping(IPage<T> source, Function<T, R> consumer) {
        IPage<R> target = new Page<>();
        target.setTotal(source.getTotal());
        target.setPages(source.getPages());
        target.setCurrent(source.getCurrent());
        target.setSize(source.getSize());
        List<R> records = new ArrayList<>();
        for (T record : source.getRecords()) {
            R res = consumer.apply(record);
            if (res != null)
                records.add(res);
        }
        target.setRecords(records);
        return target;
    }

}
