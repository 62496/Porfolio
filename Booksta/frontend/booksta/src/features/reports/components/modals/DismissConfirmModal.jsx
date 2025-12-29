import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";

export default function DismissConfirmModal({
    isOpen,
    onConfirm,
    onCancel,
}) {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onCancel}
            title="Confirm Dismissal"
        >
            <div className="mb-6">
                <p className="text-[17px] text-[#1d1d1f] leading-relaxed">
                    Are you sure you want to dismiss this report? This action will mark the report as dismissed without taking any action.
                </p>
            </div>
            <div className="flex gap-3">
                <Button
                    onClick={onConfirm}
                    type="modal-success"
                    label="Yes, Dismiss Report"
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
