import React from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import PageHeader from '../../components/layout/PageHeader';
import { useFollowingsPage } from '../../features/social/hooks/useFollowingsPage';
import FollowedAuthorCard from '../../features/social/components/FollowedAuthorCard';
import FollowedSeriesCard from '../../features/social/components/FollowedSeriesCard';

export default function FollowingsPage() {
    const {
        currentUser,
        authors,
        series,
        loading,
        handleUnfollow,
    } = useFollowingsPage();

    if (!currentUser?.id) return null;

    return (
        <div className="font-sans bg-white text-[#1d1d1f] min-h-screen flex flex-col">
            <Header />
            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="Your Followings"
                        description="Manage your favorite authors and book series"
                    />

                    {loading ? (
                        <div className="text-center py-12">
                            <div className="w-12 h-12 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4" />
                            <p className="text-[17px] text-[#6e6e73]">Loading followings…</p>
                        </div>
                    ) : (
                        <>
                            <section className="mb-16">
                                <h2 className="text-[28px] font-semibold text-[#1d1d1f] mb-6">Authors</h2>
                                {authors.length === 0 ? (
                                    <div className="text-center py-12 bg-[#f5f5f7] rounded-[18px]">
                                        <p className="text-[17px] text-[#6e6e73]">You don't follow any authors yet.</p>
                                    </div>
                                ) : (
                                    <div className="grid gap-6 grid-cols-1 lg:grid-cols-2">
                                        {authors.map(a => (
                                            <FollowedAuthorCard key={a.id} author={a} onUnfollow={handleUnfollow} />
                                        ))}
                                    </div>
                                )}
                            </section>

                            <section className="mb-12">
                                <h2 className="text-[28px] font-semibold text-[#1d1d1f] mb-6">Series</h2>
                                {series.length === 0 ? (
                                    <div className="text-center py-12 bg-[#f5f5f7] rounded-[18px]">
                                        <p className="text-[17px] text-[#6e6e73]">You don't follow any series yet.</p>
                                    </div>
                                ) : (
                                    <div className="grid gap-6 grid-cols-1 lg:grid-cols-2">
                                        {series.map(s => (
                                            <FollowedSeriesCard
                                                key={s.id}
                                                series={s}
                                                onUnfollow={handleUnfollow}
                                            />
                                        ))}
                                    </div>
                                )}
                            </section>
                        </>
                    )}
                </main>
            </div>
            <Footer />
        </div>
    );
}
