import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, Form, Input, Select, message, Popconfirm, Card, Dropdown, Upload, Spin } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, DownloadOutlined, UploadOutlined, FileExcelOutlined, FileWordOutlined, FilePdfOutlined, FileTextOutlined } from '@ant-design/icons';
import { knowledgeApi, documentApi } from '../api';

interface Knowledge {
  id: number;
  title: string;
  category: string;
  summary: string;
  content: string;
  tags: string[];
  viewCount: number;
  status: number;
  createdAt: string;
}

const { TextArea } = Input;

const KnowledgeManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<Knowledge[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [modalVisible, setModalVisible] = useState(false);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [editingItem, setEditingItem] = useState<Knowledge | null>(null);
  const [previewItem, setPreviewItem] = useState<Knowledge | null>(null);
  const [exportLoading, setExportLoading] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize]);

  const loadData = async () => {
    setLoading(true);
    try {
      const res: any = await knowledgeApi.list({
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
      });
      if (res.code === 200 || res.success) {
        setData(res.data?.records || []);
        setPagination({ ...pagination, total: res.data?.total || 0 });
      }
    } catch (error) {
      console.error('加载知识库列表失败:', error);
      setData([]);
      setPagination({ ...pagination, total: 0 });
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = () => {
    setEditingItem(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Knowledge) => {
    setEditingItem(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handlePreview = (record: Knowledge) => {
    setPreviewItem(record);
    setPreviewVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await knowledgeApi.delete(id);
      message.success('删除成功');
      loadData();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingItem) {
        await knowledgeApi.update(editingItem.id, values);
        message.success('更新成功');
      } else {
        await knowledgeApi.create(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadData();
    } catch (error) {
      message.error('操作失败');
    }
  };

  const handleExport = async (format: 'excel' | 'word' | 'pdf' | 'csv') => {
    setExportLoading(true);
    try {
      const url = documentApi.exportKnowledge(format);
      const link = document.createElement('a');
      link.href = url;
      link.download = `knowledge_export_${Date.now()}.${format === 'excel' ? 'xlsx' : format}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败');
    } finally {
      setExportLoading(false);
    }
  };

  const handleImport = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    
    try {
      const res: any = await documentApi.importKnowledge(formData);
      if (res.code === 200 || res.success) {
        const result = res.data;
        message.success(`导入完成: 成功 ${result.successCount}/${result.totalCount}`);
        if (result.failCount > 0) {
          message.warning(`失败 ${result.failCount} 条`);
        }
        loadData();
      } else {
        message.error(res.message || '导入失败');
      }
    } catch (error: any) {
      message.error(error.message || '导入失败');
    }
    return false;
  };

  const categoryMap: Record<string, { label: string; color: string }> = {
    knowledge: { label: '心理知识', color: 'blue' },
    skill: { label: '调节技巧', color: 'green' },
    case: { label: '案例故事', color: 'orange' },
  };

  const exportItems = [
    { key: 'excel', label: 'Excel 格式 (.xlsx)', icon: <FileExcelOutlined /> },
    { key: 'word', label: 'Word 格式 (.docx)', icon: <FileWordOutlined /> },
    { key: 'pdf', label: 'PDF 格式 (.pdf)', icon: <FilePdfOutlined /> },
    { key: 'csv', label: 'CSV 格式 (.csv)', icon: <FileTextOutlined /> },
  ];

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '标题', dataIndex: 'title', key: 'title', ellipsis: true },
    { 
      title: '分类', 
      dataIndex: 'category', 
      key: 'category',
      render: (category: string) => {
        const cat = categoryMap[category] || { label: category, color: 'default' };
        return <Tag color={cat.color}>{cat.label}</Tag>;
      }
    },
    { title: '摘要', dataIndex: 'summary', key: 'summary', ellipsis: true },
    { 
      title: '浏览量', 
      dataIndex: 'viewCount', 
      key: 'viewCount',
      sorter: (a: Knowledge, b: Knowledge) => a.viewCount - b.viewCount,
    },
    { 
      title: '状态', 
      dataIndex: 'status', 
      key: 'status',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '已发布' : '草稿'}
        </Tag>
      )
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 120 },
    {
      title: '操作',
      key: 'action',
      width: 180,
      render: (_: any, record: Knowledge) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handlePreview(record)}>
            预览
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确定删除此文章吗？"
            onConfirm={() => handleDelete(record.id)}
          >
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
      <div style={{ marginBottom: 16, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增文章
        </Button>
        
        <Spin spinning={exportLoading}>
          <Dropdown menu={{
            items: exportItems.map(item => ({
              ...item,
              onClick: () => handleExport(item.key as any),
            })),
          }} trigger={['click']}>
            <Button icon={<DownloadOutlined />}>
              导出 {loading && <Spin size="small" />}
            </Button>
          </Dropdown>
        </Spin>
        
        <Upload
          accept=".xlsx,.xls,.docx,.csv"
          showUploadList={false}
          beforeUpload={handleImport}
        >
          <Button icon={<UploadOutlined />}>
            导入
          </Button>
        </Upload>
        
        <Button onClick={() => window.open(documentApi.downloadTemplate())}>
          下载模板
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
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
      />

      <Modal
        title={editingItem ? '编辑文章' : '新增文章'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="title" label="标题" rules={[{ required: true, message: '请输入标题' }]}>
            <Input placeholder="请输入文章标题" maxLength={200} showCount />
          </Form.Item>
          <Form.Item name="category" label="分类" rules={[{ required: true, message: '请选择分类' }]}>
            <Select placeholder="请选择分类">
              <Select.Option value="knowledge">心理知识</Select.Option>
              <Select.Option value="skill">调节技巧</Select.Option>
              <Select.Option value="case">案例故事</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="summary" label="摘要" rules={[{ required: true, message: '请输入摘要' }]}>
            <TextArea placeholder="请输入文章摘要" rows={2} maxLength={500} showCount />
          </Form.Item>
          <Form.Item name="content" label="内容" rules={[{ required: true, message: '请输入内容' }]}>
            <TextArea rows={8} placeholder="请输入文章内容" />
          </Form.Item>
          <Form.Item name="tags" label="标签">
            <Select mode="tags" placeholder="输入标签后按回车">
              <Select.Option value="焦虑">焦虑</Select.Option>
              <Select.Option value="抑郁">抑郁</Select.Option>
              <Select.Option value="压力">压力</Select.Option>
              <Select.Option value="人际关系">人际关系</Select.Option>
              <Select.Option value="睡眠">睡眠</Select.Option>
              <Select.Option value="考试">考试</Select.Option>
              <Select.Option value="家庭">家庭</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>发布</Select.Option>
              <Select.Option value={0}>草稿</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="文章预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={null}
        width={800}
      >
        {previewItem && (
          <Card>
            <h2 style={{ marginBottom: 16 }}>{previewItem.title}</h2>
            <div style={{ marginBottom: 16 }}>
              <Tag color={categoryMap[previewItem.category]?.color || 'default'}>
                {categoryMap[previewItem.category]?.label || previewItem.category}
              </Tag>
              {previewItem.tags?.map((tag: string) => (
                <Tag key={tag}>{tag}</Tag>
              ))}
            </div>
            <p style={{ color: '#666', marginBottom: 16 }}>{previewItem.summary}</p>
            <div style={{ whiteSpace: 'pre-wrap', lineHeight: 1.8 }}>
              {previewItem.content}
            </div>
          </Card>
        )}
      </Modal>
    </div>
  );
};

export default KnowledgeManagement;
