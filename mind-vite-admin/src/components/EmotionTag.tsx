import React from 'react';
import { Tag } from 'antd';

interface EmotionTagProps {
  type: string;
  size?: 'small' | 'default';
}

const emotionMap: Record<string, { label: string; color: string }> = {
  happy: { label: '开心', color: 'green' },
  calm: { label: '平静', color: 'blue' },
  sad: { label: '难过', color: 'default' },
  anxious: { label: '焦虑', color: 'orange' },
  angry: { label: '愤怒', color: 'red' },
  fear: { label: '害怕', color: 'purple' },
};

const EmotionTag: React.FC<EmotionTagProps> = ({ type, size = 'default' }) => {
  const item = emotionMap[type] || { label: '其他', color: 'default' };
  return (
    <Tag 
      color={item.color}
      style={{ marginRight: 0, fontSize: size === 'small' ? 12 : 14 }}
    >
      {item.label}
    </Tag>
  );
};

export default EmotionTag;
export { emotionMap };