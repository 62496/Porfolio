import { useState, useMemo, useEffect, useCallback } from "react";
import authService from "../../../api/services/authService";
import messageService from "../../../api/services/messageService";
import userService from "../../../api/services/userService";

export function useMessages() {
    const [conversations, setConversations] = useState([]);
    const [selectedConversation, setSelectedConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [conversationDraft, setConversationDraft] = useState("");
    const [newDraft, setNewDraft] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [selectedRecipient, setSelectedRecipient] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [showNewChat, setShowNewChat] = useState(false);

    const currentUser = useMemo(() => authService.getCurrentUser(), []);

    // Load conversations on mount
    useEffect(() => {
        if (!currentUser) return;
        loadConversations();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentUser?.id]);

    const loadConversations = useCallback(async () => {
        try {
            setLoading(true);
            setError("");
            const data = await messageService.listConversations();
            setConversations(data || []);
        } catch (e) {
            setError("Failed to load conversations");
        } finally {
            setLoading(false);
        }
    }, []);

    const openConversation = useCallback(async (conversation) => {
        try {
            setShowNewChat(false);
            setSelectedConversation(conversation);
            const msgs = await messageService.getConversationMessages(
                conversation.conversationId,
            );
            setMessages(msgs || []);

            // Mark as read and update unread count
            await messageService.markConversationAsRead(
                conversation.conversationId,
                currentUser.id
            );

            // Update only the unread count for this conversation
            setConversations(prev =>
                prev.map(c =>
                    c.conversationId === conversation.conversationId
                        ? { ...c, unreadCount: 0 }
                        : c
                )
            );
        } catch (e) {
            setError("Failed to open conversation");
        }
    }, [currentUser?.id]);

    const handleSend = useCallback(async (e) => {
        e.preventDefault();
        if (!selectedConversation || !conversationDraft.trim()) return;

        try {
            setLoading(true);
            setError("");
            const sent = await messageService.sendMessage(
                selectedConversation.otherUser.id,
                conversationDraft.trim()
            );
            setConversationDraft("");

            // Refresh conversations + thread
            await loadConversations();
            setMessages((prev) => [...prev, sent]);
        } catch (e) {
            setError(e?.response?.data || "Failed to send message");
        } finally {
            setLoading(false);
        }
    }, [selectedConversation, conversationDraft, loadConversations]);

    const handleStartNewChat = useCallback(async (e) => {
        e.preventDefault();
        let recipient = selectedRecipient;

        if (!recipient && searchResults.length === 1) {
            recipient = searchResults[0];
            setSelectedRecipient(recipient);
        }

        if (!newDraft.trim() || !recipient) {
            setError("Please select a recipient and enter a message");
            return;
        }

        try {
            setLoading(true);
            setError("");
            const sent = await messageService.sendMessage(
                recipient.id,
                newDraft.trim()
            );
            setNewDraft("");
            setSelectedRecipient(null);
            setSearchTerm("");
            setSearchResults([]);
            await loadConversations();
            const conv =
                conversations.find((c) => c.conversationId === sent.conversationId) ||
                {
                    conversationId: sent.conversationId,
                    otherUser: sent.recipient,
                };
            await openConversation(conv);
        } catch (e) {
            setError(e?.response?.data || "Failed to start conversation");
        } finally {
            setLoading(false);
        }
    }, [selectedRecipient, searchResults, newDraft, conversations, loadConversations, openConversation]);

    const handleSearchChange = useCallback(async (e) => {
        const value = e.target.value;
        setSearchTerm(value);
        setSelectedRecipient(null);
        if (!value.trim()) {
            setSearchResults([]);
            return;
        }
        try {
            const results = await userService.searchGoogleUsers(
                value.trim(),
                currentUser?.id
            );
            setSearchResults(results || []);
        } catch (err) {
            setSearchResults([]);
        }
    }, [currentUser?.id]);

    const chooseRecipient = useCallback((user) => {
        setSelectedRecipient(user);
        setSearchTerm(`${user.firstName} ${user.lastName}`.trim() || user.email);
        setSearchResults([]);
    }, []);

    const toggleNewChat = useCallback(() => {
        setShowNewChat(prev => !prev);
    }, []);

    return {
        // State
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

        // Setters
        setConversationDraft,
        setNewDraft,

        // Actions
        openConversation,
        handleSend,
        handleStartNewChat,
        handleSearchChange,
        chooseRecipient,
        toggleNewChat,
    };
}
