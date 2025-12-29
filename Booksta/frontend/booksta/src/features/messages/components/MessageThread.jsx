import { formatDate } from "../utils/formatters";

export default function MessageThread({
    selectedConversation,
    messages,
    currentUser,
    conversationDraft,
    loading,
    onDraftChange,
    onSend,
}) {
    return (
        <>
            <div className="p-6 border-b border-[#e5e5e7]">
                <div className="flex items-center gap-3">
                    {selectedConversation.otherUser.picture ? (
                        <img
                            src={selectedConversation.otherUser.picture}
                            alt={`${selectedConversation.otherUser.firstName} ${selectedConversation.otherUser.lastName}`}
                            className="w-12 h-12 rounded-full object-cover"
                        />
                    ) : (
                        <div className="w-12 h-12 rounded-full bg-[#f5f5f7] flex items-center justify-center text-[#6e6e73] font-semibold text-[16px]">
                            {selectedConversation.otherUser.firstName?.[0]}
                            {selectedConversation.otherUser.lastName?.[0]}
                        </div>
                    )}
                    <div>
                        <h3 className="text-[20px] font-semibold">
                            {selectedConversation.otherUser.firstName} {selectedConversation.otherUser.lastName}
                        </h3>
                        <p className="text-[13px] text-[#6e6e73]">
                            {selectedConversation.otherUser.email}
                        </p>
                    </div>
                </div>
            </div>

            <div className="flex-1 overflow-y-auto p-6 space-y-4">
                {messages.map((message) => {
                    const isMine = message.sender.id === currentUser.id;
                    return (
                        <div
                            key={message.id}
                            className={`flex ${isMine ? "justify-end" : "justify-start"}`}
                        >
                            <div className={`max-w-[70%] ${isMine ? "bg-[#3a3a3c] text-white" : "bg-[#f5f5f7] text-[#1d1d1f]"} rounded-[16px] p-4`}>
                                <div className="flex items-center gap-2 mb-1">
                                    <span className={`text-[12px] font-semibold ${isMine ? "text-[#d1d1d6]" : "text-[#6e6e73]"}`}>
                                        {isMine ? "You" : message.sender.firstName}
                                    </span>
                                    <span className={`text-[11px] ${isMine ? "text-[#aeaeb2]" : "text-[#86868b]"}`}>
                                        {formatDate(message.sentAt)}
                                    </span>
                                </div>
                                <p className="text-[15px] leading-relaxed">{message.content}</p>
                            </div>
                        </div>
                    );
                })}
                {messages.length === 0 && (
                    <p className="text-center text-[15px] text-[#6e6e73] py-8">No messages yet</p>
                )}
            </div>

            <form className="p-6 border-t border-[#e5e5e7]" onSubmit={onSend}>
                <div className="flex gap-3">
                    <input
                        type="text"
                        placeholder="Type your message..."
                        value={conversationDraft}
                        onChange={(e) => onDraftChange(e.target.value)}
                        className="flex-1 px-4 py-3 border border-[#e5e5e7] rounded-lg text-[15px] focus:outline-none focus:border-[#1d1d1f] transition-colors h-[46px]"
                    />
                    <button
                        type="submit"
                        disabled={loading || !conversationDraft.trim()}
                        className="px-6 py-3 bg-[#1d1d1f] text-white rounded-lg text-[15px] font-medium hover:bg-[#424245] disabled:bg-[#e5e5e7] disabled:text-[#86868b] disabled:cursor-not-allowed transition-colors h-[46px]"
                    >
                        Send
                    </button>
                </div>
            </form>
        </>
    );
}
