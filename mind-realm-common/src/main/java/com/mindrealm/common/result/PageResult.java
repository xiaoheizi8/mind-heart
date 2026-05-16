package com.mindrealm.common.result;

import java.io.Serializable;
import java.util.List;

/**
 * @className: PageResult
 * @description: 分页结果包装类,封装分页查询结果
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.2 22:31
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private long current;

    /**
     * 每页数量
     */
    private long size;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 私有构造函数
     */
    private PageResult() {
    }

    /**
     * 构建分页结果
     * @param records 数据列表
     * @param current 当前页码
     * @param size 每页数量
     * @param total 总记录数
     * @return PageResult
     */
    public static <T> PageResult<T> of(List<T> records, long current, long size, long total) {
        PageResult<T> result = new PageResult<>();
        result.records = records;
        result.current = current;
        result.size = size;
        result.total = total;
        result.pages = (total + size - 1) / size;
        return result;
    }

    /**
     * 判断是否有上一页
     * @return 是否有上一页
     */
    public boolean hasPrevious() {
        return current > 1;
    }

    /**
     * 判断是否有下一页
     * @return 是否有下一页
     */
    public boolean hasNext() {
        return current < pages;
    }

    // Getters and Setters
    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
