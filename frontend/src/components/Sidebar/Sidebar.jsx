import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Link2, BarChart3, Plus, X } from 'lucide-react';

export const Sidebar = ({ isOpen, onClose, onOpenCreateModal }) => {
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'My URLs', path: '/urls', icon: Link2 },
    { name: 'Analytics', path: '/analytics', icon: BarChart3 },
  ];

  return (
    <>
      {/* Mobile backdrop */}
      {isOpen && (
        <div 
          className="fixed inset-0 z-40 bg-slate-900/60 backdrop-blur-sm lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar container */}
      <aside
        className={`fixed lg:sticky top-0 lg:top-16 left-0 z-40 h-screen lg:h-[calc(100vh-4rem)] w-64 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800/80 transform transition-transform duration-200 ease-in-out flex flex-col justify-between p-4 ${
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        <div className="space-y-6">
          {/* Close button on mobile */}
          <div className="flex items-center justify-between lg:hidden pb-2 border-b border-slate-100 dark:border-slate-800">
            <span className="font-bold text-slate-900 dark:text-white">Menu</span>
            <button 
              onClick={onClose} 
              className="p-1 rounded-lg text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Action button */}
          <button
            onClick={() => {
              if (onClose) onClose();
              onOpenCreateModal();
            }}
            className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl bg-gradient-to-r from-emerald-600 to-green-600 hover:from-emerald-500 hover:to-green-500 text-white font-semibold shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/35 transition-all transform hover:-translate-y-0.5 active:translate-y-0"
          >
            <Plus className="w-5 h-5 stroke-[2.5]" />
            <span>Create Short URL</span>
          </button>

          {/* Nav links */}
          <nav className="space-y-1.5">
            {navItems.map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  onClick={onClose}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-3.5 py-2.5 rounded-xl font-medium text-sm transition-all ${
                      isActive
                        ? 'bg-emerald-50 dark:bg-emerald-950/50 text-emerald-700 dark:text-emerald-400 font-semibold shadow-sm'
                        : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800/60 hover:text-slate-900 dark:hover:text-slate-200'
                    }`
                  }
                >
                  <Icon className="w-5 h-5" />
                  <span>{item.name}</span>
                </NavLink>
              );
            })}
          </nav>
        </div>

        {/* Footer info */}
        <div className="pt-4 border-t border-slate-100 dark:border-slate-800 text-xs text-slate-400 dark:text-slate-500 text-center">
          Shortify v1.0 • Built with React & Spring Boot
        </div>
      </aside>
    </>
  );
};

export default Sidebar;
