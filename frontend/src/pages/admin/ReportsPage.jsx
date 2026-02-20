import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import Toast from "../../components/common/Toast";
import PageHeader from "../../components/layout/PageHeader";
import { useReportsPage } from "../../features/reports/hooks/useReportsPage";
import ReportFilters from "../../features/reports/components/ReportFilters";
import ReportCard from "../../features/reports/components/ReportCard";
import TakeActionModal from "../../features/reports/components/modals/TakeActionModal";
import ResolveConfirmModal from "../../features/reports/components/modals/ResolveConfirmModal";
import DismissConfirmModal from "../../features/reports/components/modals/DismissConfirmModal";
import { getReportType } from "../../features/reports/utils/reportHelpers";

export default function ReportsPage() {
  const {
    isVisible,
    filter,
    statusFilter,
    searchTerm,
    selectedReport,
    showActionModal,
    actionType,
    currentStep,
    warningMessage,
    editedBookData,
    editedAuthorData,
    authorAvatarPreview,
    bookImagePreview,
    showResolveConfirmModal,
    showDismissConfirmModal,
    validationErrors,

    reports,
    filteredReports,
    authors,
    subjects,
    toast,

    setFilter,
    setStatusFilter,
    setSearchTerm,
    setActionType,
    setWarningMessage,
    setEditedBookData,
    setEditedAuthorData,
    setAuthorAvatar,
    setAuthorAvatarPreview,
    setBookImage,
    setBookImagePreview,

    handleTakeAction,
    handleDismiss,
    confirmDismiss,
    cancelDismiss,
    handleMarkResolved,
    confirmMarkResolved,
    cancelMarkResolved,
    handleNextStep,
    handleBackStep,
    submitAction,
    resetActionModalState,
    hideToast,
  } = useReportsPage();

  return (
    <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={hideToast}
          duration={toast.duration}
        />
      )}
      <Header />

      <div className="flex-1">
        <section className="py-20 bg-white">
          <div className="max-w-[1200px] mx-auto px-5">
            <PageHeader
              title="Reports Management"
              description="Review and manage community reports for books and authors"
            />

            {/* Report Stats */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
              <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Total Reports</p>
                <p className="text-[32px] font-semibold text-[#1d1d1f]">{reports.length}</p>
              </div>
              <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Book Reports</p>
                <p className="text-[32px] font-semibold text-[#0071e3]">{reports.filter(r => getReportType(r) === "BOOK").length}</p>
              </div>
              <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Author Reports</p>
                <p className="text-[32px] font-semibold text-[#0071e3]">{reports.filter(r => getReportType(r) === "AUTHOR").length}</p>
              </div>
            </div>

            <div className="mb-10">
              <ReportFilters
                reports={reports}
                filter={filter}
                statusFilter={statusFilter}
                searchTerm={searchTerm}
                onFilterChange={setFilter}
                onStatusFilterChange={setStatusFilter}
                onSearchChange={setSearchTerm}
              />
            </div>

            {/* Reports List */}
            <div className="space-y-6">
              {filteredReports.length > 0 ? (
                filteredReports.map((report, index) => (
                  <ReportCard
                    key={report.id}
                    report={report}
                    index={index}
                    isVisible={isVisible}
                    onTakeAction={handleTakeAction}
                    onDismiss={handleDismiss}
                    onMarkResolved={handleMarkResolved}
                  />
                ))
              ) : (
                <div className="text-center py-20">
                  <p className="text-[21px] text-[#6e6e73]">No reports found.</p>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* Take Action Modal */}
        <TakeActionModal
          isOpen={showActionModal}
          selectedReport={selectedReport}
          actionType={actionType}
          currentStep={currentStep}
          warningMessage={warningMessage}
          editedBookData={editedBookData}
          editedAuthorData={editedAuthorData}
          bookImagePreview={bookImagePreview}
          authorAvatarPreview={authorAvatarPreview}
          validationErrors={validationErrors}
          authors={authors}
          subjects={subjects}
          onClose={resetActionModalState}
          onActionTypeChange={setActionType}
          onWarningMessageChange={setWarningMessage}
          onEditedBookDataChange={setEditedBookData}
          onEditedAuthorDataChange={setEditedAuthorData}
          onBookImageChange={setBookImage}
          onBookImagePreviewChange={setBookImagePreview}
          onAuthorAvatarChange={setAuthorAvatar}
          onAuthorAvatarPreviewChange={setAuthorAvatarPreview}
          onNextStep={handleNextStep}
          onBackStep={handleBackStep}
          onSubmit={submitAction}
        />

        {/* Resolve Confirmation Modal */}
        <ResolveConfirmModal
          isOpen={showResolveConfirmModal}
          onConfirm={confirmMarkResolved}
          onCancel={cancelMarkResolved}
        />

        {/* Dismiss Confirmation Modal */}
        <DismissConfirmModal
          isOpen={showDismissConfirmModal}
          onConfirm={confirmDismiss}
          onCancel={cancelDismiss}
        />
      </div>

      <Footer />
    </div>
  );
}
