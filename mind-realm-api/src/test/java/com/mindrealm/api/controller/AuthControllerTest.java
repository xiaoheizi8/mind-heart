package com.mindrealm.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindrealm.api.config.JwtUtils;
import com.mindrealm.api.dto.LoginRequest;
import com.mindrealm.common.entity.User;
import com.mindrealm.common.service.impl.EmailService;
import com.mindrealm.common.service.LoginLogService;
import com.mindrealm.common.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 单元测试
 * 覆盖: 登录、验证码登录、注册、发送验证码、登出、刷新令牌
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private EmailService emailService;

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testLoginSuccess() throws Exception {
        // 准备数据
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("123456");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("e10adc3949ba59abbe56e057f20f883e") // MD5("123456")
                .status(1)
                .role(1)
                .build();

        when(userService.findByUsername("testuser")).thenReturn(user);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyInt())).thenReturn("test-token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        // 执行并验证
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(loginLogService).logSuccess(eq(1L), eq("testuser"), eq("password"));
    }

    @Test
    void testLoginWithEmptyPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码不能为空"));
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexist");
        request.setPassword("123456");

        when(userService.findByUsername("nonexist")).thenReturn(null);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpass");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("e10adc3949ba59abbe56e057f20f883e")
                .status(1)
                .build();

        when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void testLoginByEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("123456");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("e10adc3949ba59abbe56e057f20f883e")
                .status(1)
                .role(1)
                .build();

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyInt())).thenReturn("test-token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"));
    }

    @Test
    void testLoginByPhone() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("13800138000");
        request.setPassword("123456");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .phone("13800138000")
                .password("e10adc3949ba59abbe56e057f20f883e")
                .status(1)
                .role(1)
                .build();

        when(userService.findByPhone("13800138000")).thenReturn(user);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyInt())).thenReturn("test-token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testLoginByCodeSuccess() throws Exception {
        when(emailService.verifyCode("test@example.com", "123456")).thenReturn(true);

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .status(1)
                .role(1)
                .build();

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyInt())).thenReturn("test-token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        mockMvc.perform(post("/api/v1/auth/loginByCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"code\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"));
    }

    @Test
    void testLoginByCodeInvalid() throws Exception {
        when(emailService.verifyCode("test@example.com", "000000")).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/loginByCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"code\":\"000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("验证码错误或已过期"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        User user = User.builder()
                .username("newuser")
                .password("123456")
                .email("new@example.com")
                .build();

        when(userService.findByUsername("newuser")).thenReturn(null);
        when(userService.findByEmail("new@example.com")).thenReturn(null);
        when(userService.register(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void testRegisterUsernameExists() throws Exception {
        User user = User.builder()
                .username("existuser")
                .password("123456")
                .build();

        User existUser = User.builder().username("existuser").build();
        when(userService.findByUsername("existuser")).thenReturn(existUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void testRegisterPasswordTooShort() throws Exception {
        User user = User.builder()
                .username("newuser")
                .password("123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("密码长度不能少于6位"));
    }

    @Test
    void testSendCodeSuccess() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        doNothing().when(emailService).sendVerificationCode(anyString());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        mockMvc.perform(post("/api/v1/auth/sendCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证码已发送到您的邮箱"));
    }

    @Test
    void testSendCodeInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/sendCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("邮箱格式不正确"));
    }

    @Test
    void testSendCodeEmailExists() throws Exception {
        User user = User.builder().email("test@example.com").build();
        when(userService.findByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(post("/api/v1/auth/sendCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("该邮箱已注册"));
    }

    @Test
    void testSendCodeTooFrequent() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(null);
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/sendCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("发送过于频繁,请1分钟后重试"));
    }

    @Test
    void testLogout() throws Exception {
        when(redisTemplate.delete(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    void testRefreshTokenSuccess() throws Exception {
        when(jwtUtils.validateToken("old-token")).thenReturn(true);
        when(jwtUtils.getUserId("old-token")).thenReturn(1L);

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .status(1)
                .role(1)
                .build();

        when(userService.findById(1L)).thenReturn(user);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyInt())).thenReturn("new-token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), any(), anyLong(), any());

        mockMvc.perform(post("/api/v1/auth/refreshToken")
                        .header("Authorization", "Bearer old-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("new-token"));
    }

    @Test
    void testRefreshTokenInvalid() throws Exception {
        when(jwtUtils.validateToken("invalid-token")).thenReturn(false);

        mockMvc.perform(post("/api/v1/auth/refreshToken")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void testRefreshTokenNoHeader() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refreshToken"))
                .andExpect(status().isBadRequest());
    }
}
