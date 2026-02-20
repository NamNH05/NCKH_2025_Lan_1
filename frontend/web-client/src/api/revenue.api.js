import axiosClient from './axiosClient';

// Use relative path - axiosClient already has baseURL set to API Gateway
const API_BASE_URL = '/v1/revenue';

export const getRevenueSummaryApi = () => {
    return axiosClient.get(`${API_BASE_URL}/summary`);
};

export const getDailyRevenueApi = (startDate, endDate) => {
    return axiosClient.get(`${API_BASE_URL}/daily`, {
        params: { startDate, endDate }
    });
};

