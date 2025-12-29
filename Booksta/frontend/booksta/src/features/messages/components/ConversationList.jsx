import Button from "../../../components/common/Button";
import { formatDate } from "../utils/formatters";

export default function ConversationList({
    conversations,
    selectedConversation,
    loading,
    error,
    showNewChat,
    onToggleNewChat,
    onSelectConversation,
}) {
    return (
        <aside className="bg-white border border-[#e5e5e7] rounded-[18px] p-6 h-[calc(100vh-300px)] flex flex-col">
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-[20px] font-semibold">Conversations</h2>
                <Button
                    type="small-secondary"
                    label={showNewChat ? "Cancel" : "+ New"}
                    onClick={onToggleNewChat}
                />
            </div>

            {error && (
                <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-[13px] text-red-700">
                    {error}
                </div>
            )}

            <div className="flex-1 overflow-y-auto space-y-2">
                {loading && conversations.length === 0 && (
                    <p className="text-[15px] text-[#6e6e73]">Loading...</p>
                )}

                {conversations.length === 0 && !loading && (
                    <div className="text-center py-8">
                        <p className="text-[15px] text-[#6e6e73] mb-2">No messages yet</p>
                        <p className="text-[13px] text-[#86868b]">Start a new conversation</p>
                    </div>
                )}

                {conversations.map((conversation) => (
                    <button
                        key={conversation.conversationId}
                        className={`w-full p-3 rounded-lg text-left transition-colors border ${selectedConversation?.conversationId === conversation.conversationId
                            ? "bg-[#f5f5f7] border-[#1d1d1f]"
                            : "bg-white border-[#e5e5e7] hover:bg-[#fafaf9]"
                            }`}
                        onClick={() => onSelectConversation(conversation)}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            {conversation.otherUser.picture ? (
                                <img
                                    src={conversation.otherUser.picture}
                                    alt={`${conversation.otherUser.firstName} ${conversation.otherUser.lastName}`}
                                    className="w-10 h-10 rounded-full object-cover"
                                />
                            ) : (
                                <div className="w-10 h-10 rounded-full bg-[#f5f5f7] flex items-center justify-center text-[#6e6e73] font-semibold text-[14px]">
                                    {conversation.otherUser.firstName?.[0]}
                                    {conversation.otherUser.lastName?.[0]}
                                </div>
                            )}
                            <div className="flex-1 min-w-0">
                                <div className="flex items-center justify-between">
                                    <span className="text-[15px] font-semibold truncate">
                                        {conversation.otherUser.firstName} {conversation.otherUser.lastName}
                                    </span>
                                    {conversation.unreadCount > 0 && (
                                        <span className="px-2 py-0.5 bg-blue-600 text-white text-[11px] font-semibold rounded-full">
                                            {conversation.unreadCount}
                                        </span>
                                    )}
                                </div>
                                <p className="text-[13px] text-[#6e6e73] truncate">
                                    {conversation.lastMessagePreview || "No messages"}
                                </p>
                            </div>
                        </div>
                        <p className="text-[11px] text-[#86868b]">
                            {conversation.lastMessageAt ? formatDate(conversation.lastMessageAt) : ""}
                        </p>
                    </button>
                ))}
            </div>
        </aside>
    );
}
