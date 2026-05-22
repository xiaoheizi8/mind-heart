import React from 'react';
import { Card, Space, Button, Select, Input, Badge } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';

interface FilterItem {
  key: string;
  label: string;
  type: 'select' | 'input';
  placeholder?: string;
  options?: { value: string | number; label: string }[];
  allowClear?: boolean;
  width?: number;
}

interface FilterBarProps {
  filters: Record<string, any>;
  onFilterChange: (key: string, value: any) => void;
  onSearch: () => void;
  onReset: () => void;
  items: FilterItem[];
  extra?: React.ReactNode;
  showBadge?: boolean;
  badgeCount?: number;
}

const FilterBar: React.FC<FilterBarProps> = ({
  filters,
  onFilterChange,
  onSearch,
  onReset,
  items,
  extra,
  showBadge = false,
  badgeCount = 0,
}) => {
  return (
    <Card 
      size="small"
      title={
        <Space>
          {showBadge ? (
            <Badge count={badgeCount} style={{ backgroundColor: '#faad14' }}>
              <span>筛选条件</span>
            </Badge>
          ) : (
            <span>筛选条件</span>
          )}
        </Space>
      }
      extra={
        <Space>
          {extra}
          <Button icon={<ReloadOutlined />} onClick={onReset} size="small">
            重置
          </Button>
          <Button 
            type="primary" 
            icon={<SearchOutlined />} 
            onClick={onSearch}
            size="small"
          >
            搜索
          </Button>
        </Space>
      }
      style={{ marginBottom: 16 }}
    >
      <Space wrap size="middle">
        {items.map(item => (
          <div key={item.key} style={{ minWidth: item.width || 150 }}>
            <span style={{ marginRight: 8, color: '#666' }}>{item.label}:</span>
            {item.type === 'select' ? (
              <Select
                placeholder={item.placeholder || '请选择'}
                value={filters[item.key]}
                onChange={(val) => onFilterChange(item.key, val)}
                allowClear={item.allowClear !== false}
                style={{ width: item.width || 150 }}
                size="small"
              >
                {item.options?.map(opt => (
                  <Select.Option key={opt.value} value={opt.value}>
                    {opt.label}
                  </Select.Option>
                ))}
              </Select>
            ) : (
              <Input
                placeholder={item.placeholder || '请输入'}
                value={filters[item.key]}
                onChange={(e) => onFilterChange(item.key, e.target.value)}
                style={{ width: item.width || 150 }}
                size="small"
              />
            )}
          </div>
        ))}
      </Space>
    </Card>
  );
};

export default FilterBar;