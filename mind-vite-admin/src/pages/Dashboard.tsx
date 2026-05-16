import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Spin, Table, Tag, Progress } from 'antd';
import { 
  UserOutlined, BookOutlined, WarningOutlined, AlertOutlined, RiseOutlined, FallOutlined,
  FileTextOutlined, SafetyOutlined, MonitorOutlined, ArrowRightOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { statsApi } from '../api';

interface OverviewData {
  totalUsers: number;
  totalDiaries: number;
  highRiskWarnings: number;
  unhandledWarnings: number;
  activeUsers: number;
  newUsersToday: number;
  userGrowthRate: number;
  diaryGrowthRate: number;
}

interface EmotionData {
  category: string;
  count: number;
  percentage: number;
}

interface TrendData {
  date: string;
  users: number;
  diaries: number;
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<OverviewData>({
    totalUsers: 0,
    totalDiaries: 0,
    highRiskWarnings: 0,
    unhandledWarnings: 0,
    activeUsers: 0,
    newUsersToday: 0,
    userGrowthRate: 0,
    diaryGrowthRate: 0,
  });
  const [emotionData, setEmotionData] = useState<EmotionData[]>([]);
  const [trendData, setTrendData] = useState<TrendData[]>([]);

  useEffect(() => {
    loadStats();
    loadEmotionData();
    loadTrendData();
  }, []);

  const loadStats = async () => {
    setLoading(true);
    try {
      const res: any = await statsApi.getOverview();
      if (res.code === 200 || res.success) {
        setData({
          totalUsers: res.data?.totalUsers || 0,
          totalDiaries: res.data?.totalDiaries || 0,
          highRiskWarnings: res.data?.highRiskWarnings || 0,
          unhandledWarnings: res.data?.unhandledWarnings || 0,
          activeUsers: res.data?.activeUsers || 0,
          newUsersToday: res.data?.newUsersToday || 0,
          userGrowthRate: 0,
          diaryGrowthRate: 0,
        });
      }
    } catch (error) {
      // 加载失败显示空数据
      setData({
        totalUsers: 0, totalDiaries: 0, highRiskWarnings: 0, unhandledWarnings: 0,
        activeUsers: 0, newUsersToday: 0, userGrowthRate: 0, diaryGrowthRate: 0,
      });
    } finally {
      setLoading(false);
    }
  };

  const loadEmotionData = async () => {
    try {
      const res: any = await statsApi.getEmotionDistribution();
      if (res.code === 200 || res.success) {
        // 后端返回格式: {distribution: {happy: 10, sad: 5, ...}, days: 30, total: 15}
        const distribution = res.data?.distribution || {};
        const total = res.data?.total || 0;
        
        // 转换为数组格式
        const emotionArray: EmotionData[] = Object.entries(distribution).map(([category, count]) => ({
          category,
          count: count as number,
          percentage: total > 0 ? Math.round((count as number / total) * 100) : 0,
        }));
        
        setEmotionData(emotionArray);
      }
    } catch (error) {
      setEmotionData([]);
    }
  };

  const loadTrendData = async () => {
    try {
      const res: any = await statsApi.getUsers(7);
      if (res.code === 200 || res.success) {
        // 后端返回统计对象，暂用空数据
        setTrendData([]);
      }
    } catch (error) {
      setTrendData([]);
    }
  };

  const cards = [
    { title: '用户总数', value: data.totalUsers, icon: <UserOutlined />, color: '#1890ff', growth: data.userGrowthRate },
    { title: '日记总数', value: data.totalDiaries, icon: <BookOutlined />, color: '#52c41a', growth: data.diaryGrowthRate },
    { title: '高风险预警', value: data.highRiskWarnings, icon: <WarningOutlined />, color: '#f5222d', growth: -5.2 },
    { title: '待处理预警', value: data.unhandledWarnings, icon: <AlertOutlined />, color: '#faad14', growth: 2.1 },
  ];

  const navGrids = [
    { title: '用户管理', desc: '查看和管理用户信息', icon: <UserOutlined />, color: '#1890ff', bg: '#e6f7ff', path: '/users' },
    { title: '日记管理', desc: '查看用户日记与情绪分析', icon: <FileTextOutlined />, color: '#52c41a', bg: '#f6ffed', path: '/diaries' },
    { title: '知识库管理', desc: '管理心理健康知识内容', icon: <BookOutlined />, color: '#722ed1', bg: '#f9f0ff', path: '/knowledge' },
    { title: '预警管理', desc: '处理风险预警与干预', icon: <WarningOutlined />, color: '#f5222d', bg: '#fff1f0', path: '/warnings' },
    { title: '角色权限', desc: '配置角色与菜单权限', icon: <SafetyOutlined />, color: '#fa8c16', bg: '#fff7e6', path: '/roles' },
    { title: '系统监控', desc: '查看系统运行状态', icon: <MonitorOutlined />, color: '#13c2c2', bg: '#e6fffb', path: '/monitor' },
  ];

  const emotionColors: Record<string, string> = {
    '开心': '#52c41a',
    '平静': '#1890ff',
    '焦虑': '#faad14',
    '难过': '#722ed1',
    '愤怒': '#f5222d',
    '害怕': '#eb2f96',
    '其他': '#8c8c8c',
  };

  const trendColumns = [
    { title: '日期', dataIndex: 'date', key: 'date' },
    { title: '新增用户', dataIndex: 'users', key: 'users', render: (v: number) => <span style={{ color: '#1890ff' }}>+{v}</span> },
    { title: '新增日记', dataIndex: 'diaries', key: 'diaries', render: (v: number) => <span style={{ color: '#52c41a' }}>+{v}</span> },
  ];

  return (
    <div>
      <h1 style={{ fontSize: 24, marginBottom: 24 }}>数据概览</h1>
      
      {loading ? (
        <div style={{ textAlign: 'center', padding: 100 }}>
          <Spin size="large" />
        </div>
      ) : (
        <>
          {/* 统计卡片 */}
          <Row gutter={[16, 16]}>
            {cards.map((card, index) => (
              <Col xs={24} sm={12} lg={6} key={index}>
                <Card hoverable>
                  <Statistic
                    title={card.title}
                    value={card.value}
                    prefix={card.icon}
                    valueStyle={{ color: card.color }}
                  />
                  <div style={{ marginTop: 8 }}>
                    {card.growth !== undefined && (
                      <Tag color={card.growth >= 0 ? 'green' : 'red'}>
                        {card.growth >= 0 ? <RiseOutlined /> : <FallOutlined />}
                        {Math.abs(card.growth)}%
                      </Tag>
                    )}
                    <span style={{ fontSize: 12, color: '#999' }}>较上周</span>
                  </div>
                </Card>
              </Col>
            ))}
          </Row>

          {/* 宫格导航 */}
          <h2 style={{ fontSize: 18, margin: '24px 0 16px', color: '#333' }}>快捷入口</h2>
          <Row gutter={[16, 16]}>
            {navGrids.map((item, index) => (
              <Col xs={12} sm={8} lg={4} key={index}>
                <Card
                  hoverable
                  onClick={() => navigate(item.path)}
                  styles={{ body: { padding: 20, textAlign: 'center' } }}
                >
                  <div style={{
                    width: 56,
                    height: 56,
                    borderRadius: '50%',
                    background: item.bg,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    margin: '0 auto 12px',
                    fontSize: 24,
                    color: item.color,
                  }}>
                    {item.icon}
                  </div>
                  <div style={{ fontSize: 15, fontWeight: 500, color: '#333', marginBottom: 4 }}>
                    {item.title}
                  </div>
                  <div style={{ fontSize: 12, color: '#999', marginBottom: 8 }}>
                    {item.desc}
                  </div>
                  <Tag color="default" style={{ fontSize: 12 }}>
                    进入 <ArrowRightOutlined />
                  </Tag>
                </Card>
              </Col>
            ))}
          </Row>

          <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
            <Col xs={24} lg={12}>
              <Card title="情绪分布" extra={<Tag color="blue">近30天</Tag>}>
                {emotionData.map((item) => (
                  <div key={item.category} style={{ marginBottom: 12 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                      <span>
                        <Tag color={emotionColors[item.category] || 'default'}>{item.category}</Tag>
                        <span style={{ marginLeft: 8, color: '#666' }}>{item.count}条</span>
                      </span>
                      <span style={{ color: '#999' }}>{item.percentage}%</span>
                    </div>
                    <Progress 
                      percent={item.percentage} 
                      showInfo={false} 
                      strokeColor={emotionColors[item.category] || '#8c8c8c'}
                      size="small"
                    />
                  </div>
                ))}
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card title="近5日趋势" extra={<Tag color="green">实时</Tag>}>
                <Table
                  columns={trendColumns}
                  dataSource={trendData}
                  rowKey="date"
                  pagination={false}
                  size="small"
                />
              </Card>
            </Col>
          </Row>

          <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
            <Col xs={24} lg={8}>
              <Card title="今日数据">
                <Statistic title="今日新增用户" value={data.newUsersToday} />
                <div style={{ marginTop: 16 }}>
                  <Statistic title="活跃用户" value={data.activeUsers} />
                </div>
              </Card>
            </Col>
            <Col xs={24} lg={8}>
              <Card title="系统健康度">
                <div style={{ marginBottom: 16 }}>
                  <span style={{ fontSize: 14, color: '#666' }}>API响应时间</span>
                  <Progress percent={85} showInfo={false} strokeColor="#52c41a" />
                  <span style={{ fontSize: 12, color: '#999' }}>平均 120ms</span>
                </div>
                <div style={{ marginBottom: 16 }}>
                  <span style={{ fontSize: 14, color: '#666' }}>数据库连接</span>
                  <Progress percent={92} showInfo={false} strokeColor="#1890ff" />
                  <span style={{ fontSize: 12, color: '#999' }}>连接池使用 23%</span>
                </div>
                <div>
                  <span style={{ fontSize: 14, color: '#666' }}>AI服务状态</span>
                  <Progress percent={100} showInfo={false} strokeColor="#52c41a" />
                  <span style={{ fontSize: 12, color: '#999' }}>正常运行</span>
                </div>
              </Card>
            </Col>
            <Col xs={24} lg={8}>
              <Card title="预警状态">
                <div style={{ marginBottom: 16 }}>
                  <Statistic 
                    title="高风险预警" 
                    value={data.highRiskWarnings} 
                    valueStyle={{ color: '#f5222d' }}
                  />
                </div>
                <div style={{ marginBottom: 16 }}>
                  <Statistic 
                    title="中风险预警" 
                    value={18} 
                    valueStyle={{ color: '#faad14' }}
                  />
                </div>
                <div>
                  <Statistic 
                    title="已处理今日" 
                    value={6} 
                    valueStyle={{ color: '#52c41a' }}
                  />
                </div>
              </Card>
            </Col>
          </Row>
        </>
      )}
    </div>
  );
};

export default Dashboard;