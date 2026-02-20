import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import AuthService from "../../api/services/authService";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import "../../styles/Profile.css";

function Profile() {
    const [user, setUser] = useState(AuthService.getCurrentUser());
    const [loading, setLoading] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const loadUser = async () => {
            const localUser = AuthService.getCurrentUser();
            if (localUser) {
                setUser(localUser);
                setLoading(false);
                return;
            }

            setLoading(true);
            const currentUser = await AuthService.fetchCurrentUserFromApi();
            setUser(currentUser);
            setLoading(false);
        };
        loadUser();
    }, [location.key]);

    if (loading) {
        return (
            <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
                <Header />
                <main className="flex-1 flex items-center justify-center">
                    <p className="text-[#6e6e73]">Loading your profile...</p>
                </main>
                <Footer />
            </div>
        );
    }

    if (!user) {
        return (
            <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
                <Header />
                <main className="flex-1 flex items-center justify-center">
                    <div className="text-center">
                        <h2 className="text-xl font-semibold mb-2">You're not logged in</h2>
                        <p className="text-[#6e6e73] mb-4">Please log in to see your profile.</p>
                        <Link to="/login" className="text-[#0066cc] hover:underline">
                            Go to the login page
                        </Link>
                    </div>
                </main>
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            <Header />

            <main className="flex-1 max-w-[800px] mx-auto py-20 px-5 w-full">
                {/* Profile Card */}
                <div className="bg-white border border-[#e5e5e7] rounded-2xl p-8 shadow-sm">
                    <div className="flex items-center gap-6">
                        {user.picture ? (
                            <img
                                src={user.picture}
                                alt={`${user.firstName} ${user.lastName}`}
                                className="w-24 h-24 rounded-full object-cover border-2 border-[#e5e5e7]"
                            />
                        ) : (
                            <div className="w-24 h-24 rounded-full bg-[#0066cc] flex items-center justify-center text-white text-3xl font-semibold">
                                {user.firstName?.[0]}{user.lastName?.[0]}
                            </div>
                        )}
                        <div>
                            <h1 className="text-2xl font-semibold text-[#1d1d1f]">
                                {user.firstName} {user.lastName}
                            </h1>
                            <p className="text-[#6e6e73]">{user.email}</p>
                            {user.roles && user.roles.length > 0 && (
                                <div className="flex gap-2 mt-2">
                                    {user.roles.map((role) => (
                                        <span
                                            key={typeof role === 'object' ? role.id : role}
                                            className="px-2 py-1 text-xs font-medium bg-[#f5f5f7] text-[#6e6e73] rounded-full"
                                        >
                                            {typeof role === 'object' ? role.name : role}
                                        </span>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* Quick Links */}
                <div className="mt-8 grid gap-4 sm:grid-cols-2">
                    <Link
                        to="/collections"
                        className="group flex items-center gap-4 p-5 bg-white border border-[#e5e5e7] rounded-2xl hover:border-[#0066cc] hover:shadow-md transition-all"
                    >
                        <div className="w-12 h-12 rounded-xl bg-[#f5f5f7] group-hover:bg-[#e8f0fe] flex items-center justify-center transition-colors">
                            <svg className="w-6 h-6 text-[#6e6e73] group-hover:text-[#0066cc] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                            </svg>
                        </div>
                        <div>
                            <p className="font-semibold text-[#1d1d1f]">My Collections</p>
                            <p className="text-sm text-[#6e6e73]">Manage your book collections</p>
                        </div>
                    </Link>

                    <Link
                        to="/my-books"
                        className="group flex items-center gap-4 p-5 bg-white border border-[#e5e5e7] rounded-2xl hover:border-[#0066cc] hover:shadow-md transition-all"
                    >
                        <div className="w-12 h-12 rounded-xl bg-[#f5f5f7] group-hover:bg-[#e8f0fe] flex items-center justify-center transition-colors">
                            <svg className="w-6 h-6 text-[#6e6e73] group-hover:text-[#0066cc] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                            </svg>
                        </div>
                        <div>
                            <p className="font-semibold text-[#1d1d1f]">My Books</p>
                            <p className="text-sm text-[#6e6e73]">View your owned books</p>
                        </div>
                    </Link>

                    <Link
                        to="/favorites"
                        className="group flex items-center gap-4 p-5 bg-white border border-[#e5e5e7] rounded-2xl hover:border-[#ff3b30] hover:shadow-md transition-all"
                    >
                        <div className="w-12 h-12 rounded-xl bg-[#f5f5f7] group-hover:bg-[#fee8e7] flex items-center justify-center transition-colors">
                            <svg className="w-6 h-6 text-[#6e6e73] group-hover:text-[#ff3b30] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z" />
                            </svg>
                        </div>
                        <div>
                            <p className="font-semibold text-[#1d1d1f]">Favorites</p>
                            <p className="text-sm text-[#6e6e73]">Your favorite books</p>
                        </div>
                    </Link>

                    <Link
                        to="/progress"
                        className="group flex items-center gap-4 p-5 bg-white border border-[#e5e5e7] rounded-2xl hover:border-[#34c759] hover:shadow-md transition-all"
                    >
                        <div className="w-12 h-12 rounded-xl bg-[#f5f5f7] group-hover:bg-[#e8f8ec] flex items-center justify-center transition-colors">
                            <svg className="w-6 h-6 text-[#6e6e73] group-hover:text-[#34c759] transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                            </svg>
                        </div>
                        <div>
                            <p className="font-semibold text-[#1d1d1f]">Reading Progress</p>
                            <p className="text-sm text-[#6e6e73]">Track your reading</p>
                        </div>
                    </Link>
                </div>
            </main>

            <Footer />
        </div>
    );
}

export default Profile;
