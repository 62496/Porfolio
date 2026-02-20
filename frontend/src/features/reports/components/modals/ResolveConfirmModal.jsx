import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";

export default function ResolveConfirmModal({
    isOpen,
    onConfirm,
    onCancel,
}) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onCancel}
            title="Confirm Resolution"
        >
            <div className="mb-6">
                <p className="text-[17px] text-[#1d1d1f] leading-relaxed">
                    Are you sure you want to mark this report as resolved? This will close the report without taking any action.
                </p>
            </div>
            <div className="flex gap-3">
                <Button
                    onClick={onConfirm}
                    type="modal-success"
                    label="Yes, Mark as Resolved"
                    className="flex-1"
                />
                <Button
                    onClick={onCancel}
                    type="modal-danger"
                    label="No, Cancel"
                    className="flex-1"
                />
            </div>
        </Modal>
    );
}
