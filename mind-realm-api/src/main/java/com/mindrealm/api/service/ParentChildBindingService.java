package com.mindrealm.api.service;

import com.mindrealm.common.entity.ParentChildBinding;
import com.mindrealm.common.entity.User;

import java.util.List;

/**
 * @className: ParentChildBindingService
 * @description: 家长-孩子绑定关系服务接口
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.8
 */
public interface ParentChildBindingService {

    /**
     * 家长发送绑定请求
     */
    ParentChildBinding sendBindRequest(Long parentId, String childIdentifier);

    /**
     * 孩子同意绑定请求
     */
    void approveBindRequest(Long bindingId, Long childId);

    /**
     * 孩子拒绝绑定请求
     */
    void rejectBindRequest(Long bindingId, Long childId);

    /**
     * 获取已绑定的孩子列表
     */
    List<User> getBoundChildren(Long parentId);

    /**
     * 获取家长发出的待处理请求
     */
    List<ParentChildBinding> getPendingRequests(Long parentId);

    /**
     * 获取孩子的待处理请求
     */
    List<ParentChildBinding> getIncomingRequests(Long childId);

    /**
     * 家长取消绑定请求或解除已批准的绑定
     */
    void cancelOrRemoveBinding(Long bindingId, Long parentId);

    /**
     * 检查家长和孩子是否已绑定
     */
    boolean isBound(Long parentId, Long childId);
}
