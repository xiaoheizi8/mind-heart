import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, Form, Input, Select, message, Popconfirm, Dropdown } from 'antd';
import { PlusOutlined, EditOutlined, StopOutlined, CheckOutlined, DownloadOutlined } from '@ant-design/icons';
import { userApi, reportApi } from '../api';

interface User {
  id: number;
  username: string;
  nickname: string;
  phone: string;
  email: string;
  role: number;
  status: number;
  createdAt: string;
}

const UserManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<User[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [modalVisible, setModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize]);

  const loadData = async () => {
    setLoading(true);
    try {
      const res: any = await userApi.list({
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
      });
      if (res.code === 200 || res.success) {
        setData(res.data?.records || []);
        setPagination({ ...pagination, total: res.data?.total || 0 });
      }
    } catch (error) {
      console.error('Failed to load users:', error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = () => {
    setEditingUser(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: User) => {
    setEditingUser(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleStatus = async (id: number, status: number) => {
    try {
      await userApi.toggleStatus(id, status);
      message.success(status === 1 ? '启用成功' : '禁用成功');
      loadData();
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingUser) {
        await userApi.update(editingUser.id, values);
        message.success('更新成功');
      } else {
        await userApi.create(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadData();
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleExportReport = (userId: number, reportType: string) => {
    const url = reportApi.exportUserReport(userId, reportType);
    const token = localStorage.getItem('adminToken');
    
    // 使用fetch下载文件,携带token
    fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('导出失败');
      }
      return response.blob();
    })
    .then(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `用户${userId}_${reportType}情绪报告.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      message.success('报告已导出');
    })
    .catch(err => {
      console.error('导出失败:', err);
      message.error('导出失败');
    });
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
    { title: '手机号', dataIndex: 'phone', key: 'phone' },
    { title: '邮箱', dataIndex: 'email', key: 'email' },
    { 
      title: '角色', 
      dataIndex: 'role', 
      key: 'role',
      render: (role: number) => (
        <Tag color={role === 3 ? 'red' : role === 2 ? 'blue' : 'green'}>
          {role === 3 ? '管理员' : role === 2 ? '咨询师' : '用户'}
        </Tag>
      )
    },
    { 
      title: '状态', 
      dataIndex: 'status', 
      key: 'status',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '正常' : '禁用'}
        </Tag>
      )
    },
    { title: '注册时间', dataIndex: 'createdAt', key: 'createdAt' },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: User) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title={record.status === 1 ? '确定禁用此用户吗？' : '确定启用此用户吗？'}
            onConfirm={() => handleStatus(record.id, record.status === 1 ? 0 : 1)}
          >
            <Button type="link" size="small" icon={record.status === 1 ? <StopOutlined /> : <CheckOutlined />}>
              {record.status === 1 ? '禁用' : '启用'}
            </Button>
          </Popconfirm>
          <Dropdown
            menu={{
              items: [
                { key: 'week', label: '导出周报' },
                { key: 'month', label: '导出月报' },
                { key: 'quarter', label: '导出季报' },
              ],
              onClick: ({ key }) => handleExportReport(record.id, key)
            }}
          >
            <Button type="link" size="small" icon={<DownloadOutlined />}>
              导出报告
            </Button>
          </Dropdown>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增用户
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          onChange: (page, pageSize) => setPagination({ ...pagination, current: page, pageSize }),
        }}
      />

      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input disabled={!!editingUser} />
          </Form.Item>
          <Form.Item name="nickname" label="昵称">
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: !editingUser }]}>
            <Input.Password placeholder={editingUser ? '留空表示不修改' : '请输入密码'} />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱">
            <Input />
          </Form.Item>
          <Form.Item name="role" label="角色" initialValue={1}>
            <Select>
              <Select.Option value={1}>普通用户</Select.Option>
              <Select.Option value={2}>咨询师</Select.Option>
              <Select.Option value={3}>管理员</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>正常</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserManagement;