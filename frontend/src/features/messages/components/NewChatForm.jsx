import Button from "../../../components/common/Button";
import GenericInput from "../../../components/forms/GenericInput";

export default function NewChatForm({
    searchTerm,
    searchResults,
    selectedRecipient,
    newDraft,
    loading,
    onSearchChange,
    onSelectRecipient,
    onDraftChange,
    onSubmit,
}) {
    return (
        <>
            <div className="p-6 border-b border-[#e5e5e7]">
                <h3 className="text-[20px] font-semibold mb-1">New Conversation</h3>
                <p className="text-[13px] text-[#6e6e73]">Start a new chat with a reader</p>
            </div>

            <form className="flex-1 p-6 flex flex-col gap-4" onSubmit={onSubmit}>
                <div className="relative">
                    <GenericInput
                        type="text"
                        label="Recipient"
                        placeholder="Search by name or email"
                        value={searchTerm}
                        onChange={onSearchChange}
                    />
                    {searchResults.length > 0 && (
                        <div className="absolute top-full left-0 right-0 mt-2 bg-white border border-[#e5e5e7] rounded-lg shadow-lg max-h-[200px] overflow-y-auto z-10">
                            {searchResults.map((user) => (
                                <button
                                    type="button"
                                    key={user.id}
                                    className="w-full p-3 text-left hover:bg-[#f5f5f7] transition-colors flex items-center gap-3"
                                    onClick={() => onSelectRecipient(user)}
                                >
                                    <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-semibold text-[12px]">
                                        {user.firstName?.[0]}
                                        {user.lastName?.[0]}
                                    </div>
                                    <div>
                                        <div className="text-[15px] font-medium">
                                            {user.firstName} {user.lastName}
                                        </div>
                                        <div className="text-[13px] text-[#6e6e73]">{user.email}</div>
                                    </div>
                                </button>
                            ))}
                        </div>
                    )}
                    {selectedRecipient && (
                        <p className="text-[13px] text-green-700 mt-1">
                            Selected: {selectedRecipient.firstName} {selectedRecipient.lastName}
                        </p>
                    )}
                </div>

                <GenericInput
                    type="textarea"
                    label="Message"
                    placeholder="Type your message..."
                    value={newDraft}
                    onChange={(e) => onDraftChange(e.target.value)}
                    rows={4}
                />

                <Button
                    type="primary"
                    label="Send Message"
                    onClick={onSubmit}
                    disabled={loading || !newDraft.trim() || !selectedRecipient}
                />
            </form>
        </>
    );
}
