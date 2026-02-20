import React, { useState, useEffect } from 'react';
import { Spin, message, Statistic, Row, Col, Card, Table } from 'antd';
import { ArrowUpOutlined } from '@ant-design/icons';
import { getRevenueSummaryApi, getDailyRevenueApi } from '../../../../api/revenue.api';
import './RevenueDashboard.css';

const RevenueDashboard = () => {
  const [loading, setLoading] = useState(false);

  // State cho dữ liệu từ API
  const [revenueData, setRevenueData] = useState([]);
  const [summary, setSummary] = useState({
    totalRevenue: 0,
    totalOrders: 0,
    averageOrderValue: 0
  });

  // Load dữ liệu khi component mount
  useEffect(() => {
    loadRevenueData();
  }, []);

  const loadRevenueData = async () => {
    try {
      setLoading(true);
      
      // Lấy dữ liệu tóm tắt
      const summaryRes = await getRevenueSummaryApi();
      setSummary(summaryRes.data || {});
      
      // Lấy dữ liệu doanh thu hàng ngày
      const dailyRes = await getDailyRevenueApi();
      setRevenueData(Array.isArray(dailyRes.data) ? dailyRes.data : []);
    } catch (error) {
      console.error('Failed to load revenue data:', error);
      message.error('Không thể tải dữ liệu doanh thu');
      setSummary({
        totalRevenue: 0,
        totalOrders: 0,
        averageOrderValue: 0
      });
      setRevenueData([]);
    } finally {
      setLoading(false);
    }
  };

  const totalRevenue = summary.totalRevenue || 0;
  const totalOrders = summary.totalOrders || 0;
  const avgOrderValue = summary.averageOrderValue || 0;

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      maximumFractionDigits: 0,
    }).format(value || 0);
  };

  // Cột cho bảng doanh thu hàng ngày
  const revenueColumns = [
    {
      title: 'Ngày',
      dataIndex: 'date',
      key: 'date',
      render: (text) => new Date(text).toLocaleDateString('vi-VN'),
    },
    {
      title: 'Doanh Thu',
      dataIndex: 'amount',
      key: 'amount',
      render: (text) => formatCurrency(text),
    },
    {
      title: 'Số Đơn Hàng',
      dataIndex: 'orders',
      key: 'orders',
    },
    {
      title: 'Sản Phẩm Bán Ra',
      dataIndex: 'products',
      key: 'products',
    },
    {
      title: 'Trạng Thái',
      dataIndex: 'status',
      key: 'status',
      render: () => <span className="status-badge status-completed">Hoàn tất</span>,
    },
  ];


  return (
    <div className="revenue-dashboard">
      <Spin spinning={loading} size="large">
        <div className="admin-header">
          <h1>Báo Cáo Doanh Thu</h1>
        </div>

        {/* Thống kê tổng quan */}
        <Row gutter={16} className="stats-section">
          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Tổng Doanh Thu"
                value={totalRevenue}
                prefix={<span style={{ fontSize: '12px' }}>đ</span>}
                styles={{ content: { color: '#1890ff', fontSize: '20px' } }}
                formatter={(value) => formatCurrency(value)}
              />
              <p style={{ fontSize: '12px', color: '#999', marginTop: '8px' }}>Từ đầu tháng</p>
            </Card>
          </Col>

          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Tổng Đơn Hàng"
                value={totalOrders}
                styles={{ content: { color: '#52c41a', fontSize: '20px' } }}
              />
              <p style={{ fontSize: '12px', color: '#999', marginTop: '8px' }}>Đơn hàng hoàn tất</p>
            </Card>
          </Col>

          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Giá Trị Trung Bình"
                value={avgOrderValue}
                prefix={<span style={{ fontSize: '12px' }}>đ</span>}
                styles={{ content: { color: '#faad14', fontSize: '20px' } }}
                formatter={(value) => formatCurrency(value)}
              />
              <p style={{ fontSize: '12px', color: '#999', marginTop: '8px' }}>Mỗi đơn hàng</p>
            </Card>
          </Col>

          <Col xs={24} sm={12} lg={6}>
            <Card>
              <Statistic
                title="Tăng Trưởng"
                value={0}
                suffix="%"
                styles={{ content: { color: '#999', fontSize: '20px' } }}
              />
              <p style={{ fontSize: '12px', color: '#999', marginTop: '8px' }}>Đang cập nhật</p>
            </Card>
          </Col>
        </Row>

        {/* Bảng doanh thu hàng ngày */}
        <Card title="Chi Tiết Doanh Thu Hàng Ngày" style={{ marginTop: '24px' }} className="admin-card">
          <Table
            columns={revenueColumns}
            dataSource={revenueData.map((item, index) => ({ ...item, _key: item.date || index }))}
            loading={loading}
            rowKey="_key"
            locale={{ emptyText: 'Chưa có dữ liệu' }}
            pagination={{ pageSize: 10 }}
          />
        </Card>
      </Spin>
    </div>
  );
};

export default RevenueDashboard;
