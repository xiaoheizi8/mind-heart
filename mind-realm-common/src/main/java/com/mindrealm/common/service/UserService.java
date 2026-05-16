package com.mindrealm.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.User;

import java.util.List;

/**
 * @className: UserService
 * @description: 用户服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
public interface UserService {

    /**
     * 根据用户ID查询用户信息
     * @param id 用户ID
     * @return 用户对象,不存在返回null
     */
    User findById(Long id);

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象,不存在返回null
     */
    User findByUsername(String username);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户对象,不存在返回null
     */
    User findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象,不存在返回null
     */
    User findByEmail(String email);

    /**
     * 根据登录标识查询用户（支持用户名/手机号/邮箱）
     * @param loginId 登录标识
     * @return 用户对象,不存在返回null
     */
    User findByLoginId(String loginId);

    /**
     * 注册新用户
     * @param user 用户对象
     * @return 注册成功返回true,用户名已存在返回false
     */
    boolean register(User user);

    /**
     * 创建用户
     * @param user 用户对象
     * @return 创建成功返回true
     */
    boolean createUser(User user);

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新成功返回true
     */
    boolean updateUser(User user);

    /**
     * 清除用户缓存
     * @param userId 用户ID
     */
    void clearCache(Long userId);

    /**
     * 搜索用户
     * @param keyword 关键词
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<User> searchUsers(String keyword, Integer status, int pageNum, int pageSize);

    /**
     * 统计用户总数
     * @return 用户总数
     */
    long count();

    /**
     * 统计最近N天新增用户数
     * @param days 天数
     * @return 新增用户数
     */
    long countNewUsers(int days);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 关键词
     * @param status 状态
     * @return 分页结果
     */
    Page<User> listUsers(int pageNum, int pageSize, String keyword, Integer status);

    /**
     * 保存用户（新增或更新）
     * @param user 用户对象
     * @return 保存成功返回true
     */
    boolean save(User user);

    /**
     * 根据ID更新用户
     * @param user 用户对象
     * @return 更新成功返回true
     */
    boolean updateById(User user);
}
