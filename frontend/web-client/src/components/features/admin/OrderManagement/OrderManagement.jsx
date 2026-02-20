import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Popconfirm, Spin, InputNumber } from 'antd';
import orderAPI from '../../../../api/order.api';
import './OrderManagement.css';

const OrderManagement = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingOrder, setEditingOrder] = useState(null);
  const [form] = Form.useForm();
  const [searchTerm, setSearchTerm] = useState('');

  // Use ref to track if component mounted to prevent double API calls in React 18 Strict Mode
  const isMountedRef = React.useRef(false);

  useEffect(() => {
    // Only load orders once on mount, not on every re-render
    if (!isMountedRef.current) {
      isMountedRef.current = true;
      loadOrders();
    }
    
    // Cleanup: reset on unmount
    return () => {
      isMountedRef.current = false;
    };
  }, []);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const response = await orderAPI.getAllOrders();
      const data = response.data || [];
      
      // Development only: Log in development environment
      if (process.env.NODE_ENV === 'development') {
        console.log('✅ Orders loaded successfully:', {
          code: data.code,
          count: data.data?.length || Array.isArray(data) ? data.length : 0,
          message: data.message
        });
      }
      
      // Handle response from gateway wrapper
      const ordersData = Array.isArray(data) ? data : (data.data ? data.data : []);
      setOrders(Array.isArray(ordersData) ? ordersData : []);
    } catch (error) {
      console.error('❌ Failed to load orders:', error);
      message.error('Không thể tải danh sách đơn hàng');
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddOrder = () => {
    setEditingOrder(null);
    form.resetFields();
    setIsModalOpen(true);
  };

  const handleEditOrder = (order) => {
    setEditingOrder(order);
    form.setFieldsValue(order);
    setIsModalOpen(true);
  };

  const handleDeleteOrder = async (id) => {
    try {
      await orderAPI.cancelOrder(id);
      message.success('Xóa đơn hàng thành công');
      loadOrders();
    } catch (error) {
      console.error('Delete failed:', error);
      message.error('Không thể xóa đơn hàng');
    }
  };

  const handleSubmit = async (values) => {
    try {
      if (editingOrder) {
        await orderAPI.updateOrderStatus(editingOrder.id, values.status);
        message.success('Cập nhật đơn hàng thành công');
      } else {
        message.error('Không thể thêm đơn hàng mới từ admin dashboard');
        return;
      }
      setIsModalOpen(false);
      loadOrders();
    } catch (error) {
      console.error('Submit failed:', error);
      message.error('Không thể lưu đơn hàng');
    }
  };

  const columns = [
    {
      title: 'Mã đơn hàng',
      dataIndex: 'id',
      key: 'id',
      render: (id) => `#${id}`,
      width: 100,
    },
    {
      title: 'ID Khách hàng',
      dataIndex: 'customerId',
      key: 'customerId',
      width: 100,
    },
    {
      title: 'Sản phẩm',
      dataIndex: 'itemDetails',
      key: 'itemDetails',
      render: (itemDetails) => {
        try {
          if (!itemDetails) return '-';
          const items = typeof itemDetails === 'string' ? JSON.parse(itemDetails) : itemDetails;
          if (Array.isArray(items) && items.length > 0) {
            return items.map(item => `${item.name} (x${item.quantity})`).join(', ').substring(0, 50) + 
                   (items.map(item => `${item.name} (x${item.quantity})`).join(', ').length > 50 ? '...' : '');
          }
          return '-';
        } catch (e) {
          console.warn('Error parsing items:', e);
          return '-';
        }
      },
      width: 150,
    },
    {
      title: 'Số tiền',
      dataIndex: 'total',
      key: 'total',
      render: (total) => `${Number(total).toLocaleString('vi-VN')} ₫`,
      width: 120,
    },
    {
      title: 'Phí vận chuyển',
      dataIndex: 'shippingFee',
      key: 'shippingFee',
      render: (shippingFee) => shippingFee ? `${Number(shippingFee).toLocaleString('vi-VN')} ₫` : 'Miễn phí',
      width: 120,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const statusColor = {
          'PENDING': '#ffc53d',
          'PAID': '#1890ff',
          'SHIPPED': '#52c41a',
          'DELIVERED': '#13c2c2',
          'CANCELLED': '#ff4d4f'
        };
        const statusLabel = {
          'PENDING': 'Chờ xử lý',
          'PAID': 'Đã thanh toán',
          'SHIPPED': 'Đã gửi',
          'DELIVERED': 'Đã giao',
          'CANCELLED': 'Đã hủy'
        };
        return (
          <span style={{
            color: '#fff',
            backgroundColor: statusColor[status] || '#999',
            padding: '4px 8px',
            borderRadius: '4px',
            fontSize: '12px'
          }}>
            {statusLabel[status] || status}
          </span>
        );
      },
      width: 100,
    },
    {
      title: 'Địa chỉ',
      dataIndex: 'address',
      key: 'address',
      render: (address) => address || 'Chưa xác định',
      width: 150,
    },
    {
      title: 'Ngày đặt',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (createdAt) => createdAt ? new Date(createdAt).toLocaleDateString('vi-VN') : '-',
      width: 100,
    },
    {
      title: 'Hành động',
      key: 'action',
      width: 100,
      render: (_, record) => (
        <Space size="small">
          <Button type="primary" size="small" onClick={() => handleEditOrder(record)}>
            Sửa
          </Button>
          <Popconfirm
            title="Xóa đơn hàng"
            description="Bạn chắc chắn muốn xóa đơn hàng này?"
            onConfirm={() => handleDeleteOrder(record.id)}
            okText="Có"
            cancelText="Không"
          >
            <Button danger size="small">Xóa</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const filteredOrders = orders.filter(order => {
    const search = searchTerm.toLowerCase();
    return !search || 
           `#${order.id}`.toLowerCase().includes(search) ||
           `${order.customerId}`.toLowerCase().includes(search) ||
           order.address?.toLowerCase().includes(search) ||
           order.status?.toLowerCase().includes(search);
  });

  return (
    <Spin spinning={loading}>
      <div className="admin-content">
        <div className="admin-header">
          <h1>Quản Lý Đơn Hàng</h1>
          <Button onClick={() => loadOrders()} style={{ marginLeft: '10px' }}>
            Làm mới
          </Button>
        </div>

        <div style={{ marginBottom: '20px' }}>
          <Input
            placeholder="Tìm kiếm theo mã đơn, ID khách, địa chỉ hoặc trạng thái..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ maxWidth: '400px' }}
          />
        </div>

        <Table
          columns={columns}
          dataSource={filteredOrders}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />

        <Modal
          title="Cập nhật trạng thái đơn hàng"
          open={isModalOpen}
          onOk={() => form.submit()}
          onCancel={() => setIsModalOpen(false)}
        >
          <Form
            form={form}
            layout="vertical"
            onFinish={handleSubmit}
          >
            {editingOrder && (
              <>
                <Form.Item label="Mã đơn hàng">
                  <Input value={`#${editingOrder.id}`} disabled />
                </Form.Item>

                <Form.Item label="ID Khách hàng">
                  <Input value={editingOrder.customerId} disabled />
                </Form.Item>

                <Form.Item label="Số tiền">
                  <Input value={`${Number(editingOrder.total).toLocaleString('vi-VN')} ₫`} disabled />
                </Form.Item>

                <Form.Item label="Địa chỉ">
                  <Input value={editingOrder.address || 'Chưa xác định'} disabled />
                </Form.Item>

                <Form.Item label="Ngày đặt">
                  <Input value={editingOrder.createdAt ? new Date(editingOrder.createdAt).toLocaleDateString('vi-VN') : '-'} disabled />
                </Form.Item>
              </>
            )}

            <Form.Item
              label="Trạng thái"
              name="status"
              rules={[{ required: true, message: 'Vui lòng chọn trạng thái' }]}
            >
              <Select
                options={[
                  { value: 'PENDING', label: 'Chờ xử lý' },
                  { value: 'PAID', label: 'Đã thanh toán' },
                  { value: 'SHIPPED', label: 'Đã gửi' },
                  { value: 'DELIVERED', label: 'Đã giao' },
                  { value: 'CANCELLED', label: 'Đã hủy' }
                ]}
              />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </Spin>
  );
};

export default OrderManagement;