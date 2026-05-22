import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/admin/v1';

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
});

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('adminToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => response.data,
  (error: any) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('adminToken');
      window.location.href = '/login';
    }
    return Promise.reject(error.response?.data || error);
  }
);

export interface ApiResponse {
  code?: number;
  success?: boolean;
  message?: string;
  data?: any;
}

export const authApi = {
  login: (data: { username: string; password: string }) => 
    request.post('/auth/login', data),
  logout: () => request.post('/auth/logout'),
};

export const adminAuthApi = {
  login: (data: { username: string; password: string }) => 
    request.post('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  getInfo: () => request.get('/auth/info'),
};

export const statsApi = {
  getOverview: () => request.get('/stats/overview'),
  getUsers: (days?: number) => request.get('/stats/users', { params: { days } }),
  getDiaries: (days?: number) => request.get('/stats/diaries', { params: { days } }),
  getWarnings: () => request.get('/stats/warnings'),
  getEmotionDistribution: () => request.get('/stats/emotion-distribution'),
};

export const menuApi = {
  getUserMenus: () => request.get('/menu/user'),
  getAllMenuTree: () => request.get('/menu/tree'),
  saveRoleMenus: (roleId: number, menuIds: number[]) => 
    request.post(`/menu/role/${roleId}`, menuIds),
  getRoleMenus: (roleId: number) => request.get(`/menu/role/${roleId}`),
};

export const roleApi = {
  list: (params: any) => request.get('/role/list', { params }),
  getById: (id: number) => request.get(`/role/${id}`),
  save: (data: any) => request.post('/role', data),
  update: (id: number, data: any) => request.put(`/role/${id}`, data),
  delete: (id: number) => request.delete(`/role/${id}`),
  getAll: () => request.get('/role/all'),
};

export const monitorApi = {
  getOverview: () => request.get('/monitor/overview'),
  getRedis: () => request.get('/monitor/redis'),
  getMysql: () => request.get('/monitor/mysql'),
  getJvm: () => request.get('/monitor/jvm'),
  getServer: () => request.get('/monitor/server'),
  getApiPerformance: () => request.get('/monitor/api-performance'),
};

// 情绪报告相关API
export const reportApi = {
  // 导出用户报告PDF
  exportUserReport: (userId: number, reportType: string = 'week', days?: number) => {
    const params = new URLSearchParams({ reportType });
    if (days) params.append('days', days.toString());
    return `http://localhost:8080/admin/v1/reports/export/${userId}?${params.toString()}`;
  },
  // 生成用户报告
  generateUserReport: (userId: number, reportType: string = 'week') => 
    request.post(`/reports/generate/${userId}`, null, { params: { reportType } }),
  // 查询用户报告列表
  getUserReports: (userId: number, reportType?: string) => 
    request.get(`/reports/list/${userId}`, { params: { reportType } }),
};

export const userApi = {
  list: (params?: { keyword?: string; status?: number; pageNum?: number; pageSize?: number }) =>
    request.get('/user/list', { params }),
  getById: (id: number) => request.get(`/user/${id}`),
  create: (data: any) => request.post('/user/create', data),
  update: (id: number, data: any) => request.put(`/user/${id}`, data),
  toggleStatus: (id: number, status: number) => request.post(`/user/${id}/status`, { status }),
  resetPassword: (id: number, newPassword: string) => 
    request.post(`/user/${id}/resetPassword`, { newPassword }),
};

export const diaryApi = {
  list: (params?: { keyword?: string; emotionCategory?: string; riskLevel?: number; pageNum?: number; pageSize?: number }) =>
    request.get('/diary/list', { params }),
  getById: (id: number) => request.get(`/diary/${id}`),
  getStats: () => request.get('/diary/stats'),
};

export const knowledgeApi = {
  list: (params?: { keyword?: string; category?: string; status?: number; pageNum?: number; pageSize?: number }) =>
    request.get('/knowledge/list', { params }),
  getById: (id: number) => request.get(`/knowledge/${id}`),
  create: (data: any) => request.post('/knowledge/create', data),
  update: (id: number, data: any) => request.put(`/knowledge/${id}`, data),
  delete: (id: number) => request.delete(`/knowledge/${id}`),
};

export const warningApi = {
  list: (params?: { userId?: number; riskLevel?: number; handled?: number; pageNum?: number; pageSize?: number }) =>
    request.get('/warning/list', { params }),
  getHighRisk: () => request.get('/warning/high-risk'),
  handle: (warningId: number, note?: string) => 
    request.post('/warning/handle', { warningId, note }),
};

export const feedbackApi = {
  list: (params?: { status?: number; pageNum?: number; pageSize?: number }) =>
    request.get('/feedback/list', { params }),
  handle: (id: number, reply: string) => request.post(`/feedback/${id}/handle`, { reply }),
};

export const documentApi = {
  exportKnowledge: (format: 'excel' | 'word' | 'pdf' | 'csv' = 'excel', category?: string) => {
    const token = localStorage.getItem('adminToken');
    const params = new URLSearchParams();
    params.append('format', format);
    if (category) params.append('category', category);
    let url = `${API_BASE_URL}/document/export?${params.toString()}`;
    if (token) {
      url += `&token=${encodeURIComponent(token)}`;
    }
    return url;
  },
  downloadTemplate: () => {
    const token = localStorage.getItem('adminToken');
    let url = `${API_BASE_URL}/document/export/template`;
    if (token) {
      url += `?token=${encodeURIComponent(token)}`;
    }
    return url;
  },
  importKnowledge: (formData: FormData) => 
    request.post('/document/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 60000,
    }),
};

export const storyApi = {
  // 获取待审核列表
  getPendingList: (params?: { page?: number; size?: number; status?: number }) =>
    request.get('/story/pending', { params }),
  // 获取故事详情
  getById: (id: number) => request.get(`/story/${id}`),
  // 审核故事
  audit: (id: number, data: { auditorId: number; approved: boolean; rejectReason?: string }) =>
    request.post(`/story/${id}/audit`, data),
  // 删除故事
  delete: (id: number) => request.delete(`/story/${id}`),
};

export default request;