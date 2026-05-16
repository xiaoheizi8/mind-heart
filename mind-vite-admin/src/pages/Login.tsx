import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { adminAuthApi } from '../api';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const onFinish = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const res: any = await adminAuthApi.login(values);
      console.log('登录响应:', res);
      
      // 响应格式: { token, userId, username, nickname, role }
      const token = res.token || res.data?.token;
      
      if (token) {
        localStorage.setItem('adminToken', token);
        localStorage.setItem('adminUser', JSON.stringify(res.data || res));
        message.success('登录成功');
        navigate('/');
      } else if (res.code === 401 || res.code === 403) {
        message.error(res.message || '用户名或密码错误');
      } else {
        message.error(res.message || '登录失败');
      }
    } catch (error: any) {
      console.error('登录错误:', error);
      message.error(error?.response?.data?.message || error?.message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <Card style={{ width: 400, boxShadow: '0 8px 32px rgba(0,0,0,0.2)', borderRadius: 12 }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <h1 style={{ fontSize: 28, fontWeight: 700, color: '#1a1a2e', margin: 0 }}>
            心域管理后台
          </h1>
          <p style={{ color: '#666', marginTop: 8 }}>青少年心理数字孪生系统</p>
          <p style={{ color: '#999', fontSize: 12, marginTop: 4 }}>管理员账号: admin / 123456</p>
        </div>
        
        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input 
              prefix={<UserOutlined />} 
              placeholder="用户名" 
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password 
              prefix={<LockOutlined />} 
              placeholder="密码" 
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Login;