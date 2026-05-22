import React from 'react';
import { Tag } from 'antd';

interface StatusTagProps {
  status: number;
  type?: 'story' | 'warning' | 'feedback';
  size?: 'small' | 'default';
}

const storyStatusMap: Record<number, { label: string; color: string }> = {
  0: { label: '待审核', color: 'orange' },
  1: { label: '已发布', color: 'green' },
  2: { label: '已拒绝', color: 'red' },
  3: { label: '已下架', color: 'default' },
};

const warningStatusMap: Record<number, { label: string; color: string }> = {
  0: { label: '待处理', color: 'orange' },
  1: { label: '处理中', color: 'blue' },
  2: { label: '已处理', color: 'green' },
};

const feedbackStatusMap: Record<number, { label: string; color: string }> = {
  0: { label: '待处理', color: 'orange' },
  1: { label: '处理中', color: 'blue' },
  2: { label: '已解决', color: 'green' },
};

const StatusTag: React.FC<StatusTagProps> = ({ status, type = 'story', size = 'default' }) => {
  let map: Record<number, { label: string; color: string }>;
  
  switch (type) {
    case 'warning':
      map = warningStatusMap;
      break;
    case 'feedback':
      map = feedbackStatusMap;
      break;
    default:
      map = storyStatusMap;
  }
  
  const item = map[status] || { label: '未知', color: 'default' };
  
  return (
    <Tag 
      color={item.color}
      style={{ marginRight: 0, fontSize: size === 'small' ? 12 : 14 }}
    >
      {item.label}
    </Tag>
  );
};

export default StatusTag;
export { storyStatusMap, warningStatusMap, feedbackStatusMap };