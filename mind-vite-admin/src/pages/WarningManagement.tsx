import React, { useState, useEffect } from 'react';
import { Table, Tag, Button, Modal, Input, message, Card, Row, Col } from 'antd';
import { warningApi } from '../api';

interface Warning {
  id: number;
  userId: number;
  riskLevel: number;
  triggerType: string;
  content: string;
  handled: number;
  handlerNote?: string;
  createdAt: string;
  handledAt?: string;
}

const WarningManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<Warning[]>([]);
  const [highRiskData, setHighRiskData] = useState<Warning[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [handleModalVisible, setHandleModalVisible] = useState(false);
  const [selectedWarning, setSelectedWarning] = useState<Warning | null>(null);
  const [handleNote, setHandleNote] = useState('');

  useEffect(() => {
    loadData();
    loadHighRisk();
  }, [pagination.current, pagination.pageSize]);

  const loadData = async () => {
    setLoading(true);
    try {
      const res: any = await warningApi.list({
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
      });
      if (res.code === 200 || res.success) {
        setData(res.data?.records || []);
        setPagination({ ...pagination, total: res.data?.total || 0 });
      }
    } catch (error) {
      console.error('Failed to load warnings:', error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const loadHighRisk = async () => {
    try {
      const res: any = await warningApi.getHighRisk();
      if (res.code === 200 || res.success) {
        setHighRiskData(Array.isArray(res.data) ? res.data : []);
      }
    } catch (error) {
      console.error('Failed to load high risk:', error);
      setHighRiskData([]);
    }
  };

  const handleOpen = (record: Warning) => {
    setSelectedWarning(record);
    setHandleNote(record.handlerNote || '');
    setHandleModalVisible(true);
  };

  const handleSubmit = async () => {
    if (!selectedWarning) return;
    try {
      await warningApi.handle(selectedWarning.id, handleNote);
      message.success('处理成功');
      setHandleModalVisible(false);
      loadData();
      loadHighRisk();
    } catch (error) {
      message.error('处理失败');
    }
  };

  const getRiskColor = (level: number) => {
    switch (level) {
      case 3: return 'red';
      case 2: return 'orange';
      default: return 'green';
    }
  };

  const getRiskText = (level: number) => {
    switch (level) {
      case 3: return '高风险';
      case 2: return '中风险';
      default: return '低风险';
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '用户ID', dataIndex: 'userId', key: 'userId', width: 80 },
    { 
      title: '风险等级', 
      dataIndex: 'riskLevel', 
      key: 'riskLevel',
      render: (level: number) => (
        <Tag color={getRiskColor(level)}>{getRiskText(level)}</Tag>
      )
    },
    { title: '触发类型', dataIndex: 'triggerType', key: 'triggerType' },
    { 
      title: '内容', 
      dataIndex: 'content', 
      key: 'content',
      ellipsis: true,
      render: (text: string) => text?.substring(0, 50) + (text?.length > 50 ? '...' : '')
    },
    { 
      title: '状态', 
      dataIndex: 'handled', 
      key: 'handled',
      render: (handled: number) => (
        <Tag color={handled === 1 ? 'green' : 'red'}>
          {handled === 1 ? '已处理' : '待处理'}
        </Tag>
      )
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Warning) => (
        record.handled === 0 ? (
          <Button type="link" size="small" onClick={() => handleOpen(record)}>
            处理
          </Button>
        ) : null
      ),
    },
  ];

  return (
    <div>
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col span={24}>
          <Card 
            title="⚠️ 待处理高风险预警" 
            extra={`共 ${highRiskData.length} 条`}
            style={{ borderColor: '#f5222d' }}
          >
            {highRiskData.length === 0 ? (
              <div style={{ color: '#52c41a' }}>暂无高风险预警 ✓</div>
            ) : (
              <Table
                dataSource={highRiskData}
                columns={columns}
                rowKey="id"
                pagination={false}
                size="small"
              />
            )}
          </Card>
        </Col>
      </Row>

      <h2 style={{ marginBottom: 16 }}>预警列表</h2>
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
        title="处理预警"
        open={handleModalVisible}
        onOk={handleSubmit}
        onCancel={() => setHandleModalVisible(false)}
      >
        <div style={{ marginBottom: 16 }}>
          <p><strong>风险等级：</strong><Tag color={getRiskColor(selectedWarning?.riskLevel || 1)}>{getRiskText(selectedWarning?.riskLevel || 1)}</Tag></p>
          <p><strong>触发内容：</strong></p>
          <div style={{ background: '#f5f5f5', padding: 12, borderRadius: 4 }}>
            {selectedWarning?.content}
          </div>
        </div>
        <Input.TextArea 
          rows={4} 
          value={handleNote}
          onChange={(e) => setHandleNote(e.target.value)}
          placeholder="请输入处理备注..."
        />
      </Modal>
    </div>
  );
};

export default WarningManagement;