import { createBrowserRouter, Navigate } from 'react-router-dom';
import AdminLayout from './layouts/AdminLayout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import UserManagement from './pages/UserManagement';
import WarningManagement from './pages/WarningManagement';
import KnowledgeManagement from './pages/KnowledgeManagement';
import DiaryManagement from './pages/DiaryManagement';
import RoleManagement from './pages/RoleManagement';
import SystemMonitor from './pages/SystemMonitor';
import StoryManagement from './pages/StoryManagement';
import EsSyncManagement from './pages/EsSyncManagement';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: <AdminLayout />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: 'users', element: <UserManagement /> },
      { path: 'diaries', element: <DiaryManagement /> },
      { path: 'knowledge', element: <KnowledgeManagement /> },
      { path: 'warnings', element: <WarningManagement /> },
      { path: 'roles', element: <RoleManagement /> },
      { path: 'stories', element: <StoryManagement /> },
      { path: 'es-sync', element: <EsSyncManagement /> },
      { path: 'monitor', element: <SystemMonitor /> },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);

export default router;