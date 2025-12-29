import React from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import AddAuthorForm from '../../features/books/components/AddAuthorForm';

export default function AddAuthorPage() {
    return (
        <div className="font-sans bg-white text-[#1d1d1f] min-h-screen flex flex-col">
            <Header />
            <div className="flex-1">
                <AddAuthorForm />
            </div>
            <Footer />
        </div>
    );
}
