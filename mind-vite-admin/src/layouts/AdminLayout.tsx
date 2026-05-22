import React, { useEffect, useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Avatar, Dropdown, theme, Tag, Breadcrumb, Space } from 'antd';
import { 
  DashboardOutlined, 
  UserOutlined, 
  WarningOutlined,
  BookOutlined,
  FileTextOutlined,
  LogoutOutlined,
  SettingOutlined,
  SafetyOutlined,
  MonitorOutlined,
  HomeOutlined,
  HeartOutlined
} from '@ant-design/icons';
import { adminAuthApi } from '../api';

const { Header, Sider, Content } = Layout;

const AdminLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { token } = theme.useToken();
  const [userInfo, setUserInfo] = useState<any>(null);

  useEffect(() => {
    loadUserInfo();
  }, []);

  const loadUserInfo = async () => {
    try {
      const res: any = await adminAuthApi.getInfo();
      if (res.code === 200 || res.success) {
        setUserInfo(res.data);
        localStorage.setItem('adminUser', JSON.stringify(res.data));
      }
    } catch (e) {
      // 从本地读取
      const cached = localStorage.getItem('adminUser');
      if (cached) {
        setUserInfo(JSON.parse(cached));
      }
    }
  };

  // 面包屑映射配置
  const breadcrumbNameMap: Record<string, string> = {
    '/': '数据概览',
    '/users': '用户管理',
    '/diaries': '日记管理',
    '/knowledge': '知识库管理',
    '/warnings': '预警管理',
    '/roles': '角色权限',
    '/stories': '故事管理',
    '/monitor': '系统监控',
  };

  // 生成面包屑数据
  const generateBreadcrumb = () => {
    const pathSnippets = location.pathname.split('/').filter(i => i);
    const breadcrumbs: any[] = [
      {
        key: 'home',
        title: (
          <Space>
            <HomeOutlined />
            <span>首页</span>
          </Space>
        ),
        href: '/',
      },
    ];

    pathSnippets.forEach((snippet, index) => {
      const url = `/${pathSnippets.slice(0, index + 1).join('/')}`;
      breadcrumbs.push({
        key: url,
        title: breadcrumbNameMap[url] || snippet,
        href: url,
      });
    });

    return breadcrumbs;
  };

  const roleMap: Record<number, string> = {
    1: '普通用户',
    2: '咨询师',
    3: '管理员',
  };

  const allMenuItems = [
    { key: '/', icon: <DashboardOutlined />, label: '数据概览', roles: [2, 3] },
    { key: '/users', icon: <UserOutlined />, label: '用户管理', roles: [3] },
    { key: '/diaries', icon: <FileTextOutlined />, label: '日记管理', roles: [2, 3] },
    { key: '/stories', icon: <HeartOutlined />, label: '故事管理', roles: [2, 3] },
    { key: '/knowledge', icon: <BookOutlined />, label: '知识库管理', roles: [2, 3] },
    { key: '/warnings', icon: <WarningOutlined />, label: '预警管理', roles: [2, 3] },
    { key: '/roles', icon: <SafetyOutlined />, label: '角色权限', roles: [3] },
    { key: '/monitor', icon: <MonitorOutlined />, label: '系统监控', roles: [3] },
  ];

  const menuItems = allMenuItems.filter(item => 
    item.roles.includes(userInfo?.role || 3)
  );

  const handleMenuClick = (key: string) => {
    navigate(key);
  };

  const handleLogout = async () => {
    try {
      await adminAuthApi.logout();
    } catch (e) {
      // ignore
    }
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    navigate('/login');
  };

  const userMenu = {
    items: [
      { key: 'settings', icon: <SettingOutlined />, label: '设置' },
      { type: 'divider' as const },
      { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout },
    ],
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Sider 
        theme="dark" 
        width={240}
        style={{
          boxShadow: '2px 0 8px rgba(0,0,0,0.1)',
          overflow: 'auto',
          height: '100vh',
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
        }}
      >
        <div style={{ 
          height: 64, 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          gap: 12,
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          borderBottom: '1px solid rgba(255,255,255,0.1)'
        }}>
          <img src="/favicon.svg" alt="logo" style={{ width: 32, height: 32 }} />
          <h2 style={{ color: '#fff', margin: 0, fontSize: 20, fontWeight: 600 }}>心域后台</h2>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => handleMenuClick(key)}
          style={{ 
            marginTop: 16,
            borderRight: 'none',
            fontSize: 14,
          }}
        />
      </Sider>
      <Layout style={{ marginLeft: 240 }}>
        <Header style={{ 
          background: '#fff', 
          padding: '0 32px', 
          display: 'flex', 
          justifyContent: 'space-between',
          alignItems: 'center',
          boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
          position: 'sticky',
          top: 0,
          zIndex: 10,
        }}>
          <Breadcrumb 
            items={generateBreadcrumb()}
            style={{ fontSize: 14 }}
          />
          <Dropdown menu={userMenu}>
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 12 }}>
              <Avatar 
                icon={<UserOutlined />} 
                size={40}
                style={{ 
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  boxShadow: '0 2px 8px rgba(102, 126, 234, 0.3)'
                }} 
              />
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
                <span style={{ fontSize: 15, fontWeight: 600, color: '#333' }}>
                  {userInfo?.nickname || userInfo?.username || '管理员'}
                </span>
                <Tag 
                  color="blue" 
                  style={{ 
                    fontSize: 11, 
                    padding: '0 8px', 
                    lineHeight: '20px',
                    borderRadius: '10px',
                    marginTop: 2,
                  }}
                >
                  {roleMap[userInfo?.role] || '管理员'}
                </Tag>
              </div>
            </div>
          </Dropdown>
        </Header>
        <Content style={{ margin: '24px 32px', minHeight: 280 }}>
          <div style={{ 
            background: '#fff', 
            padding: 32,
            borderRadius: 12,
            boxShadow: '0 2px 12px rgba(0,0,0,0.08)',
            minHeight: 'calc(100vh - 112px)',
          }}>
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default AdminLayout;