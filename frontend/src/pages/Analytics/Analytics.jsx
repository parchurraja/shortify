import React, { useState, useEffect } from 'react';
import { analyticsService } from '../../services/analyticsService';
import ClickTimelineChart from '../../components/Charts/ClickTimelineChart';
import DeviceChart from '../../components/Charts/DeviceChart';
import OsBrowserChart from '../../components/Charts/OsBrowserChart';
import { SkeletonChart } from '../../components/Common/SkeletonLoader';
import { BarChart3, Calendar, Smartphone, Globe, Monitor } from 'lucide-react';

export const Analytics = () => {
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState(null);
  const [dateRange, setDateRange] = useState('7d'); // '7d', '30d', 'all'

  const fetchAnalytics = async () => {
    setLoading(true);
    try {
      const res = await analyticsService.getDashboardData();
      setDashboardData(res.data);
    } catch (err) {
      console.error('Failed to load analytics', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, []);

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Header section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white font-outfit tracking-tight">
            Traffic Analytics
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Deep dive into device distribution, browsers, and timeline trends
          </p>
        </div>

        {/* Date range filter pill */}
        <div className="inline-flex items-center p-1 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm">
          <button
            onClick={() => setDateRange('7d')}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all ${
              dateRange === '7d'
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            Last 7 Days
          </button>
          <button
            onClick={() => setDateRange('30d')}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all ${
              dateRange === '30d'
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            Last 30 Days
          </button>
          <button
            onClick={() => setDateRange('all')}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold transition-all ${
              dateRange === 'all'
                ? 'bg-indigo-600 text-white shadow-sm'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
            }`}
          >
            All Time
          </button>
        </div>
      </div>

      {/* Main Clicks Timeline Line Chart */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-indigo-50 dark:bg-indigo-950/60 text-indigo-600 dark:text-indigo-400 flex items-center justify-center">
              <BarChart3 className="w-5 h-5 stroke-[2.5]" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-900 dark:text-white font-outfit">Click Traffic Timeline</h2>
              <p className="text-xs text-slate-500 dark:text-slate-400">Total clicks aggregated over time</p>
            </div>
          </div>
        </div>

        {loading ? (
          <SkeletonChart />
        ) : (
          <ClickTimelineChart dataPoints={dashboardData?.recentClicksTimeline || []} />
        )}
      </div>

      {/* Breakdown Grid: Devices, OS, Browsers */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Devices */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm space-y-4">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-purple-50 dark:bg-purple-950/60 text-purple-600 dark:text-purple-400 flex items-center justify-center">
              <Smartphone className="w-4 h-4 stroke-[2.5]" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white font-outfit">Clicks by Device</h3>
              <p className="text-xs text-slate-400">Desktop, Mobile, Tablet</p>
            </div>
          </div>

          {loading ? (
            <div className="h-64 animate-pulse bg-slate-100 dark:bg-slate-800 rounded-2xl" />
          ) : (
            <DeviceChart deviceData={dashboardData?.clicksByDevice || {}} />
          )}
        </div>

        {/* Operating Systems */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm space-y-4">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
              <Monitor className="w-4 h-4 stroke-[2.5]" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white font-outfit">Operating Systems</h3>
              <p className="text-xs text-slate-400">Windows, macOS, Linux, Android, iOS</p>
            </div>
          </div>

          {loading ? (
            <div className="h-64 animate-pulse bg-slate-100 dark:bg-slate-800 rounded-2xl" />
          ) : (
            <OsBrowserChart dataObj={dashboardData?.clicksByOs || {}} title="Operating Systems" />
          )}
        </div>

        {/* Browsers */}
        <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm space-y-4">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-sky-50 dark:bg-sky-950/60 text-sky-600 dark:text-sky-400 flex items-center justify-center">
              <Globe className="w-4 h-4 stroke-[2.5]" />
            </div>
            <div>
              <h3 className="text-base font-bold text-slate-900 dark:text-white font-outfit">Top Browsers</h3>
              <p className="text-xs text-slate-400">Chrome, Safari, Firefox, Edge</p>
            </div>
          </div>

          {loading ? (
            <div className="h-64 animate-pulse bg-slate-100 dark:bg-slate-800 rounded-2xl" />
          ) : (
            <OsBrowserChart dataObj={dashboardData?.clicksByBrowser || {}} title="Browsers" />
          )}
        </div>
      </div>
    </div>
  );
};

export default Analytics;
