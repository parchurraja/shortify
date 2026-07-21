import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar/Navbar';
import Sidebar from './Sidebar/Sidebar';
import CreateUrlModal from './Modals/CreateUrlModal';
import EditUrlModal from './Modals/EditUrlModal';
import QrCodeModal from './Modals/QrCodeModal';
import ConfirmModal from './Modals/ConfirmModal';

export const DashboardLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Modals state
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [editModalData, setEditModalData] = useState(null);
  const [qrModalData, setQrModalData] = useState(null);
  const [confirmModalData, setConfirmModalData] = useState(null);

  // Refresh trigger state for child views
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const triggerRefresh = () => {
    setRefreshTrigger((prev) => prev + 1);
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-sans antialiased selection:bg-indigo-500 selection:text-white transition-colors duration-200">
      <Navbar
        onToggleSidebar={() => setSidebarOpen(!sidebarOpen)}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
      />

      <div className="flex">
        <Sidebar
          isOpen={sidebarOpen}
          onClose={() => setSidebarOpen(false)}
          onOpenCreateModal={() => setIsCreateModalOpen(true)}
        />

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto w-full min-w-0">
          <Outlet
            context={{
              searchQuery,
              onOpenCreateModal: () => setIsCreateModalOpen(true),
              onOpenEditModal: (urlObj) => setEditModalData(urlObj),
              onOpenQrModal: (urlObj) => setQrModalData(urlObj),
              onOpenConfirmModal: (modalConfig) => setConfirmModalData(modalConfig),
              refreshTrigger,
              triggerRefresh,
            }}
          />
        </main>
      </div>

      {/* Global Modals */}
      {isCreateModalOpen && (
        <CreateUrlModal
          isOpen={isCreateModalOpen}
          onClose={() => setIsCreateModalOpen(false)}
          onSuccess={() => {
            setIsCreateModalOpen(false);
            triggerRefresh();
          }}
        />
      )}

      {editModalData && (
        <EditUrlModal
          urlData={editModalData}
          onClose={() => setEditModalData(null)}
          onSuccess={() => {
            setEditModalData(null);
            triggerRefresh();
          }}
        />
      )}

      {qrModalData && (
        <QrCodeModal
          urlData={qrModalData}
          onClose={() => setQrModalData(null)}
        />
      )}

      {confirmModalData && (
        <ConfirmModal
          {...confirmModalData}
          onClose={() => setConfirmModalData(null)}
        />
      )}
    </div>
  );
};

export default DashboardLayout;
