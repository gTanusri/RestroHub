import { useState } from 'react';
import { X, Loader2, CreditCard, Info } from 'lucide-react';
import { Dialog } from '@headlessui/react';
import api from '@services/common/api';

const UPIFormModal = ({ isOpen, onClose, onSaved }) => {
  const [formData, setFormData] = useState({ name: '', upiId: '', defaultLink: false });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setSubmitting(true);
      setError(null);
      await api.post('/secure/api/v1/upi-links', formData);
      onSaved?.();
      onClose();
      setFormData({ name: '', upiId: '', defaultLink: false });
    } catch (err) {
      console.error('Failed:', err);
      setError(err.response?.data?.message || 'Failed to add UPI link');
    } finally {
      setSubmitting(false);
    }
  };

  const inputClass = `
    w-full rounded-lg border border-gray-200 bg-white
    px-4 py-2.5 text-sm text-gray-900 placeholder-gray-400
    outline-none transition-all
    focus:border-blue-300 focus:ring-2 focus:ring-blue-100
  `;

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-black/30 backdrop-blur-sm" />
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel
          className="
            w-full max-w-md overflow-hidden rounded-2xl
            border border-gray-200 bg-white shadow-xl
          "
        >
          <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4 sm:px-6">
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-50">
                <CreditCard className="h-5 w-5 text-blue-600" />
              </div>
              <Dialog.Title className="text-lg font-bold text-gray-900">
                Add UPI Link
              </Dialog.Title>
            </div>
            <button
              onClick={onClose}
              className="
                inline-flex h-8 w-8 items-center justify-center
                rounded-lg text-gray-400 hover:bg-gray-100 hover:text-gray-600
                transition-colors
              "
            >
              <X className="h-5 w-5" />
            </button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="space-y-4 px-5 py-5 sm:px-6">
              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-800">
                  Account Name
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  className={inputClass}
                  placeholder="e.g., Main Account"
                  required
                />
              </div>

              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-800">
                  UPI ID
                </label>
                <input
                  type="text"
                  value={formData.upiId}
                  onChange={(e) =>
                    setFormData({ ...formData, upiId: e.target.value })
                  }
                  className={inputClass}
                  placeholder="yourname@paytm"
                  required
                />
                <p className="mt-1.5 flex items-start gap-1.5 text-xs text-gray-500">
                  <Info className="mt-0.5 h-3 w-3 shrink-0 text-gray-400" />
                  Enter your UPI ID from Paytm, PhonePe, GPay, etc.
                </p>
              </div>

              <label className="flex items-center gap-2 rounded-lg border border-gray-200 bg-gray-50 px-3 py-2.5 text-sm text-gray-700">
                <input
                  type="checkbox"
                  checked={formData.defaultLink}
                  onChange={(e) =>
                    setFormData({ ...formData, defaultLink: e.target.checked })
                  }
                  className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                />
                Make this the default payment link
              </label>

              {error && (
                <div className="rounded-lg border border-red-100 bg-red-50 p-3 text-xs font-medium text-red-700">
                  {error}
                </div>
              )}

              <div className="rounded-lg border border-blue-100 bg-blue-50 p-3">
                <p className="text-xs leading-relaxed text-blue-700">
                  <strong className="font-semibold">Note:</strong> After
                  adding, test with a Rs 1 transaction to verify the UPI link.
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3 border-t border-gray-100 px-5 py-4 sm:px-6">
              <button
                type="button"
                onClick={onClose}
                disabled={submitting}
                className="
                  flex-1 rounded-lg border border-gray-200 px-4 py-2.5
                  text-sm font-medium text-gray-700
                  hover:bg-gray-50 transition-colors
                  disabled:opacity-50
                "
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting}
                className="
                  flex-1 inline-flex items-center justify-center gap-2
                  rounded-lg bg-blue-50 px-4 py-2.5
                  text-sm font-medium text-blue-700
                  border border-blue-200
                  hover:bg-blue-100 transition-colors
                  disabled:opacity-50
                "
              >
                {submitting && <Loader2 className="h-4 w-4 animate-spin" />}
                Add UPI Link
              </button>
            </div>
          </form>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
};

export default UPIFormModal;
