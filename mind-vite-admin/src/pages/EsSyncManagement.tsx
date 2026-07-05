import React, { useState, useEffect, useCallback } from 'react';
import {
  Card, Row, Col, Statistic, Button, Space, Modal, InputNumber, Input,
  message, Tag, Spin, Descriptions, Divider, Table, Select, Tabs,
} from 'antd';
import {
  SyncOutlined,
  ReloadOutlined,
  DatabaseOutlined,
  CloudServerOutlined,
  DeleteOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  SearchOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import { esSyncApi } from '../api';

interface SyncStatus {
  esCount: number;
  dbCount: number;
}

interface AllStatus {
  [type: string]: SyncStatus;
}

const typeConfig: Record<string, { label: string; icon: React.ReactNode; color: string; searchFields: string[] }> = {
  diary: {
    label: '情绪日记', icon: <DatabaseOutlined />, color: '#1677ff',
    searchFields: ['content', 'emotionTags', 'aiAnalysis'],
  },
  story: {
    label: '匿名故事', icon: <CloudServerOutlined />, color: '#722ed1',
    searchFields: ['title', 'content', 'tags'],
  },
};

const emotionTypeOptions = [
  { label: '😊 开心', value: 'happy' },
  { label: '😢 难过', value: 'sad' },
  { label: '😰 焦虑', value: 'anxious' },
  { label: '😡 生气', value: 'angry' },
  { label: '😌 平静', value: 'calm' },
];

const EsSyncManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [syncLoading, setSyncLoading] = useState<Record<string, boolean>>({});
  const [allStatus, setAllStatus] = useState<AllStatus>({});
  const [rebuildModalOpen, setRebuildModalOpen] = useState(false);
  const [rebuildType, setRebuildType] = useState<string>('');
  const [batchSize, setBatchSize] = useState(500);
  const [syncResult, setSyncResult] = useState<Record<string, any> | null>(null);

  // 搜索状态
  const [searchType, setSearchType] = useState<string>('diary');
  const [keyword, setKeyword] = useState('');
  const [searchFilters, setSearchFilters] = useState<Record<string, any>>({});
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [searchTotal, setSearchTotal] = useState(0);
  const [searchPage, setSearchPage] = useState(1);
  const [searchLoading, setSearchLoading] = useState(false);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [detailDoc, setDetailDoc] = useState<Record<string, any> | null>(null);

  // ==================== 同步状态 ====================
  const loadStatus = useCallback(async () => {
    setLoading(true);
    try {
      const res: any = await esSyncApi.getAllStatus();
      if (res.code === 200 || res.success) {
        setAllStatus(res.data || {});
      }
    } catch (error) {
      console.error('加载ES同步状态失败:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStatus();
  }, [loadStatus]);

  const handleFullSync = async (type: string) => {
    setSyncLoading(prev => ({ ...prev, [type]: true }));
    setSyncResult(null);
    try {
      const res: any = await esSyncApi.fullSync(type, batchSize);
      if (res.code === 200 || res.success) {
        setSyncResult(res.data);
        message.success(`${typeConfig[type]?.label || type} 全量同步完成: ${res.data?.syncedCount || 0} 条`);
        loadStatus();
      } else {
        message.error(res.message || '同步失败');
      }
    } catch (error: any) {
      message.error(error?.message || '同步失败');
    } finally {
      setSyncLoading(prev => ({ ...prev, [type]: false }));
    }
  };

  const handleRebuildClick = (type: string) => {
    setRebuildType(type);
    setRebuildModalOpen(true);
  };

  const handleRebuildConfirm = async () => {
    setRebuildModalOpen(false);
    setSyncLoading(prev => ({ ...prev, [rebuildType]: true }));
    setSyncResult(null);
    try {
      const res: any = await esSyncApi.rebuildIndex(rebuildType, batchSize);
      if (res.code === 200 || res.success) {
        setSyncResult(res.data);
        message.success(`${typeConfig[rebuildType]?.label || rebuildType} 索引重建完成`);
        loadStatus();
      } else {
        message.error(res.message || '重建失败');
      }
    } catch (error: any) {
      message.error(error?.message || '重建失败');
    } finally {
      setSyncLoading(prev => ({ ...prev, [rebuildType]: false }));
    }
  };

  // ==================== ES数据搜索 ====================
  const handleSearch = async (page = 1) => {
    setSearchLoading(true);
    try {
      const params: any = { type: searchType, page, size: 10 };
      if (keyword.trim()) params.keyword = keyword.trim();
      Object.entries(searchFilters).forEach(([k, v]) => {
        if (v !== undefined && v !== null && v !== '') params[k] = v;
      });

      const res: any = await esSyncApi.search(params);
      if (res.code === 200 || res.success) {
        setSearchResults(res.data?.records || []);
        setSearchTotal(res.data?.total || 0);
        setSearchPage(page);
      } else {
        message.error(res.message || '搜索失败');
      }
    } catch (error: any) {
      message.error(error?.message || '搜索失败');
    } finally {
      setSearchLoading(false);
    }
  };

  const handleViewDetail = async (record: any) => {
    setDetailDoc(record);
    setDetailModalOpen(true);
  };

  const handleSearchTypeChange = (type: string) => {
    setSearchType(type);
    setSearchFilters({});
    setSearchResults([]);
    setSearchTotal(0);
    setKeyword('');
  };

  // ==================== 搜索表格列 ====================
  const diaryColumns = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '用户ID', dataIndex: 'userId', width: 80 },
    {
      title: '内容', dataIndex: 'content', ellipsis: true,
      render: (text: string) => text?.length > 80 ? text.slice(0, 80) + '...' : text || '-',
    },
    {
      title: '情绪', dataIndex: 'emotionCategory', width: 90,
      render: (cat: string) => {
        const opt = emotionTypeOptions.find(o => o.value === cat);
        return <Tag>{opt?.label || cat || '-'}</Tag>;
      },
    },
    { title: '情绪分', dataIndex: 'emotionScore', width: 85 },
    {
      title: '匹配分', dataIndex: '_score', width: 80,
      render: (s: number) => s ? s.toFixed(2) : '-',
    },
    { title: '创建时间', dataIndex: 'createdAt', width: 170 },
    {
      title: '操作', width: 70,
      render: (_: any, record: any) => (
        <Button type="link" size="small" icon={<EyeOutlined />}
          onClick={() => handleViewDetail(record)}>详情</Button>
      ),
    },
  ];

  const storyColumns = [
    { title: 'ID', dataIndex: 'id', width: 70 },
    { title: '用户ID', dataIndex: 'userId', width: 80 },
    { title: '标题', dataIndex: 'title', ellipsis: true, width: 150 },
    {
      title: '内容', dataIndex: 'content', ellipsis: true,
      render: (text: string) => text?.length > 80 ? text.slice(0, 80) + '...' : text || '-',
    },
    {
      title: '情绪', dataIndex: 'emotionType', width: 90,
      render: (cat: string) => {
        const opt = emotionTypeOptions.find(o => o.value === cat);
        return <Tag>{opt?.label || cat || '-'}</Tag>;
      },
    },
    { title: '标签', dataIndex: 'tags', ellipsis: true, width: 120 },
    {
      title: '状态', dataIndex: 'status', width: 70,
      render: (s: number) => <Tag color={s === 1 ? 'green' : s === 0 ? 'orange' : 'red'}>
        {s === 1 ? '已发布' : s === 0 ? '待审核' : '已下架'}
      </Tag>,
    },
    {
      title: '匹配分', dataIndex: '_score', width: 80,
      render: (s: number) => s ? s.toFixed(2) : '-',
    },
    { title: '发布时间', dataIndex: 'publishedAt', width: 170 },
  ];

  const currentColumns = searchType === 'diary' ? diaryColumns : storyColumns;

  // ==================== 辅助 ====================
  const getSyncHealth = (type: string): { status: 'normal' | 'warning' | 'error'; text: string } => {
    const s = allStatus[type];
    if (!s) return { status: 'error', text: '未知' };
    if (s.esCount < 0 || s.dbCount < 0) return { status: 'error', text: '连接异常' };
    if (s.esCount === 0 && s.dbCount > 0) return { status: 'error', text: 'ES无数据' };
    if (s.esCount < s.dbCount) return { status: 'warning', text: '数据滞后' };
    if (s.esCount === s.dbCount) return { status: 'normal', text: '已同步' };
    return { status: 'warning', text: '数据不一致' };
  };

  const healthTagColors: Record<string, string> = {
    normal: 'green', warning: 'orange', error: 'red',
  };

  // 生成详情展示的字段列表
  const getDetailFields = (): { label: string; key: string }[] => {
    if (searchType === 'diary') {
      return [
        { label: 'ID', key: 'id' }, { label: '用户ID', key: 'userId' },
        { label: '内容', key: 'content' }, { label: '情绪分类', key: 'emotionCategory' },
        { label: '情绪分', key: 'emotionScore' }, { label: '情绪标签', key: 'emotionTags' },
        { label: 'AI分析', key: 'aiAnalysis' }, { label: '创建时间', key: 'createdAt' },
      ];
    }
    return [
      { label: 'ID', key: 'id' }, { label: '用户ID', key: 'userId' },
      { label: '标题', key: 'title' }, { label: '内容', key: 'content' },
      { label: '情绪类型', key: 'emotionType' }, { label: '标签', key: 'tags' },
      { label: '点赞数', key: 'likeCount' }, { label: '评论数', key: 'commentCount' },
      { label: '浏览数', key: 'viewCount' }, { label: '状态', key: 'status' },
      { label: '昵称', key: 'displayNickname' }, { label: '发布时间', key: 'publishedAt' },
      { label: '创建时间', key: 'createdAt' },
    ];
  };

  return (
    <div>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ margin: 0, fontSize: 20, fontWeight: 600 }}>ES 数据同步管理</h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={loadStatus} loading={loading}>刷新状态</Button>
        </Space>
      </div>

      <Tabs defaultActiveKey="sync" items={[
        {
          key: 'sync',
          label: <span><SyncOutlined /> 同步管理</span>,
          children: (
            <>
              {/* 同步状态卡片 */}
              <Spin spinning={loading}>
                <Row gutter={[24, 24]}>
                  {Object.keys(typeConfig).map(type => {
                    const status = allStatus[type];
                    const health = getSyncHealth(type);
                    const config = typeConfig[type];
                    const isSyncing = syncLoading[type];

                    return (
                      <Col xs={24} lg={12} key={type}>
                        <Card
                          title={
                            <Space>
                              <span style={{ color: config.color }}>{config.icon}</span>
                              <span>{config.label}</span>
                              <Tag color={healthTagColors[health.status]}>{health.text}</Tag>
                            </Space>
                          }
                          style={{ height: '100%' }}
                        >
                          <Row gutter={16}>
                            <Col span={12}>
                              <Statistic title="MySQL 记录数" value={status?.dbCount ?? '-'}
                                prefix={<DatabaseOutlined />} valueStyle={{ color: '#1677ff' }} />
                            </Col>
                            <Col span={12}>
                              <Statistic title="ES 文档数" value={status?.esCount ?? '-'}
                                prefix={<CloudServerOutlined />}
                                valueStyle={{ color: status?.esCount === status?.dbCount ? '#52c41a' : '#faad14' }} />
                            </Col>
                          </Row>

                          {status && status.esCount >= 0 && status.dbCount >= 0 && status.esCount !== status.dbCount && (
                            <div style={{
                              marginTop: 16, padding: '8px 12px', background: '#fffbe6',
                              border: '1px solid #ffe58f', borderRadius: 6, fontSize: 13, color: '#ad6800',
                            }}>
                              <WarningOutlined style={{ marginRight: 8 }} />
                              差异: {Math.abs(status.dbCount - status.esCount)} 条记录未同步
                            </div>
                          )}

                          <Divider style={{ margin: '16px 0' }} />
                          <Space wrap>
                            <Button type="primary" icon={<SyncOutlined spin={isSyncing} />}
                              onClick={() => handleFullSync(type)} loading={isSyncing}>
                              全量同步
                            </Button>
                            <Button danger icon={<DeleteOutlined />}
                              onClick={() => handleRebuildClick(type)} loading={isSyncing}>
                              重建索引
                            </Button>
                          </Space>
                        </Card>
                      </Col>
                    );
                  })}
                </Row>
              </Spin>

              {syncResult && (
                <Card title="最近一次同步结果" size="small" style={{ marginTop: 24 }}>
                  <Descriptions column={4} size="small">
                    <Descriptions.Item label="类型">
                      <Tag color={typeConfig[syncResult.type]?.color}>{typeConfig[syncResult.type]?.label}</Tag>
                    </Descriptions.Item>
                    <Descriptions.Item label="同步数量">{syncResult.syncedCount}</Descriptions.Item>
                    <Descriptions.Item label="耗时">{syncResult.elapsedMs}ms</Descriptions.Item>
                    <Descriptions.Item label="状态">
                      <Tag icon={<CheckCircleOutlined />} color="success">完成</Tag>
                    </Descriptions.Item>
                  </Descriptions>
                </Card>
              )}
            </>
          ),
        },
        {
          key: 'search',
          label: <span><SearchOutlined /> ES数据查询</span>,
          children: (
            <div>
              {/* 搜索栏 */}
              <Card size="small" style={{ marginBottom: 16 }}>
                <Row gutter={[12, 12]} align="middle">
                  <Col>
                    <Select value={searchType} onChange={handleSearchTypeChange} style={{ width: 120 }}>
                      <Select.Option value="diary">情绪日记</Select.Option>
                      <Select.Option value="story">匿名故事</Select.Option>
                    </Select>
                  </Col>
                  <Col flex="auto">
                    <Input.Search
                      placeholder={`搜索 ${typeConfig[searchType]?.searchFields?.join('、') || ''} ...`}
                      value={keyword}
                      onChange={e => setKeyword(e.target.value)}
                      onSearch={() => handleSearch(1)}
                      enterButton={<><SearchOutlined /> 搜索</>}
                      allowClear
                    />
                  </Col>
                  {searchType === 'diary' && (
                    <Col>
                      <Select placeholder="情绪筛选" allowClear style={{ width: 130 }}
                        value={searchFilters.emotionCategory}
                        onChange={v => setSearchFilters(f => ({ ...f, emotionCategory: v }))}>
                        {emotionTypeOptions.map(o => (
                          <Select.Option key={o.value} value={o.value}>{o.label}</Select.Option>
                        ))}
                      </Select>
                    </Col>
                  )}
                  {searchType === 'story' && (
                    <>
                      <Col>
                        <Select placeholder="情绪筛选" allowClear style={{ width: 130 }}
                          value={searchFilters.emotionType}
                          onChange={v => setSearchFilters(f => ({ ...f, emotionType: v }))}>
                          {emotionTypeOptions.map(o => (
                            <Select.Option key={o.value} value={o.value}>{o.label}</Select.Option>
                          ))}
                        </Select>
                      </Col>
                      <Col>
                        <Select placeholder="状态筛选" allowClear style={{ width: 110 }}
                          value={searchFilters.status}
                          onChange={v => setSearchFilters(f => ({ ...f, status: v }))}>
                          <Select.Option value={0}>待审核</Select.Option>
                          <Select.Option value={1}>已发布</Select.Option>
                          <Select.Option value={2}>已拒绝</Select.Option>
                        </Select>
                      </Col>
                    </>
                  )}
                </Row>
                <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
                  搜索范围: {typeConfig[searchType]?.searchFields?.join('、')} | 使用 IK 中文分词
                </div>
              </Card>

              {/* 搜索结果 */}
              <Table
                columns={currentColumns}
                dataSource={searchResults}
                rowKey="id"
                loading={searchLoading}
                size="middle"
                pagination={{
                  current: searchPage,
                  pageSize: 10,
                  total: searchTotal,
                  onChange: (p) => handleSearch(p),
                  showTotal: (total) => `共 ${total} 条`,
                  showSizeChanger: false,
                }}
                locale={{ emptyText: keyword ? '无匹配结果' : '输入关键词后点击搜索' }}
              />
            </div>
          ),
        },
      ]} />

      {/* 文档详情弹窗 */}
      <Modal
        title={`${typeConfig[searchType]?.label} 文档详情`}
        open={detailModalOpen}
        onCancel={() => setDetailModalOpen(false)}
        footer={null}
        width={700}
      >
        {detailDoc && (
          <Descriptions column={1} size="small" bordered>
            {getDetailFields().map(f => (
              <Descriptions.Item key={f.key} label={f.label}>
                {f.key === 'content' || f.key === 'aiAnalysis' ? (
                  <div style={{ whiteSpace: 'pre-wrap', maxHeight: 200, overflow: 'auto' }}>
                    {detailDoc[f.key] || '-'}
                  </div>
                ) : f.key === 'emotionScore' && detailDoc[f.key] ? (
                  Number(detailDoc[f.key]).toFixed(2)
                ) : (
                  detailDoc[f.key] !== undefined ? String(detailDoc[f.key]) : '-'
                )}
              </Descriptions.Item>
            ))}
          </Descriptions>
        )}
      </Modal>

      {/* 重建索引确认弹窗 */}
      <Modal
        title={<Space><WarningOutlined style={{ color: '#ff4d4f' }} /><span>确认重建索引</span></Space>}
        open={rebuildModalOpen}
        onOk={handleRebuildConfirm}
        onCancel={() => setRebuildModalOpen(false)}
        okText="确认重建"
        okButtonProps={{ danger: true }}
        cancelText="取消"
      >
        <p>即将重建 <Tag color={typeConfig[rebuildType]?.color}>{typeConfig[rebuildType]?.label}</Tag> 的 ES 索引。</p>
        <p style={{ color: '#ff4d4f' }}>
          <WarningOutlined style={{ marginRight: 8 }} />
          此操作将删除现有索引并重新创建，同步期间搜索功能可能暂时不可用。
        </p>
        <div style={{ marginTop: 16 }}>
          <span style={{ marginRight: 8 }}>每批同步数量:</span>
          <InputNumber min={100} max={2000} step={100} value={batchSize} onChange={v => setBatchSize(v || 500)} />
        </div>
      </Modal>
    </div>
  );
};

export default EsSyncManagement;
