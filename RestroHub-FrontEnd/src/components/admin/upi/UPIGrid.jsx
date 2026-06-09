import { useState, useEffect } from 'react';
import { RefreshCw, AlertCircle, CreditCard } from 'lucide-react';
import UPICard from './UPICard';
import api from '@services/common/api';
import AdminSkeleton from '../AdminSkeleton';

const normalizeLink = (link) => ({
  id: link.id,
  name: link.name,
  upiId: link.upiId,
  isDefault: Boolean(link.defaultLink),
  transactions: link.transactions || 0,
  revenue: Number(link.revenue || 0),
  paymentUrl: link.paymentUrl,
});

const UPIGrid = ({ onTest, onCountChange, refreshKey }) => {
  const [upiLinks, setUpiLinks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [copiedId, setCopiedId] = useState(null);

  const fallbackLinks = [
    {
      id: 1, name: 'Main Account', upiId: 'restaurant@paytm',
      isDefault: true, transactions: 89, revenue: 45230,
    },
    {
      id: 2, name: 'Backup Account', upiId: 'restaurant@upi',
      isDefault: false, transactions: 12, revenue: 5670,
    },
  ];

  useEffect(() => {
    fetchLinks();
  }, [refreshKey]);

  const fetchLinks = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.get('/secure/api/v1/upi-links');
      const links = response.data.map(normalizeLink);
      setUpiLinks(links);
      onCountChange?.(links.length);
    } catch (err) {
      console.error('Fetch failed:', err);
      setError('Failed to load UPI links');
      setUpiLinks(fallbackLinks);
      onCountChange?.(fallbackLinks.length);
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = (upiId, id) => {
    navigator.clipboard.writeText(upiId);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleSetDefault = async (id) => {
    const response = await api.put(`/secure/api/v1/upi-links/${id}/default`);
    const updated = normalizeLink(response.data);
    setUpiLinks((prev) => prev.map((link) => ({
      ...link,
      isDefault: link.id === updated.id,
      ...(link.id === updated.id ? updated : {}),
    })));
  };

  const handleDelete = async (id) => {
    await api.delete(`/secure/api/v1/upi-links/${id}`);
    setUpiLinks((prev) => {
      const next = prev.filter((link) => link.id !== id);
      onCountChange?.(next.length);
      return next;
    });
  };

  if (loading) {
    return (
      <div className="grid grid-cols-1 gap-4 sm:gap-6 md:grid-cols-2">
        {[1, 2].map((i) => (
          <AdminSkeleton key={i} variant="upi" />
        ))}
      </div>
    );
  }

  if (error && upiLinks.length === 0) {
    return (
      <div className="rounded-2xl border border-gray-200 bg-white px-6 py-12 text-center sm:py-16">
        <AlertCircle className="mx-auto mb-4 h-12 w-12 text-red-300 sm:h-16 sm:w-16" />
        <p className="text-sm font-medium text-red-600 sm:text-base">{error}</p>
        <button
          onClick={fetchLinks}
          className="
            mt-4 inline-flex items-center gap-2 rounded-lg
            bg-blue-50 px-4 py-2 text-sm font-medium text-blue-700
            hover:bg-blue-100 transition-colors
          "
        >
          <RefreshCw className="h-4 w-4" />
          Try Again
        </button>
      </div>
    );
  }

  if (upiLinks.length === 0) {
    return (
      <div className="rounded-2xl border border-gray-200 bg-white px-6 py-12 text-center sm:py-16">
        <CreditCard className="mx-auto mb-4 h-12 w-12 text-blue-200 sm:h-16 sm:w-16" />
        <p className="font-medium text-gray-700">No UPI links yet</p>
        <p className="mt-1 text-sm text-gray-500">
          Add your first UPI link to accept payments
        </p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4 sm:gap-6 md:grid-cols-2">
      {upiLinks.map((link) => (
        <UPICard
          key={link.id}
          link={link}
          copiedId={copiedId}
          onCopy={handleCopy}
          onSetDefault={handleSetDefault}
          onDelete={handleDelete}
          onTest={onTest}
        />
      ))}
    </div>
  );
};

export default UPIGrid;
