import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Modal, Card, Descriptions, message, Select, DatePicker, Input } from 'antd';
import { EyeOutlined, SearchOutlined } from '@ant-design/icons';
import { diaryApi } from '../api';
import dayjs from 'dayjs';

interface Diary {
  id: number;
  userId: number;
  username: string;
  content: string;
  emotionCategory: string;
  emotionScore: number;
  emotionTags: string[];
  mediaUrls: string[];
  aiAnalysis: string;
  riskLevel: number;
  createdAt: string;
}

const DiaryManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<Diary[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedDiary, setSelectedDiary] = useState<Diary | null>(null);
  const [filters, setFilters] = useState({ keyword: '', emotionCategory: '', riskLevel: undefined as number | undefined });

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize]);

  const loadData = async () => {
    setLoading(true);
    try {
      const res: any = await diaryApi.list({
        pageNum: pagination.current,
        pageSize: pagination.pageSize,
        ...filters,
      });
      if (res.code === 200 || res.success) {
        setData(res.data?.records || []);
        setPagination({ ...pagination, total: res.data?.total || 0 });
      }
    } catch (error) {
      console.error('加载日记列表失败:', error);
      setData([]);
      setPagination({ ...pagination, total: 0 });
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPagination({ ...pagination, current: 1 });
    loadData();
  };

  const handleView = (record: Diary) => {
    setSelectedDiary(record);
    setDetailVisible(true);
  };

  const emotionMap: Record<string, { label: string; color: string }> = {
    happy: { label: '开心', color: 'green' },
    calm: { label: '平静', color: 'blue' },
    sad: { label: '难过', color: 'gray' },
    anxious: { label: '焦虑', color: 'orange' },
    angry: { label: '愤怒', color: 'red' },
    fear: { label: '害怕', color: 'purple' },
  };

  const riskMap: Record<number, { label: string; color: string }> = {
    0: { label: '正常', color: 'green' },
    1: { label: '关注', color: 'orange' },
    2: { label: '警告', color: 'red' },
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '用户', dataIndex: 'username', key: 'username' },
    { 
      title: '内容', 
      dataIndex: 'content', 
      key: 'content',
      ellipsis: true,
      render: (text: string) => text.length > 30 ? text.substring(0, 30) + '...' : text
    },
    { 
      title: '情绪', 
      dataIndex: 'emotionCategory', 
      key: 'emotionCategory',
      render: (emotion: string) => {
        const emo = emotionMap[emotion] || { label: emotion, color: 'default' };
        return <Tag color={emo.color}>{emo.label}</Tag>;
      }
    },
    { 
      title: '情绪分', 
      dataIndex: 'emotionScore', 
      key: 'emotionScore',
      sorter: (a: Diary, b: Diary) => a.emotionScore - b.emotionScore,
      render: (score: number) => (
        <span style={{ color: score >= 60 ? '#52c41a' : score >= 40 ? '#faad14' : '#ff4d4f' }}>
          {score}
        </span>
      )
    },
    { 
      title: '风险等级', 
      dataIndex: 'riskLevel', 
      key: 'riskLevel',
      render: (risk: number) => {
        const r = riskMap[risk] || { label: '未知', color: 'default' };
        return <Tag color={r.color}>{r.label}</Tag>;
      }
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 140 },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: any, record: Diary) => (
        <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleView(record)}>
          查看详情
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Space wrap>
          <Input.Search
            placeholder="搜索用户/内容"
            style={{ width: 200 }}
            value={filters.keyword}
            onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
            onSearch={handleSearch}
          />
          <Select
            placeholder="情绪类型"
            style={{ width: 120 }}
            allowClear
            value={filters.emotionCategory || undefined}
            onChange={(v) => setFilters({ ...filters, emotionCategory: v || '' })}
          >
            <Select.Option value="happy">开心</Select.Option>
            <Select.Option value="sad">难过</Select.Option>
            <Select.Option value="anxious">焦虑</Select.Option>
            <Select.Option value="angry">愤怒</Select.Option>
            <Select.Option value="fear">害怕</Select.Option>
            <Select.Option value="calm">平静</Select.Option>
          </Select>
          <Select
            placeholder="风险等级"
            style={{ width: 120 }}
            allowClear
            value={filters.riskLevel}
            onChange={(v) => setFilters({ ...filters, riskLevel: v })}
          >
            <Select.Option value={0}>正常</Select.Option>
            <Select.Option value={1}>关注</Select.Option>
            <Select.Option value={2}>警告</Select.Option>
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
        </Space>
      </Card>

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
        title="日记详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={700}
      >
        {selectedDiary && (
          <Card>
            <Descriptions column={2} bordered>
              <Descriptions.Item label="用户">{selectedDiary.username}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{selectedDiary.createdAt}</Descriptions.Item>
              <Descriptions.Item label="情绪类型">
                <Tag color={emotionMap[selectedDiary.emotionCategory]?.color || 'default'}>
                  {emotionMap[selectedDiary.emotionCategory]?.label || selectedDiary.emotionCategory}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="情绪分数">
                <span style={{ color: selectedDiary.emotionScore >= 60 ? '#52c41a' : '#ff4d4f' }}>
                  {selectedDiary.emotionScore}
                </span>
              </Descriptions.Item>
              <Descriptions.Item label="风险等级" span={2}>
                <Tag color={riskMap[selectedDiary.riskLevel]?.color || 'default'}>
                  {riskMap[selectedDiary.riskLevel]?.label || '未知'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="日记内容" span={2}>
                <div style={{ whiteSpace: 'pre-wrap', maxHeight: 200, overflow: 'auto' }}>
                  {selectedDiary.content}
                </div>
              </Descriptions.Item>
              <Descriptions.Item label="情绪标签" span={2}>
                {selectedDiary.emotionTags?.map((tag: string) => (
                  <Tag key={tag}>{tag}</Tag>
                ))}
              </Descriptions.Item>
              <Descriptions.Item label="AI分析" span={2}>
                <div style={{ color: '#666', fontStyle: 'italic' }}>
                  {selectedDiary.aiAnalysis || '暂无分析'}
                </div>
              </Descriptions.Item>
            </Descriptions>
          </Card>
        )}
      </Modal>
    </div>
  );
};

export default DiaryManagement;
