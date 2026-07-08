package com.mindrealm.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindrealm.api.service.ParentChildBindingService;
import com.mindrealm.common.entity.ParentChildBinding;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.mapper.ParentChildBindingMapper;
import com.mindrealm.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: ParentChildBindingServiceImpl
 * @description: 家长-孩子绑定关系服务实现
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.7.8
 */
@Service
@RequiredArgsConstructor
public class ParentChildBindingServiceImpl implements ParentChildBindingService {

    private static final Logger log = LoggerFactory.getLogger(ParentChildBindingServiceImpl.class);

    private final ParentChildBindingMapper bindingMapper;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParentChildBinding sendBindRequest(Long parentId, String childIdentifier) {
        // 通过手机号或用户名查找孩子
        User child = userService.findByPhone(childIdentifier);
        if (child == null) {
            child = userService.findByUsername(childIdentifier);
        }
        if (child == null) {
            throw new IllegalArgumentException("未找到该用户");
        }

        // 不能绑定自己
        if (child.getId().equals(parentId)) {
            throw new IllegalArgumentException("不能绑定自己");
        }

        // 孩子不能是家长角色
        if (child.getRole() != null && child.getRole() == 5) {
            throw new IllegalArgumentException("不能绑定其他家长账号");
        }

        // 检查是否已有绑定记录
        LambdaQueryWrapper<ParentChildBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildBinding::getParentId, parentId)
               .eq(ParentChildBinding::getChildId, child.getId());
        ParentChildBinding existing = bindingMapper.selectOne(wrapper);

        if (existing != null) {
            if ("APPROVED".equals(existing.getStatus())) {
                throw new IllegalArgumentException("已绑定该用户");
            }
            if ("PENDING".equals(existing.getStatus())) {
                throw new IllegalArgumentException("已发送过绑定请求，等待对方确认");
            }
            // 之前被拒绝的，可以重新发送
            existing.setStatus("PENDING");
            existing.setRequestTime(LocalDateTime.now());
            existing.setResponseTime(null);
            bindingMapper.updateById(existing);
            log.info("重新发送绑定请求: parentId={}, childId={}", parentId, child.getId());
            return existing;
        }

        // 创建新的绑定请求
        ParentChildBinding binding = ParentChildBinding.builder()
                .parentId(parentId)
                .childId(child.getId())
                .status("PENDING")
                .requestTime(LocalDateTime.now())
                .build();

        bindingMapper.insert(binding);
        log.info("发送绑定请求: parentId={}, childId={}, bindingId={}", parentId, child.getId(), binding.getId());
        return binding;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveBindRequest(Long bindingId, Long childId) {
        ParentChildBinding binding = bindingMapper.selectById(bindingId);
        if (binding == null) {
            throw new IllegalArgumentException("绑定请求不存在");
        }
        if (!binding.getChildId().equals(childId)) {
            throw new IllegalArgumentException("无权操作此绑定请求");
        }
        if (!"PENDING".equals(binding.getStatus())) {
            throw new IllegalArgumentException("该请求已处理");
        }

        binding.setStatus("APPROVED");
        binding.setResponseTime(LocalDateTime.now());
        bindingMapper.updateById(binding);
        log.info("同意绑定请求: bindingId={}, childId={}", bindingId, childId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectBindRequest(Long bindingId, Long childId) {
        ParentChildBinding binding = bindingMapper.selectById(bindingId);
        if (binding == null) {
            throw new IllegalArgumentException("绑定请求不存在");
        }
        if (!binding.getChildId().equals(childId)) {
            throw new IllegalArgumentException("无权操作此绑定请求");
        }
        if (!"PENDING".equals(binding.getStatus())) {
            throw new IllegalArgumentException("该请求已处理");
        }

        binding.setStatus("REJECTED");
        binding.setResponseTime(LocalDateTime.now());
        bindingMapper.updateById(binding);
        log.info("拒绝绑定请求: bindingId={}, childId={}", bindingId, childId);
    }

    @Override
    public List<User> getBoundChildren(Long parentId) {
        LambdaQueryWrapper<ParentChildBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildBinding::getParentId, parentId)
               .eq(ParentChildBinding::getStatus, "APPROVED");

        List<ParentChildBinding> bindings = bindingMapper.selectList(wrapper);
        List<User> children = new ArrayList<>();
        for (ParentChildBinding binding : bindings) {
            User child = userService.findById(binding.getChildId());
            if (child != null) {
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public List<ParentChildBinding> getPendingRequests(Long parentId) {
        LambdaQueryWrapper<ParentChildBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildBinding::getParentId, parentId)
               .eq(ParentChildBinding::getStatus, "PENDING")
               .orderByDesc(ParentChildBinding::getRequestTime);
        return bindingMapper.selectList(wrapper);
    }

    @Override
    public List<ParentChildBinding> getIncomingRequests(Long childId) {
        LambdaQueryWrapper<ParentChildBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildBinding::getChildId, childId)
               .eq(ParentChildBinding::getStatus, "PENDING")
               .orderByDesc(ParentChildBinding::getRequestTime);
        return bindingMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrRemoveBinding(Long bindingId, Long parentId) {
        ParentChildBinding binding = bindingMapper.selectById(bindingId);
        if (binding == null) {
            throw new IllegalArgumentException("绑定记录不存在");
        }
        if (!binding.getParentId().equals(parentId)) {
            throw new IllegalArgumentException("无权操作此绑定记录");
        }

        bindingMapper.deleteById(bindingId);
        log.info("解除绑定: bindingId={}, parentId={}, childId={}", bindingId, parentId, binding.getChildId());
    }

    @Override
    public boolean isBound(Long parentId, Long childId) {
        LambdaQueryWrapper<ParentChildBinding> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParentChildBinding::getParentId, parentId)
               .eq(ParentChildBinding::getChildId, childId)
               .eq(ParentChildBinding::getStatus, "APPROVED");
        return bindingMapper.selectCount(wrapper) > 0;
    }
}
