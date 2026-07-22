import React, { useRef } from 'react';
import { QRCodeCanvas } from 'qrcode.react';
import { getShortLinkUrl } from '../../utils/shortLink';
import toast from 'react-hot-toast';
import { X, Download, Share2, Link2 } from 'lucide-react';

export const QrCodeModal = ({ urlData, onClose }) => {
  const qrRef = useRef();

  if (!urlData) return null;

  const fullShortUrl = getShortLinkUrl(urlData.shortCode, urlData.shortUrl);

  const downloadQrCode = () => {
    const canvas = qrRef.current.querySelector('canvas');
    if (!canvas) return;
    const url = canvas.toDataURL('image/png');
    const a = document.createElement('a');
    a.href = url;
    a.download = `shortify-qr-${urlData.shortCode}.png`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    toast.success('QR Code downloaded as PNG!');
  };

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: urlData.title || 'Shortened Link',
          text: 'Check out this short link',
          url: fullShortUrl,
        });
      } catch (err) {
        // Ignored share cancel
      }
    } else {
      navigator.clipboard.writeText(fullShortUrl);
      toast.success('Link copied to clipboard!');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/60 backdrop-blur-sm animate-fadeIn">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-6 sm:p-8 max-w-sm w-full shadow-2xl relative text-center">
        <button
          onClick={onClose}
          className="absolute right-4 top-4 p-2 rounded-xl text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="mb-4">
          <h2 className="text-xl font-bold text-slate-900 dark:text-white font-outfit">QR Code</h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 truncate mt-1">
            {urlData.title || urlData.shortCode}
          </p>
        </div>

        {/* QR Canvas Container */}
        <div 
          ref={qrRef} 
          className="p-4 bg-white rounded-2xl shadow-inner border border-slate-100 dark:border-slate-800 inline-block mb-4"
        >
          <QRCodeCanvas
            value={fullShortUrl}
            size={200}
            bgColor="#ffffff"
            fgColor="#0f172a"
            level="H"
            includeMargin={true}
          />
        </div>

        <p className="text-xs font-mono text-indigo-600 dark:text-indigo-400 bg-indigo-50 dark:bg-indigo-950/50 py-1.5 px-3 rounded-lg border border-indigo-100 dark:border-indigo-900/40 mb-6 break-all">
          {fullShortUrl}
        </p>

        <div className="flex items-center justify-center gap-3">
          <button
            onClick={downloadQrCode}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm shadow-md shadow-indigo-500/20 transition-all"
          >
            <Download className="w-4 h-4" />
            <span>Download PNG</span>
          </button>

          <button
            onClick={handleShare}
            className="p-2.5 rounded-xl border border-slate-200 dark:border-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            title="Share or Copy Link"
          >
            <Share2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default QrCodeModal;
