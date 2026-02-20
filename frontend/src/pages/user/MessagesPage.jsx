import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import PageHeader from "../../components/layout/PageHeader";
import Button from "../../components/common/Button";
import { useMessages } from "../../features/messages/hooks/useMessages";
import ConversationList from "../../features/messages/components/ConversationList";
import MessageThread from "../../features/messages/components/MessageThread";
import NewChatForm from "../../features/messages/components/NewChatForm";

export default function MessagesPage() {
    const {
        currentUser,
        conversations,
        selectedConversation,
        messages,
        conversationDraft,
        newDraft,
        searchTerm,
        searchResults,
        selectedRecipient,
        loading,
        error,
        showNewChat,
        setConversationDraft,
        setNewDraft,
        openConversation,
        handleSend,
        handleStartNewChat,
        handleSearchChange,
        chooseRecipient,
        toggleNewChat,
    } = useMessages();

    if (!currentUser) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
                <Header />
                <div className="flex-1 flex items-center justify-center">
                    <div className="text-center">
                        <h1 className="text-[48px] font-bold mb-4">Sign In Required</h1>
                        <p className="text-[17px] text-[#6e6e73] mb-6">You need to be logged in to access messages</p>
                        <Button label="Sign In" type="primary" href="/login" />
                    </div>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            <Header />

            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="Messages"
                        description="Connect with fellow readers and share your thoughts"
                    />

                    <div className="grid grid-cols-[320px_1fr] gap-6">
                        <ConversationList
                            conversations={conversations}
                            selectedConversation={selectedConversation}
                            loading={loading}
                            error={error}
                            showNewChat={showNewChat}
                            onToggleNewChat={toggleNewChat}
                            onSelectConversation={openConversation}
                        />

                        <div className="bg-white border border-[#e5e5e7] rounded-[18px] overflow-hidden h-[calc(100vh-300px)] flex flex-col">
                            {showNewChat ? (
                                <NewChatForm
                                    searchTerm={searchTerm}
                                    searchResults={searchResults}
                                    selectedRecipient={selectedRecipient}
                                    newDraft={newDraft}
                                    loading={loading}
                                    onSearchChange={handleSearchChange}
                                    onSelectRecipient={chooseRecipient}
                                    onDraftChange={setNewDraft}
                                    onSubmit={handleStartNewChat}
                                />
                            ) : selectedConversation ? (
                                <MessageThread
                                    selectedConversation={selectedConversation}
                                    messages={messages}
                                    currentUser={currentUser}
                                    conversationDraft={conversationDraft}
                                    loading={loading}
                                    onDraftChange={setConversationDraft}
                                    onSend={handleSend}
                                />
                            ) : (
                                <div className="flex-1 flex items-center justify-center">
                                    <div className="text-center">
                                        <h2 className="text-[28px] font-semibold mb-2">Start a Conversation</h2>
                                        <p className="text-[17px] text-[#6e6e73]">Select a conversation or click "New" to start chatting</p>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </main>
            </div>

            <Footer />
        </div>
    );
}
