import React from "react";
import { GoogleLogin } from "@react-oauth/google";
import { useNavigate } from "react-router-dom";

import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import AuthService from "../../api/services/authService";
import useToast from "../../hooks/useToast";
import Toast from "../../components/common/Toast";

export default function Login() {
  const navigate = useNavigate();
  const { toast, showToast, hideToast } = useToast();

  const handleSuccess = async (credentialResponse) => {
    try {
      await AuthService.loginWithGoogle(credentialResponse.credential);
      navigate("/profile");
    } catch (error) {
      showToast(`Error during connection: ${error.response?.data?.message || error.message}`, "error");
    }
  };

  const handleError = () => {
    showToast("Google connection failure", "error");
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#f5f5f7] text-[#1d1d1f]">
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={hideToast}
          duration={toast.duration}
        />
      )}
      <Header />

      
      <main className="flex-1 pt-[96px] px-4 pb-12 flex items-center justify-center">
        <div className="w-full max-w-md">
          <div className="bg-white rounded-2xl border border-[#e5e5e7] shadow-sm px-8 py-10 text-center">
            <h1 className="text-3xl font-semibold mb-3">Sign in to Booksta</h1>
            <p className="text-[15px] text-[#6e6e73] mb-8">
            Please log in with your Google account to continue.
            </p>

            <div className="flex justify-center mb-6">
              <GoogleLogin
                onSuccess={handleSuccess}
                onError={handleError}
                useOneTap
                theme="filled_blue"
                size="large"
                shape="rectangular"
              />
            </div>

            <p className="text-[13px] text-[#86868b]">
            By logging in, you agree to Booksta's terms of use.
            </p>
          </div>

          
        </div>
      </main>

      
      <Footer />
    </div>
  );
}
