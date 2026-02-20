import React, { useState } from 'react';
import Modal from '../../../components/common/Modal';
import Button from '../../../components/common/Button';
import messageService from '../../../api/services/messageService';
import useToast from '../../../hooks/useToast';
import Toast from '../../../components/common/Toast';

export default function ContactSellerModal({
    isOpen,
    onClose,
    seller,
    book,
}) {
    const [message, setMessage] = useState('');
    const [sending, setSending] = useState(false);
    const { toast, showToast, hideToast } = useToast();

    const handleSend = async () => {
        if (!message.trim()) return;

        try {
            setSending(true);
            await messageService.sendMessage(seller.sellerId, message.trim());
            showToast('Message sent successfully!', 'success');
            setMessage('');
            setTimeout(() => {
                onClose();
            }, 1500);
        } catch {
            showToast('Failed to send message. Please try again.', 'error');
        } finally {
            setSending(false);
        }
    };

    const handleClose = () => {
        if (!sending) {
            setMessage('');
            onClose();
        }
    };

    // Pre-fill message template
    const handleUseTemplate = () => {
        const template = `Hi ${seller?.sellerName},\n\nI'm interested in purchasing "${book?.title}" that you have listed for $${seller?.pricePerUnit?.toFixed(2)}.\n\nIs it still available?`;
        setMessage(template);
    };

    if (!isOpen || !seller) return null;

    return (
        <>
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={hideToast}
                    duration={toast.duration}
                />
            )}
            <Modal isOpen={isOpen} onClose={handleClose} title="Contact Seller">
                <div className="space-y-4">
                    {/* Seller Info */}
                    <div className="flex items-center gap-3 p-3 bg-[#f5f5f7] rounded-[10px]">
                        {seller.sellerPicture ? (
                            <img
                                src={seller.sellerPicture}
                                alt={seller.sellerName}
                                className="w-12 h-12 rounded-full object-cover border-2 border-white shadow-sm"
                            />
                        ) : (
                            <div className="w-12 h-12 rounded-full bg-[#e5e5e7] flex items-center justify-center">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-6 h-6 text-[#6e6e73]">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                                </svg>
                            </div>
                        )}
                        <div className="flex-1">
                            <p className="font-medium text-[15px] text-[#1d1d1f]">
                                {seller.sellerName}
                            </p>
                            <p className="text-[13px] text-[#6e6e73]">
                                Selling "{book?.title}" for ${seller.pricePerUnit?.toFixed(2)}
                            </p>
                        </div>
                    </div>

                    {/* Message Input */}
                    <div>
                        <div className="flex items-center justify-between mb-2">
                            <label className="text-[14px] font-medium text-[#1d1d1f]">
                                Your Message
                            </label>
                            <button
                                type="button"
                                onClick={handleUseTemplate}
                                className="text-[13px] text-[#0066cc] hover:underline"
                            >
                                Use template
                            </button>
                        </div>
                        <textarea
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            placeholder="Write your message to the seller..."
                            rows={5}
                            className="w-full px-4 py-3 rounded-[10px] border border-[#e5e5e7] text-[15px] text-[#1d1d1f] placeholder-[#86868b] focus:outline-none focus:border-[#1d1d1f] focus:ring-1 focus:ring-[#1d1d1f] resize-none transition-colors"
                            disabled={sending}
                        />
                        <p className="text-[12px] text-[#86868b] mt-1">
                            {message.length} characters
                        </p>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-3 justify-end pt-2">
                        <Button
                            type="secondary"
                            onClick={handleClose}
                            disabled={sending}
                        >
                            Cancel
                        </Button>
                        <Button
                            type="primary"
                            onClick={handleSend}
                            disabled={!message.trim() || sending}
                        >
                            {sending ? (
                                <>
                                    <svg className="animate-spin w-4 h-4 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                    </svg>
                                    Sending...
                                </>
                            ) : (
                                <>
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4 mr-1">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M6 12L3.269 3.126A59.768 59.768 0 0121.485 12 59.77 59.77 0 013.27 20.876L5.999 12zm0 0h7.5" />
                                    </svg>
                                    Send Message
                                </>
                            )}
                        </Button>
                    </div>
                </div>
            </Modal>
        </>
    );
}
