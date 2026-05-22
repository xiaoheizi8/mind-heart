import React from 'react';
import { Table, Tag, Button, Space } from 'antd';
import { EyeOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

interface Column {
  key: string;
  title: string;
  dataIndex?: string;
  width?: number | string;
  ellipsis?: boolean;
  render?: (value: any, record: any, index: number) => React.ReactNode;
}

interface ActionButton {
  key: string;
  label: string;
  icon?: React.ReactNode;
  danger?: boolean;
  type?: 'text' | 'link' | 'default';
  visible?: (record: any) => boolean;
  onClick: (record: any) => void;
}

interface DataTableProps {
  columns: Column[];
  dataSource: any[];
  loading?: boolean;
  pagination?: any;
  onChange?: (pagination: any, filters: any, sorter: any) => void;
  rowKey?: string;
  actions?: ActionButton[];
  size?: 'small' | 'middle' | 'large';
}

const DataTable: React.FC<DataTableProps> = ({
  columns,
  dataSource,
  loading = false,
  pagination,
  onChange,
  rowKey = 'id',
  actions = [],
  size = 'middle',
}) => {
  // 处理操作列
  const actionColumn: Column | null = actions.length > 0 ? {
    key: 'action',
    title: '操作',
    width: actions.length * 80 + 40,
    render: (_: any, record: any) => (
      <Space size="small">
        {actions.map(action => {
          const isVisible = action.visible ? action.visible(record) : true;
          if (!isVisible) return null;
          
          return (
            <Button
              key={action.key}
              type={action.type || 'text'}
              size="small"
              danger={action.danger}
              icon={action.icon}
              onClick={() => action.onClick(record)}
            >
              {action.label}
            </Button>
          );
        })}
      </Space>
    ),
  } : null;

  const finalColumns = actionColumn ? [...columns, actionColumn] : columns;

  return (
    <Table
      columns={finalColumns}
      dataSource={dataSource}
      rowKey={rowKey}
      loading={loading}
      pagination={pagination}
      onChange={onChange}
      size={size}
      scroll={{ x: 'max-content' }}
    />
  );
};

export default DataTable;