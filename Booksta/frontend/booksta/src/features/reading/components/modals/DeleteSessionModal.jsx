import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";

export default function DeleteSessionModal({
    isOpen,
    onConfirm,
    onCancel,
}) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onCancel}
            title="Delete Reading Session"
        >
            <div className="space-y-4">
                <p className="text-[15px] text-[#6e6e73]">
                    Are you sure you want to delete this reading session? This action cannot be undone.
                </p>

                <div className="flex gap-3 pt-2">
                    <Button
                        type="modal-danger"
                        label="Delete Session"
                        onClick={onConfirm}
                        className="flex-1"
                    />
                    <Button
                        type="modal-secondary"
                        label="Cancel"
                        onClick={onCancel}
                        className="flex-1"
                    />
                </div>
            </div>
        </Modal>
    );
}
