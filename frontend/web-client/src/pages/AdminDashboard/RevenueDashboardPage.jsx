import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import { useAuth } from '../../context/AuthContext';
import AdminLayout from '../../components/ui/AdminLayout/AdminLayout';
import RevenueDashboard from '../../components/features/admin/RevenueDashboard/RevenueDashboard';

const RevenueDashboardPage = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    if (!user || (user.role && !user.role.toUpperCase().includes('ADMIN'))) {
      message.error('Bạn không có quyền truy cập trang này');
      navigate('/');
    }
  }, [user, navigate]);

  return (
    <AdminLayout activePage="revenue">
      <RevenueDashboard />
    </AdminLayout>
  );
};

export default RevenueDashboardPage;
