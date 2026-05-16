package com.mindrealm.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @className: PageDTO
 * @description: 分页数据传输对象,用于API接口返回分页数据
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
    
    /**
     * 数据列表
     */
    private List<T> records;
    
    /**
     * 当前页码
     */
    private Integer current;
    
    /**
     * 每页数量
     */
    private Integer size;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 构建分页结果
     * @param records 数据列表
     * @param current 当前页码
     * @param size 每页数量
     * @param total 总记录数
     * @return PageDTO
     */
    public static <T> PageDTO<T> of(List<T> records, Integer current, Integer size, Long total) {
        PageDTO<T> page = new PageDTO<>();
        page.setRecords(records);
        page.setCurrent(current);
        page.setSize(size);
        page.setTotal(total);
        page.setPages((int) Math.ceil((double) total / size));
        return page;
    }
}