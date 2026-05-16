import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Switch, message, Tree, Space, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { roleApi, menuApi } from '../api';

interface Role {
  id: number;
  roleCode: string;
  roleName: string;
  description: string;
  status: number;
  sort: number;
  createdAt: string;
}

interface MenuNode {
  id: number;
  menuName: string;
  parentId: number;
  children?: MenuNode[];
}

const RoleManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [roles, setRoles] = useState<Role[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [menuModalVisible, setMenuModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [editingRole, setEditingRole] = useState<Role | null>(null);
  const [menuTree, setMenuTree] = useState<MenuNode[]>([]);
  const [checkedMenuIds, setCheckedMenuIds] = useState<number[]>([]);
  const [currentRoleId, setCurrentRoleId] = useState<number | null>(null);

  useEffect(() => {
    loadRoles();
    loadMenuTree();
  }, [page, keyword]);

  const loadRoles = async () => {
    setLoading(true);
    try {
      const res: any = await roleApi.list({ pageNum: page, pageSize, keyword });
      // 提取data字段，API返回格式: { code, message, data: { records, total } }
      setRoles(res.data?.records || []);
      setTotal(res.data?.total || 0);
    } catch (error) {
      message.error('加载角色列表失败');
    } finally {
      setLoading(false);
    }
  };

  const loadMenuTree = async () => {
    try {
      const res: any = await menuApi.getAllMenuTree();
      // 提取data字段
      setMenuTree(res.data || []);
    } catch (error) {
      console.error('加载菜单树失败', error);
    }
  };

  const handleAdd = () => {
    setEditingRole(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Role) => {
    setEditingRole(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await roleApi.delete(id);
      message.success('删除成功');
      loadRoles();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingRole) {
        await roleApi.update(editingRole.id, values);
        message.success('更新成功');
      } else {
        await roleApi.save(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadRoles();
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleMenuPermission = async (roleId: number) => {
    setCurrentRoleId(roleId);
    try {
      const res: any = await menuApi.getRoleMenus(roleId);
      // 提取data字段
      setCheckedMenuIds(res.data || []);
      setMenuModalVisible(true);
    } catch (error) {
      message.error('加载菜单权限失败');
    }
  };

  const handleMenuSubmit = async () => {
    if (!currentRoleId) return;
    try {
      await menuApi.saveRoleMenus(currentRoleId, checkedMenuIds);
      message.success('保存成功');
      setMenuModalVisible(false);
    } catch (error) {
      message.error('保存失败');
    }
  };

  const columns = [
    { title: '角色编码', dataIndex: 'roleCode', key: 'roleCode' },
    { title: '角色名称', dataIndex: 'roleName', key: 'roleName' },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { 
      title: '状态', 
      dataIndex: 'status', 
      key: 'status',
      render: (status: number) => status === 1 ? '启用' : '禁用'
    },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_: any, record: Role) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleMenuPermission(record.id)}>
            菜单权限
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确定删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Input.Search
          placeholder="搜索角色名称或编码"
          style={{ width: 300 }}
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onSearch={loadRoles}
        />
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增角色
        </Button>
      </div>

      <Table
        loading={loading}
        columns={columns}
        dataSource={roles}
        rowKey="id"
        pagination={{
          current: page,
          pageSize,
          total,
          onChange: (p) => setPage(p),
        }}
      />

      <Modal
        title={editingRole ? '编辑角色' : '新增角色'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="roleCode" label="角色编码" rules={[{ required: true }]}>
            <Input placeholder="例如：ADMIN" />
          </Form.Item>
          <Form.Item name="roleName" label="角色名称" rules={[{ required: true }]}>
            <Input placeholder="例如：系统管理员" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={3} placeholder="角色描述" />
          </Form.Item>
          <Form.Item name="sort" label="排序" initialValue={0}>
            <Input type="number" />
          </Form.Item>
          <Form.Item name="status" label="状态" valuePropName="checked" initialValue={true}>
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="菜单权限配置"
        open={menuModalVisible}
        onOk={handleMenuSubmit}
        onCancel={() => setMenuModalVisible(false)}
        width={600}
      >
        <Tree
          checkable
          checkedKeys={checkedMenuIds}
          onCheck={(checked: any) => setCheckedMenuIds(Array.isArray(checked) ? checked : checked.checked)}
          treeData={menuTree as any}
          fieldNames={{ title: 'menuName', key: 'id', children: 'children' }}
        />
      </Modal>
    </div>
  );
};

export default RoleManagement;
