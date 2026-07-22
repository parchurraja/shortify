import React, { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { urlService } from '../../services/urlService';
import { analyticsService } from '../../services/analyticsService';
import { SkeletonCard, SkeletonTableRow, SkeletonChart } from '../../components/Common/SkeletonLoader';
import ClickTimelineChart from '../../components/Charts/ClickTimelineChart';
import { getShortLinkUrl } from '../../utils/shortLink';
import toast from 'react-hot-toast';
import { 
  Link2, 
  MousePointerClick, 
  TrendingUp, 
  Award, 
  Copy, 
  Check, 
  Edit3, 
  Trash2, 
  QrCode, 
  ExternalLink,
  Plus,
  Calendar
} from 'lucide-react';

export const Dashboard = () => {
  const { 
    searchQuery, 
    onOpenCreateModal, 
    onOpenEditModal, 
    onOpenQrModal, 
    onOpenConfirmModal,
    refreshTrigger,
    triggerRefresh 
  } = useOutletContext();

  const [loadingStats, setLoadingStats] = useState(true);
  const [loadingUrls, setLoadingUrls] = useState(true);
  const [dashboardData, setDashboardData] = useState(null);
  const [urlsPage, setUrlsPage] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [page, setPage] = useState(0);
  const [copiedId, setCopiedId] = useState(null);

  const fetchDashboardStats = async () => {
    setLoadingStats(true);
    try {
      const res = await analyticsService.getDashboardData();
      setDashboardData(res.data);
    } catch (err) {
      console.error('Failed to fetch dashboard analytics', err);
    } finally {
      setLoadingStats(false);
    }
  };

  const fetchUrls = async () => {
    setLoadingUrls(true);
    try {
      const res = await urlService.getUrls(page, 5, searchQuery);
      setUrlsPage(res.data);
    } catch (err) {
      console.error('Failed to fetch URLs', err);
    } finally {
      setLoadingUrls(false);
    }
  };

  useEffect(() => {
    fetchDashboardStats();
  }, [refreshTrigger]);

  useEffect(() => {
    fetchUrls();
  }, [page, searchQuery, refreshTrigger]);

  const handleCopy = (urlItem) => {
    const fullShortUrl = getShortLinkUrl(urlItem.shortCode, urlItem.shortUrl);
    navigator.clipboard.writeText(fullShortUrl);
    setCopiedId(urlItem.id);
    toast.success('Short link copied!');
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleDelete = (urlItem) => {
    onOpenConfirmModal({
      title: 'Delete Short URL',
      message: `Are you sure you want to delete "${urlItem.title || urlItem.shortCode}"? This action cannot be undone.`,
      onConfirm: async () => {
        try {
          await urlService.deleteUrl(urlItem.id);
          toast.success('URL deleted successfully');
          triggerRefresh();
        } catch (err) {
          toast.error('Failed to delete URL');
        }
      },
    });
  };

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Header section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white font-outfit tracking-tight">
            Dashboard Overview
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Real-time link performance & analytics tracking
          </p>
        </div>

        <button
          onClick={onOpenCreateModal}
          className="inline-flex items-center justify-center gap-2 py-2.5 px-5 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-semibold text-sm shadow-lg shadow-indigo-500/25 transition-all transform hover:-translate-y-0.5 active:translate-y-0"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Shorten New Link</span>
        </button>
      </div>

      {/* 4 Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {loadingStats ? (
          <>
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
            <SkeletonCard />
          </>
        ) : (
          <>
            <div className="p-6 rounded-2xl bg-white dark:bg-slate-900 border border-emerald-200/70 dark:border-emerald-900/40 shadow-sm hover:shadow-md transition-all">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-bold uppercase tracking-wider text-emerald-600 dark:text-emerald-400">Total Links</span>
                <div className="w-10 h-10 rounded-xl bg-emerald-50 dark:bg-emerald-950/60 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
                  <Link2 className="w-5 h-5 stroke-[2.5]" />
                </div>
              </div>
              <div className="text-3xl font-black text-slate-900 dark:text-white font-outfit">
                {dashboardData?.totalUrls ?? 0}
              </div>
              <p className="text-xs text-slate-400 mt-1">Shortened URLs created</p>
            </div>

            <div className="p-6 rounded-2xl bg-white dark:bg-slate-900 border border-orange-200/70 dark:border-orange-900/40 shadow-sm hover:shadow-md transition-all">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-bold uppercase tracking-wider text-orange-600 dark:text-orange-400">Total Clicks</span>
                <div className="w-10 h-10 rounded-xl bg-orange-50 dark:bg-orange-950/60 text-orange-600 dark:text-orange-400 flex items-center justify-center">
                  <MousePointerClick className="w-5 h-5 stroke-[2.5]" />
                </div>
              </div>
              <div className="text-3xl font-black text-slate-900 dark:text-white font-outfit">
                {dashboardData?.totalClicks ?? 0}
              </div>
              <p className="text-xs text-slate-400 mt-1">All-time link redirects</p>
            </div>

            <div className="p-6 rounded-2xl bg-white dark:bg-slate-900 border border-amber-200/70 dark:border-amber-900/40 shadow-sm hover:shadow-md transition-all">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-bold uppercase tracking-wider text-amber-600 dark:text-amber-400">Clicks Today</span>
                <div className="w-10 h-10 rounded-xl bg-amber-50 dark:bg-amber-950/60 text-amber-600 dark:text-amber-400 flex items-center justify-center">
                  <TrendingUp className="w-5 h-5 stroke-[2.5]" />
                </div>
              </div>
              <div className="text-3xl font-black text-slate-900 dark:text-white font-outfit">
                {dashboardData?.clicksToday ?? 0}
              </div>
              <p className="text-xs text-slate-400 mt-1">Clicks recorded past 24 hours</p>
            </div>

            <div className="p-6 rounded-2xl bg-white dark:bg-slate-900 border border-sky-200/70 dark:border-sky-900/40 shadow-sm hover:shadow-md transition-all">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-bold uppercase tracking-wider text-sky-600 dark:text-sky-400">Top Performing</span>
                <div className="w-10 h-10 rounded-xl bg-sky-50 dark:bg-sky-950/60 text-sky-600 dark:text-sky-400 flex items-center justify-center">
                  <Award className="w-5 h-5 stroke-[2.5]" />
                </div>
              </div>
              <div className="text-lg font-bold text-slate-900 dark:text-white font-mono truncate">
                {dashboardData?.topPerformingUrl?.shortCode || 'N/A'}
              </div>
              <p className="text-xs text-slate-400 mt-1">
                {dashboardData?.topPerformingUrl ? `${dashboardData.topPerformingUrl.clickCount} clicks` : 'No clicks yet'}
              </p>
            </div>
          </>
        )}
      </div>

      {/* 7-Day Trend Chart */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-bold text-slate-900 dark:text-white font-outfit">Click Engagement (Last 7 Days)</h2>
            <p className="text-xs text-slate-500 dark:text-slate-400">Daily click traffic distribution</p>
          </div>
          <div className="p-2 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400">
            <Calendar className="w-4 h-4" />
          </div>
        </div>

        {loadingStats ? (
          <SkeletonChart />
        ) : (
          <ClickTimelineChart dataPoints={dashboardData?.recentClicksTimeline || []} />
        )}
      </div>

      {/* Recent URLs Table */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-bold text-slate-900 dark:text-white font-outfit">Recent Links</h2>
          <span className="text-xs text-slate-400">Showing {urlsPage?.content?.length || 0} items</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                <th className="py-3 px-4">Original URL</th>
                <th className="py-3 px-4">Short Link</th>
                <th className="py-3 px-4">Clicks</th>
                <th className="py-3 px-4">Created</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
              {loadingUrls ? (
                <>
                  <SkeletonTableRow />
                  <SkeletonTableRow />
                  <SkeletonTableRow />
                </>
              ) : urlsPage?.content?.length === 0 ? (
                <tr>
                  <td colSpan="5" className="py-12 text-center text-slate-400">
                    <div className="flex flex-col items-center justify-center space-y-2">
                      <Link2 className="w-8 h-8 text-slate-300 dark:text-slate-700" />
                      <p className="font-semibold text-slate-600 dark:text-slate-400">No short links yet</p>
                      <button
                        onClick={onOpenCreateModal}
                        className="text-xs font-semibold text-indigo-600 dark:text-indigo-400 hover:underline"
                      >
                        Create your first link →
                      </button>
                    </div>
                  </td>
                </tr>
              ) : (
                urlsPage.content.map((urlItem) => (
                  <tr key={urlItem.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition-colors group">
                    <td className="py-3.5 px-4 max-w-xs truncate">
                      <div className="font-semibold text-slate-900 dark:text-slate-100 truncate">
                        {urlItem.title || urlItem.originalUrl}
                      </div>
                      <div className="text-xs text-slate-400 truncate">{urlItem.originalUrl}</div>
                    </td>

                    <td className="py-3.5 px-4">
                      <a
                        href={getShortLinkUrl(urlItem.shortCode, urlItem.shortUrl)}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="font-mono font-semibold text-indigo-600 dark:text-indigo-400 hover:underline inline-flex items-center gap-1"
                      >
                        <span className="truncate max-w-[220px]">/{urlItem.shortCode}</span>
                        <ExternalLink className="w-3 h-3 opacity-0 group-hover:opacity-100 transition-opacity" />
                      </a>
                    </td>

                    <td className="py-3.5 px-4 font-semibold text-slate-700 dark:text-slate-300">
                      {urlItem.clickCount ?? 0}
                    </td>

                    <td className="py-3.5 px-4 text-xs text-slate-500 dark:text-slate-400">
                      {urlItem.createdAt ? new Date(urlItem.createdAt).toLocaleDateString() : 'N/A'}
                    </td>

                    <td className="py-3.5 px-4 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button
                          onClick={() => handleCopy(urlItem)}
                          className="p-2 rounded-lg text-slate-500 hover:text-indigo-600 dark:hover:text-indigo-400 hover:bg-indigo-50 dark:hover:bg-indigo-950/50 transition-colors"
                          title="Copy short link"
                        >
                          {copiedId === urlItem.id ? <Check className="w-4 h-4 text-emerald-500" /> : <Copy className="w-4 h-4" />}
                        </button>

                        <button
                          onClick={() => onOpenQrModal(urlItem)}
                          className="p-2 rounded-lg text-slate-500 hover:text-purple-600 dark:hover:text-purple-400 hover:bg-purple-50 dark:hover:bg-purple-950/50 transition-colors"
                          title="View QR Code"
                        >
                          <QrCode className="w-4 h-4" />
                        </button>

                        <button
                          onClick={() => onOpenEditModal(urlItem)}
                          className="p-2 rounded-lg text-slate-500 hover:text-amber-600 dark:hover:text-amber-400 hover:bg-amber-50 dark:hover:bg-amber-950/50 transition-colors"
                          title="Edit link"
                        >
                          <Edit3 className="w-4 h-4" />
                        </button>

                        <button
                          onClick={() => handleDelete(urlItem)}
                          className="p-2 rounded-lg text-slate-500 hover:text-red-600 dark:hover:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/50 transition-colors"
                          title="Delete link"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
