import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Modal, Form, Input, InputNumber, message, Space, Popconfirm } from 'antd';
import logger from '../../utils/logger';
import { useAuth } from '../../context/AuthContext';
import AdminLayout from '../../components/ui/AdminLayout/AdminLayout';
import { getAllProductsApi, createProductApi, updateProductApi, deleteProductApi } from '../../api/product.api';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    if (!user || (user.role && !user.role.toUpperCase().includes('ADMIN'))) {
      message.error('Bạn không có quyền truy cập trang này');
      navigate('/');
      return;
    }
    loadProducts();
  }, [user, navigate]);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const response = await getAllProductsApi();
      const data = response.data || [];
      setProducts(Array.isArray(data) ? data : []);
    } catch (error) {
      logger.error('Failed to load products:', error);
      message.error('Không thể tải danh sách sản phẩm');
      setProducts([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddProduct = () => {
    setEditingProduct(null);
    form.resetFields();
    setIsModalOpen(true);
  };

  const handleEditProduct = (product) => {
    setEditingProduct(product);
    form.setFieldsValue(product);
    setIsModalOpen(true);
  };

  const handleDeleteProduct = async (id) => {
    try {
      await deleteProductApi(id);
      message.success('Xóa sản phẩm thành công');
      loadProducts();
    } catch (error) {
      logger.error('Delete failed:', error);
      message.error('Không thể xóa sản phẩm');
    }
  };

  const handleSubmit = async (values) => {
    try {
      if (editingProduct) {
        await updateProductApi(editingProduct.id, values);
        message.success('Cập nhật sản phẩm thành công');
      } else {
        await createProductApi(values);
        message.success('Thêm sản phẩm thành công');
      }
      setIsModalOpen(false);
      loadProducts();
    } catch (error) {
      logger.error('Submit failed:', error);
      message.error('Không thể lưu sản phẩm');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'Tên sản phẩm',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Danh mục',
      dataIndex: 'category',
      key: 'category',
      width: 100,
    },
    {
      title: 'Giá',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (price) => `${price?.toLocaleString()}đ`,
    },
    {
      title: 'Số lượng',
      dataIndex: 'quantum',
      key: 'quantum',
      width: 100,
    },
    {
      title: 'Hành động',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space size="small">
          <Button type="primary" size="small" onClick={() => handleEditProduct(record)}>
            Sửa
          </Button>
          <Popconfirm
            title="Xóa sản phẩm"
            description="Bạn chắc chắn muốn xóa sản phẩm này?"
            onConfirm={() => handleDeleteProduct(record.id)}
            okText="Có"
            cancelText="Không"
          >
            <Button danger size="small">Xóa</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <AdminLayout activePage="products">
      <div className="admin-header">
        <h1>Quản Lý Sản Phẩm</h1>
        <Button type="primary" onClick={handleAddProduct}>
          Thêm Sản Phẩm
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={products}
        loading={loading}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />

      <Modal
        title={editingProduct ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm'}
        open={isModalOpen}
        onOk={() => form.submit()}
        onCancel={() => setIsModalOpen(false)}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Form.Item
            label="Tên sản phẩm"
            name="name"
            rules={[{ required: true, message: 'Vui lòng nhập tên sản phẩm' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Danh mục"
            name="category"
            rules={[{ required: true, message: 'Vui lòng nhập danh mục' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Giá"
            name="price"
            rules={[{ required: true, message: 'Vui lòng nhập giá' }]}
          >
            <InputNumber min={0} />
          </Form.Item>

          <Form.Item
            label="Số lượng"
            name="quantum"
            rules={[{ required: true, message: 'Vui lòng nhập số lượng' }]}
          >
            <InputNumber min={0} />
          </Form.Item>

          <Form.Item
            label="Hình ảnh URL"
            name="imageUrl"
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="Mô tả"
            name="description"
          >
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </AdminLayout>
  );
};

export default AdminDashboard;
