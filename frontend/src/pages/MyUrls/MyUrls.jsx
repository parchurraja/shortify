import React, { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { urlService } from '../../services/urlService';
import { SkeletonTableRow } from '../../components/Common/SkeletonLoader';
import { getShortLinkUrl } from '../../utils/shortLink';
import toast from 'react-hot-toast';
import { 
  Search, 
  Plus, 
  Copy, 
  Check, 
  Edit3, 
  Trash2, 
  QrCode, 
  ExternalLink,
  ChevronLeft,
  ChevronRight,
  Filter,
  Link2
} from 'lucide-react';

export const MyUrls = () => {
  const { 
    searchQuery: globalSearch, 
    onOpenCreateModal, 
    onOpenEditModal, 
    onOpenQrModal, 
    onOpenConfirmModal,
    refreshTrigger,
    triggerRefresh 
  } = useOutletContext();

  const [searchTerm, setSearchTerm] = useState(globalSearch || '');
  const [debouncedSearch, setDebouncedSearch] = useState(searchTerm);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sort, setSort] = useState('createdAt,desc');
  const [loading, setLoading] = useState(true);
  const [urlsPage, setUrlsPage] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [copiedId, setCopiedId] = useState(null);

  // Sync global search from Navbar
  useEffect(() => {
    if (globalSearch !== undefined) {
      setSearchTerm(globalSearch);
    }
  }, [globalSearch]);

  // Search debouncing delay (300ms)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPage(0);
    }, 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  const fetchUrls = async () => {
    setLoading(true);
    try {
      const res = await urlService.getUrls(page, size, debouncedSearch, sort);
      setUrlsPage(res.data);
    } catch (err) {
      toast.error('Failed to load short URLs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUrls();
  }, [page, size, debouncedSearch, sort, refreshTrigger]);

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
      message: `Are you sure you want to delete "${urlItem.title || urlItem.shortCode}"? All click analytics will be permanently removed.`,
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
    <div className="space-y-6 animate-fadeIn">
      {/* Header section */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white font-outfit tracking-tight">
            My Short URLs
          </h1>
          <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
            Manage, edit, and inspect all created links ({urlsPage?.totalElements || 0} total)
          </p>
        </div>

        <button
          onClick={onOpenCreateModal}
          className="inline-flex items-center justify-center gap-2 py-2.5 px-5 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-semibold text-sm shadow-lg shadow-indigo-500/25 transition-all transform hover:-translate-y-0.5"
        >
          <Plus className="w-4 h-4 stroke-[2.5]" />
          <span>Create Short URL</span>
        </button>
      </div>

      {/* Filter and Search controls bar */}
      <div className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm flex flex-col md:flex-row items-center justify-between gap-4">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by URL, alias, title..."
            className="w-full pl-10 pr-4 py-2 text-sm rounded-xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/60 text-slate-900 dark:text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto justify-end">
          {/* Items per page selector */}
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500">
            <span>Show:</span>
            <select
              value={size}
              onChange={(e) => {
                setSize(Number(e.target.value));
                setPage(0);
              }}
              className="py-1.5 px-3 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/60 text-slate-900 dark:text-slate-100 text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value={10}>10 per page</option>
              <option value={25}>25 per page</option>
              <option value={50}>50 per page</option>
              <option value={100}>100 per page</option>
            </select>
          </div>

          {/* Sort selector */}
          <div className="flex items-center gap-2 text-xs font-medium text-slate-500">
            <span>Sort:</span>
            <select
              value={sort}
              onChange={(e) => setSort(e.target.value)}
              className="py-1.5 px-3 rounded-lg border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950/60 text-slate-900 dark:text-slate-100 text-xs focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="createdAt,desc">Newest First</option>
              <option value="createdAt,asc">Oldest First</option>
              <option value="clickCount,desc">Most Clicked</option>
              <option value="clickCount,asc">Least Clicked</option>
            </select>
          </div>
        </div>
      </div>

      {/* Main Data Table */}
      <div className="p-6 rounded-3xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 shadow-sm space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 dark:border-slate-800 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                <th className="py-3 px-4">Title & Destination</th>
                <th className="py-3 px-4">Short Code</th>
                <th className="py-3 px-4">Clicks</th>
                <th className="py-3 px-4">Status</th>
                <th className="py-3 px-4">Created Date</th>
                <th className="py-3 px-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/60">
              {loading ? (
                <>
                  <SkeletonTableRow />
                  <SkeletonTableRow />
                  <SkeletonTableRow />
                  <SkeletonTableRow />
                  <SkeletonTableRow />
                </>
              ) : urlsPage?.content?.length === 0 ? (
                <tr>
                  <td colSpan="6" className="py-16 text-center text-slate-400">
                    <div className="flex flex-col items-center justify-center space-y-3">
                      <div className="w-12 h-12 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-slate-400">
                        <Link2 className="w-6 h-6" />
                      </div>
                      <p className="font-semibold text-slate-700 dark:text-slate-300">No matching short links found</p>
                      <p className="text-xs text-slate-400 max-w-xs">
                        Try adjusting your search terms or create a new shortened URL.
                      </p>
                    </div>
                  </td>
                </tr>
              ) : (
                urlsPage.content.map((urlItem) => {
                  console.log(urlItem);
                  return (
                  <tr key={urlItem.id} className="hover:bg-slate-50/60 dark:hover:bg-slate-800/40 transition-colors group">
                    <td className="py-4 px-4 max-w-xs truncate">
                      <div className="font-semibold text-slate-900 dark:text-slate-100 truncate">
                        {urlItem.title || urlItem.originalUrl}
                      </div>
                      <div className="text-xs text-slate-400 truncate">{urlItem.originalUrl}</div>
                    </td>

                    <td className="py-4 px-4">
                      <a
                        href={getShortLinkUrl(urlItem.shortCode, urlItem.shortUrl)}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="font-mono font-semibold text-indigo-600 dark:text-indigo-400 hover:underline inline-flex items-center gap-1"
                      >
                        <span className="truncate max-w-[220px]">/{urlItem.shortCode}</span>
                        <ExternalLink className="w-3.5 h-3.5 opacity-0 group-hover:opacity-100 transition-opacity" />
                      </a>
                    </td>

                    <td className="py-4 px-4 font-semibold text-slate-700 dark:text-slate-300">
                      {urlItem.clickCount ?? 0}
                    </td>

                    <td className="py-4 px-4">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${
                          urlItem.isActive
                            ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/60 dark:text-emerald-400 border border-emerald-200/50 dark:border-emerald-800/40'
                            : 'bg-red-50 text-red-700 dark:bg-red-950/60 dark:text-red-400 border border-red-200/50 dark:border-red-800/40'
                        }`}
                      >
                        {urlItem.isActive ? 'Active' : 'Inactive'}
                      </span>
                    </td>

                    <td className="py-4 px-4 text-xs text-slate-500 dark:text-slate-400">
                      {urlItem.createdAt ? new Date(urlItem.createdAt).toLocaleDateString() : 'N/A'}
                    </td>

                    <td className="py-4 px-4 text-right">
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
                  );
                })}
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination controls */}
        {urlsPage?.totalPages > 1 && (
          <div className="flex items-center justify-between pt-4 border-t border-slate-100 dark:border-slate-800">
            <span className="text-xs text-slate-500 dark:text-slate-400">
              Page {page + 1} of {urlsPage.totalPages}
            </span>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(p - 1, 0))}
                disabled={page === 0}
                className="p-2 rounded-lg border border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => setPage((p) => Math.min(p + 1, urlsPage.totalPages - 1))}
                disabled={page >= urlsPage.totalPages - 1}
                className="p-2 rounded-lg border border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MyUrls;
