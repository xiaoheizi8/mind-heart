import React, { useState, useEffect } from 'react';
import { Card, Modal, Descriptions, Tag, message, Space, Statistic, Row, Col, Input, Spin } from 'antd';
import { EyeOutlined, CheckOutlined, CloseOutlined, DeleteOutlined } from '@ant-design/icons';
import { storyApi } from '../api';
import { EmotionTag, StatusTag, FilterBar, DataTable } from '../components';

interface Story {
  id: number;
  userId?: number;
  title: string;
  content?: string;
  emotionType: string;
  tags: string[];
  coverImage?: string;
  isAnonymous: boolean;
  displayNickname: string;
  likeCount: number;
  commentCount: number;
  shareCount?: number;
  viewCount?: number;
  status: number;
  rejectReason?: string;
  auditorId?: number;
  auditTime?: string;
  createdAt: string;
  publishedAt?: string;
}

const StoryManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<Story[]>([]);
  const [stats, setStats] = useState({ total: 0, pending: 0, approved: 0, rejected: 0 });
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [auditModalVisible, setAuditModalVisible] = useState(false);
  const [selectedStory, setSelectedStory] = useState<Story | null>(null);
  const [rejectReason, setRejectReason] = useState('');
  const [auditLoading, setAuditLoading] = useState(false);
  const [filters, setFilters] = useState<Record<string, any>>({ status: undefined });

  useEffect(() => {
    loadData();
    loadStats();
  }, [pagination.current, pagination.pageSize, filters.status]);

  const loadStats = async () => {
    try {
      // Fetch counts for each status separately for stats panel
      const [pendingRes, approvedRes, rejectedRes] = await Promise.all([
        storyApi.getPendingList({ page: 1, size: 1, status: 0 }),
        storyApi.getPendingList({ page: 1, size: 1, status: 1 }),
        storyApi.getPendingList({ page: 1, size: 1, status: 2 })
      ]);
      const pending = (pendingRes as any)?.data?.total || 0;
      const approved = (approvedRes as any)?.data?.total || 0;
      const rejected = (rejectedRes as any)?.data?.total || 0;
      setStats({ total: pending + approved + rejected, pending, approved, rejected });
    } catch (e) { /* ignore */ }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const res: any = await storyApi.getPendingList({
        page: pagination.current,
        size: pagination.pageSize,
        ...filters
      });
      if (res) {
        setData(res.data?.records || []);
        setPagination({ ...pagination, total: res.data?.total || 0 });
      }
    } catch (error) {
      console.error('加载列表失败:', error);
      message.error('加载列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key: string, value: any) => {
    setFilters(prev => ({ ...prev, [key]: value }));
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  const handleReset = () => {
    setFilters({ status: undefined });
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  const handleView = async (record: Story) => {
    setSelectedStory(record);
    setDetailVisible(true);
    setDetailLoading(true);
    try {
      const res: any = await storyApi.getById(record.id);
      if (res?.data) {
        setSelectedStory(res.data);
      }
    } catch (e) {
      console.error('加载详情失败:', e);
    } finally {
      setDetailLoading(false);
    }
  };

  const [auditApproved, setAuditApproved] = useState(true);

  const handleAudit = (record: Story, approved: boolean) => {
    setSelectedStory(record);
    setRejectReason('');
    setAuditApproved(approved);
    setAuditModalVisible(true);
  };

  const handleAuditSubmit = async () => {
    if (!selectedStory) return;
    setAuditLoading(true);
    try {
      const res = await storyApi.audit(selectedStory.id, {
        auditorId: 1,
        approved: auditApproved,
        rejectReason: auditApproved ? undefined : rejectReason
      });
      if (res) {
        message.success(auditApproved ? '审核通过' : '已拒绝');
        setAuditModalVisible(false);
        loadData();
      }
    } catch (error) {
      message.error('审核失败');
    } finally {
      setAuditLoading(false);
    }
  };

  const handleDelete = (record: Story) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这条故事吗？',
      okText: '确认',
      cancelText: '取消',
      async onOk() {
        try {
          const res = await storyApi.delete(record.id);
          if (res) {
            message.success('删除成功');
            loadData();
          }
        } catch (error) {
          message.error('删除失败');
        }
      }
    });
  };

  const columns = [
    { key: 'id', title: 'ID', dataIndex: 'id', width: 60 },
    { key: 'title', title: '标题', dataIndex: 'title', width: 200, ellipsis: true },
    { key: 'emotionType', title: '情绪', dataIndex: 'emotionType', width: 80, 
      render: (_: any, record: Story) => <EmotionTag type={record.emotionType} size="small" />
    },
    { key: 'isAnonymous', title: '方式', dataIndex: 'isAnonymous', width: 70,
      render: (val: boolean) => <Tag color={val ? 'default' : 'blue'}>{val ? '匿名' : '公开'}</Tag>
    },
    { key: 'displayNickname', title: '发布者', dataIndex: 'displayNickname', width: 90 },
    { key: 'likeCount', title: '点赞', dataIndex: 'likeCount', width: 60 },
    { key: 'commentCount', title: '评论', dataIndex: 'commentCount', width: 60 },
    { key: 'status', title: '状态', dataIndex: 'status', width: 80,
      render: (_: any, record: Story) => <StatusTag status={record.status} />
    },
    { key: 'createdAt', title: '发布时间', dataIndex: 'createdAt', width: 160 },
  ];

  const actions = [
    { key: 'view', label: '查看', icon: <EyeOutlined />, onClick: handleView },
    { key: 'pass', label: '通过', icon: <CheckOutlined />, onClick: (r: Story) => handleAudit(r, true), visible: (r: Story) => r.status === 0 },
    { key: 'reject', label: '拒绝', icon: <CloseOutlined />, onClick: (r: Story) => handleAudit(r, false), visible: (r: Story) => r.status === 0 },
    { key: 'delete', label: '删除', icon: <DeleteOutlined />, danger: true, onClick: handleDelete },
  ];

  const filterItems = [
    { key: 'status', label: '状态', type: 'select' as const, allowClear: true, width: 140,
      options: [
        { value: 0, label: '待审核' },
        { value: 1, label: '已发布' },
        { value: 2, label: '已拒绝' },
      ]
    },
  ];

  return (
    <div style={{ padding: 0 }}>
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card size="small">
            <Statistic title="总故事数" value={stats.total} />
          </Card>
        </Col>
        <Col span={6}>
          <Card size="small">
            <Statistic title="待审核" value={stats.pending} valueStyle={{ color: '#faad14' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card size="small">
            <Statistic title="已发布" value={stats.approved} valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card size="small">
            <Statistic title="已拒绝" value={stats.rejected} valueStyle={{ color: '#ff4d4f' }} />
          </Card>
        </Col>
      </Row>

      {/* 筛选栏 */}
      <FilterBar
        filters={filters}
        onFilterChange={handleFilterChange}
        onSearch={loadData}
        onReset={handleReset}
        items={filterItems}
      />

      {/* 数据表格 */}
      <Card title="故事列表" size="small">
        <DataTable
          columns={columns}
          dataSource={data}
          loading={loading}
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total: number) => `共 ${total} 条`
          }}
          onChange={(pag) => setPagination({ ...pagination, current: pag.current || 1, pageSize: pag.pageSize || 10 })}
          actions={actions}
        />
      </Card>

      {/* 详情弹窗 */}
      <Modal
        title="故事详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={700}
      >
        <Spin spinning={detailLoading}>
          {selectedStory && (
            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="ID">{selectedStory.id}</Descriptions.Item>
              <Descriptions.Item label="情绪">
                <EmotionTag type={selectedStory.emotionType} />
              </Descriptions.Item>
              <Descriptions.Item label="状态">
                <StatusTag status={selectedStory.status} />
              </Descriptions.Item>
              <Descriptions.Item label="发布方式">
                {selectedStory.isAnonymous ? '匿名' : '公开'}
              </Descriptions.Item>
              <Descriptions.Item label="发布者ID">{selectedStory.userId || '-'}</Descriptions.Item>
              <Descriptions.Item label="发布昵称">{selectedStory.displayNickname || '-'}</Descriptions.Item>
              <Descriptions.Item label="标题" span={2}>{selectedStory.title}</Descriptions.Item>
              <Descriptions.Item label="内容" span={2}>
                <div style={{ whiteSpace: 'pre-wrap', maxHeight: 200, overflow: 'auto' }}>
                  {selectedStory.content || '(暂未加载)'}
                </div>
              </Descriptions.Item>
              <Descriptions.Item label="点赞数">{selectedStory.likeCount || 0}</Descriptions.Item>
              <Descriptions.Item label="评论数">{selectedStory.commentCount || 0}</Descriptions.Item>
              <Descriptions.Item label="分享数">{selectedStory.shareCount ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="浏览数">{selectedStory.viewCount ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="标签" span={2}>
                {selectedStory.tags?.length ? selectedStory.tags.map((tag, i) => (
                  <Tag key={i} style={{ marginRight: 4 }}>{tag}</Tag>
                )) : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="发布时间">{selectedStory.publishedAt || '-'}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{selectedStory.createdAt || '-'}</Descriptions.Item>
              {selectedStory.rejectReason && (
                <Descriptions.Item label="拒绝原因" span={2}>{selectedStory.rejectReason}</Descriptions.Item>
              )}
            </Descriptions>
          )}
        </Spin>
      </Modal>

      {/* 审核弹窗 */}
      <Modal
        title={auditApproved ? '审核通过' : '拒绝故事'}
        open={auditModalVisible}
        onCancel={() => setAuditModalVisible(false)}
        onOk={handleAuditSubmit}
        confirmLoading={auditLoading}
        okText={auditApproved ? '确认通过' : '确认拒绝'}
        okButtonProps={{ danger: !auditApproved }}
        width={600}
      >
        {selectedStory && (
          <Space direction="vertical" style={{ width: '100%' }} size="middle">
            <Descriptions column={2} bordered size="small">
              <Descriptions.Item label="标题" span={2}>{selectedStory.title}</Descriptions.Item>
              <Descriptions.Item label="情绪">
                <EmotionTag type={selectedStory.emotionType} />
              </Descriptions.Item>
              <Descriptions.Item label="发布者">{selectedStory.displayNickname}</Descriptions.Item>
            </Descriptions>

            <div>
              <strong>内容预览:</strong>
              <div style={{
                marginTop: 8,
                maxHeight: 200,
                overflow: 'auto',
                padding: 12,
                background: '#f5f5f5',
                borderRadius: 4
              }}>
                {selectedStory.content}
              </div>
            </div>

            {!auditApproved && (
              <div>
                <strong>拒绝原因（必填）:</strong>
                <Input.TextArea
                  rows={3}
                  value={rejectReason}
                  onChange={(e: any) => setRejectReason(e.target.value)}
                  placeholder="请填写拒绝原因"
                  style={{ marginTop: 8 }}
                />
              </div>
            )}
          </Space>
        )}
      </Modal>
    </div>
  );
};

export default StoryManagement;