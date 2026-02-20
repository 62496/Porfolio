import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

/**
 * PrivateRoute - Protects routes based on authentication and roles
 *
 * Usage:
 *   <PrivateRoute>                           // Just requires login
 *   <PrivateRoute roles={["AUTHOR"]}>        // Requires AUTHOR role
 *   <PrivateRoute roles={["AUTHOR", "LIBRARIAN"]}> // Requires any of these roles
 */
export default function PrivateRoute({ children, roles = [] }) {
    const { user, loading, isAuthenticated, hasAnyRole } = useAuth();
    const location = useLocation();

    if (loading) {
        return (
            <div className="min-h-screen bg-[#f5f5f7] flex items-center justify-center">
                <div className="text-center">
                    <div className="w-12 h-12 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                    <p className="text-[15px] text-[#6e6e73]">Loading...</p>
                </div>
            </div>
        );
    }

    if (!isAuthenticated()) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    if (roles.length > 0 && !hasAnyRole(roles)) {
        return (
            <div className="min-h-screen bg-[#f5f5f7] flex items-center justify-center">
                <div className="text-center max-w-md mx-auto px-6">
                    <div className="w-16 h-16 mx-auto mb-6 rounded-full bg-red-100 flex items-center justify-center">
                        <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                        </svg>
                    </div>
                    <h2 className="text-[28px] font-semibold text-[#1d1d1f] mb-2">Access Denied</h2>
                    <p className="text-[17px] text-[#6e6e73] mb-6">
                        You don't have permission to access this page.
                        {roles.length > 0 && (
                            <span className="block mt-2 text-[15px]">
                                Required role: {roles.join(" or ")}
                            </span>
                        )}
                    </p>
                    <button
                        onClick={() => window.history.back()}
                        className="px-6 py-3 bg-[#1d1d1f] text-white rounded-xl text-[15px] font-medium hover:bg-[#424245] transition-colors"
                    >
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    return children;
}
