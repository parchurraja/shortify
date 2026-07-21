import React from 'react';

export const SkeletonCard = () => (
  <div className="p-6 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 animate-pulse shadow-sm">
    <div className="flex items-center justify-between mb-4">
      <div className="h-4 w-24 bg-slate-200 dark:bg-slate-800 rounded"></div>
      <div className="w-10 h-10 bg-slate-200 dark:bg-slate-800 rounded-xl"></div>
    </div>
    <div className="h-8 w-16 bg-slate-200 dark:bg-slate-800 rounded mb-2"></div>
    <div className="h-3 w-32 bg-slate-100 dark:bg-slate-800/60 rounded"></div>
  </div>
);

export const SkeletonTableRow = () => (
  <tr className="border-b border-slate-100 dark:border-slate-800/60 animate-pulse">
    <td className="py-4 px-4"><div className="h-4 w-40 bg-slate-200 dark:bg-slate-800 rounded"></div></td>
    <td className="py-4 px-4"><div className="h-4 w-28 bg-slate-200 dark:bg-slate-800 rounded"></div></td>
    <td className="py-4 px-4"><div className="h-4 w-12 bg-slate-200 dark:bg-slate-800 rounded"></div></td>
    <td className="py-4 px-4"><div className="h-4 w-24 bg-slate-200 dark:bg-slate-800 rounded"></div></td>
    <td className="py-4 px-4"><div className="h-8 w-32 bg-slate-200 dark:bg-slate-800 rounded-lg ml-auto"></div></td>
  </tr>
);

export const SkeletonChart = () => (
  <div className="p-6 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800/80 animate-pulse h-80 flex flex-col justify-between">
    <div className="h-5 w-40 bg-slate-200 dark:bg-slate-800 rounded"></div>
    <div className="flex items-end gap-3 h-48 w-full pt-6">
      {[40, 70, 45, 90, 60, 80, 50].map((h, i) => (
        <div key={i} style={{ height: `${h}%` }} className="flex-1 bg-slate-200 dark:bg-slate-800 rounded-t-lg"></div>
      ))}
    </div>
  </div>
);
