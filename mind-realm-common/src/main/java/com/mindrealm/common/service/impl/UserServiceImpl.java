package com.mindrealm.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.mapper.UserMapper;
import com.mindrealm.common.service.UserService;
import com.mindrealm.common.util.TimeUtil;
import com.mindrealm.common.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @className: UserServiceImpl
 * @description: 用户服务实现类
 * @author: 一朝风月
 * @code: 面向自己,面向未来
 * @createTime: 2026.4.5
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:";
    private static final long CACHE_EXPIRE = 30;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("findById: 无效的用户ID {}", id);
            return null;
        }
        return userMapper.selectById(id);
    }

    @Override
    public User findByUsername(String username) {
        if (ValidatorUtil.isEmpty(username)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username.trim()));
    }

    @Override
    public User findByPhone(String phone) {
        if (ValidatorUtil.isEmpty(phone)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone.trim()));
    }

    @Override
    public User findByEmail(String email) {
        if (ValidatorUtil.isEmpty(email)) {
            return null;
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email.trim().toLowerCase()));
    }

    @Override
    public User findByLoginId(String loginId) {
        if (ValidatorUtil.isEmpty(loginId)) {
            return null;
        }
        
        loginId = loginId.trim();
        
        // 根据格式判断登录方式
        if (ValidatorUtil.isEmail(loginId)) {
            return findByEmail(loginId);
        } else if (ValidatorUtil.isPhone(loginId)) {
            return findByPhone(loginId);
        } else {
            return findByUsername(loginId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        if (user == null) {
            log.warn("register: 用户对象为空");
            return false;
        }
        
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            log.warn("register: 用户名已存在 {}", user.getUsername());
            return false;
        }
        
        // 检查手机号是否已存在
        if (ValidatorUtil.isNotEmpty(user.getPhone()) && findByPhone(user.getPhone()) != null) {
            log.warn("register: 手机号已存在 {}", user.getPhone());
            return false;
        }
        
        // 检查邮箱是否已存在
        if (ValidatorUtil.isNotEmpty(user.getEmail()) && findByEmail(user.getEmail()) != null) {
            log.warn("register: 邮箱已存在 {}", user.getEmail());
            return false;
        }
        
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(User user) {
        if (user == null) {
            log.warn("createUser: 用户对象为空");
            return false;
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            log.warn("updateUser: 用户对象或ID为空");
            return false;
        }
        
        log.info("updateUser: id={}, avatar={}", user.getId(), user.getAvatar());
        
        // 使用MyBatis-Plus的update+Wrapper来确保更新
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setAvatar(user.getAvatar());
        updateUser.setNickname(user.getNickname());
        updateUser.setAge(user.getAge());
        updateUser.setGender(user.getGender());
        updateUser.setUpdatedAt(TimeUtil.now());
        
        int result = userMapper.updateById(updateUser);
        
        if (result > 0) {
            clearCache(user.getId());
            log.info("updateUser: 更新成功, id={}", user.getId());
        }
        return result > 0;
    }

    @Override
    public void clearCache(Long userId) {
        if (userId != null) {
            redisTemplate.delete(USER_CACHE_PREFIX + userId);
            log.debug("清除用户缓存: {}", userId);
        }
    }

    @Override
    public Page<User> searchUsers(String keyword, Integer status, int pageNum, int pageSize) {
        // 参数校验
        pageNum = Math.max(1, pageNum);
        pageSize = pageSize <= 0 ? 10 : Math.min(pageSize, 100);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (ValidatorUtil.isNotEmpty(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w
                    .like(User::getUsername, kw)
                    .or().like(User::getNickname, kw)
                    .or().like(User::getPhone, kw));
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreatedAt);
        
        return userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public long count() {
        return userMapper.selectCount(null);
    }

    @Override
    public long countNewUsers(int days) {
        if (days <= 0) {
            days = 7;
        }
        LocalDateTime start = TimeUtil.now().minusDays(days);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .ge(User::getCreatedAt, start);
        return userMapper.selectCount(wrapper);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    @Override
    public Page<User> listUsers(int pageNum, int pageSize, String keyword, Integer status) {
        return searchUsers(keyword, status, pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(User user) {
        if (user == null) {
            log.warn("save: 用户对象为空");
            return false;
        }
        
        if (user.getId() == null) {
            return userMapper.insert(user) > 0;
        } else {
            int result = userMapper.updateById(user);
            if (result > 0) {
                clearCache(user.getId());
            }
            return result > 0;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(User user) {
        if (user == null || user.getId() == null) {
            log.warn("updateById: 用户对象或ID为空");
            return false;
        }
        
        int result = userMapper.updateById(user);
        if (result > 0) {
            clearCache(user.getId());
        }
        return result > 0;
    }
}
