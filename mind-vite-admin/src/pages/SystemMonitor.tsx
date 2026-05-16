import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Statistic, Progress, Table, Spin, Tag } from 'antd';
import { ClusterOutlined, DatabaseOutlined, CloudServerOutlined, DashboardOutlined } from '@ant-design/icons';
import { monitorApi } from '../api';

const SystemMonitor: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [overview, setOverview] = useState<any>({});
  const [redisInfo, setRedisInfo] = useState<any>({});
  const [mysqlInfo, setMysqlInfo] = useState<any>({});
  const [jvmInfo, setJvmInfo] = useState<any>({});
  const [serverInfo, setServerInfo] = useState<any>({});

  useEffect(() => {
    loadMonitorData();
    const timer = setInterval(loadMonitorData, 30000);
    return () => clearInterval(timer);
  }, []);

  const loadMonitorData = async () => {
    setLoading(true);
    try {
      const [overviewRes, redisRes, mysqlRes, jvmRes, serverRes] = await Promise.all([
        monitorApi.getOverview(),
        monitorApi.getRedis(),
        monitorApi.getMysql(),
        monitorApi.getJvm(),
        monitorApi.getServer(),
      ]);

      // 提取data字段，因为API返回格式为 { code, message, data }
      setOverview(overviewRes?.data || {});
      setRedisInfo(redisRes?.data || {});
      setMysqlInfo(mysqlRes?.data || {});
      setJvmInfo(jvmRes?.data || {});  // 修复bug: 之前错误地使用了 jvmInfo
      setServerInfo(serverRes?.data || {});
    } catch (error) {
      console.error('加载监控数据失败', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusTag = (status: string) => {
    const colorMap: any = {
      '正常': 'success',
      '未配置': 'default',
      '异常': 'error',
    };
    return <Tag color={colorMap[status] || 'default'}>{status}</Tag>;
  };

  if (loading && !overview.uptime) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  return (
    <div>
      <Row gutter={[16, 16]}>
        {/* 系统概览 */}
        <Col span={24}>
          <Card title={<><DashboardOutlined /> 系统概览</>} extra={getStatusTag('正常')}>
            <Row gutter={16}>
              <Col span={6}>
                <Statistic title="运行时间" value={overview.uptime || '未知'} />
              </Col>
              <Col span={6}>
                <Statistic title="CPU核心数" value={overview.cpuCores || 0} suffix="核" />
              </Col>
              <Col span={6}>
                <Statistic title="系统负载" value={overview.systemLoad || 0} precision={2} />
              </Col>
              <Col span={6}>
                <Statistic title="JVM最大内存" value={overview.jvmMaxMemory || '未知'} />
              </Col>
            </Row>
          </Card>
        </Col>

        {/* Redis状态 */}
        <Col span={8}>
          <Card title={<><ClusterOutlined /> Redis状态</>} extra={getStatusTag(redisInfo.status)}>
            <Statistic title="版本" value={redisInfo.version || '-'} />
            <Statistic title="已用内存" value={redisInfo.usedMemory || '-'} style={{ marginTop: 16 }} />
            <Statistic title="峰值内存" value={redisInfo.usedMemoryPeak || '-'} style={{ marginTop: 16 }} />
            <Statistic title="连接数" value={redisInfo.connectedClients || 0} style={{ marginTop: 16 }} />
            <Statistic title="命令总数" value={redisInfo.totalCommands || 0} style={{ marginTop: 16 }} />
            <Statistic title="命中率" value={redisInfo.hitRate || '-'} style={{ marginTop: 16 }} />
          </Card>
        </Col>

        {/* MySQL状态 */}
        <Col span={8}>
          <Card title={<><DatabaseOutlined /> MySQL状态</>} extra={getStatusTag(mysqlInfo.status)}>
            <Statistic title="数据库大小" value={mysqlInfo.databaseSize || '-'} />
            <Statistic title="当前连接数" value={mysqlInfo.connections || 0} style={{ marginTop: 16 }} />
            <Statistic title="总连接数" value={mysqlInfo.totalConnections || 0} style={{ marginTop: 16 }} />
            <Statistic title="查询次数" value={mysqlInfo.queries || 0} style={{ marginTop: 16 }} />
            <Statistic title="表数量" value={mysqlInfo.tableCount || 0} style={{ marginTop: 16 }} />
          </Card>
        </Col>

        {/* JVM状态 */}
        <Col span={8}>
          <Card title={<><CloudServerOutlined /> JVM状态</>}>
            <Statistic title="堆内存使用" value={jvmInfo.heapUsed || '-'} />
            <Progress 
              percent={jvmInfo.heapUsagePercent || 0} 
              status={jvmInfo.heapUsagePercent > 80 ? 'exception' : 'normal'}
              style={{ marginTop: 8 }}
            />
            <Statistic title="堆内存最大" value={jvmInfo.heapMax || '-'} style={{ marginTop: 16 }} />
            <Statistic title="线程数" value={jvmInfo.threadCount || 0} style={{ marginTop: 16 }} />
            <Statistic title="峰值线程数" value={jvmInfo.peakThreadCount || 0} style={{ marginTop: 16 }} />
            <Statistic title="已加载类" value={jvmInfo.loadedClassCount || 0} style={{ marginTop: 16 }} />
          </Card>
        </Col>

        {/* 服务器信息 */}
        <Col span={12}>
          <Card title="服务器信息">
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <Statistic title="操作系统" value={serverInfo.osName || '-'} />
              </Col>
              <Col span={12}>
                <Statistic title="系统版本" value={serverInfo.osVersion || '-'} />
              </Col>
              <Col span={12}>
                <Statistic title="Java版本" value={serverInfo.javaVersion || '-'} />
              </Col>
              <Col span={12}>
                <Statistic title="架构" value={serverInfo.osArch || '-'} />
              </Col>
            </Row>
          </Card>
        </Col>

        {/* 磁盘信息 */}
        <Col span={12}>
          <Card title="磁盘使用">
            <Statistic title="总容量" value={overview.diskTotal || '-'} />
            <Progress 
              percent={overview.diskUsagePercent || 0} 
              status={overview.diskUsagePercent > 80 ? 'exception' : 'normal'}
              style={{ marginTop: 8 }}
            />
            <Statistic title="已使用" value={overview.diskUsed || '-'} style={{ marginTop: 16 }} />
            <Statistic title="可用" value={overview.diskFree || '-'} style={{ marginTop: 16 }} />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default SystemMonitor;
