import React from 'react';
import Header from "../../components/layout/Header"
import Footer from "../../components/layout/Footer"
import BookForm from "../../features/books/components/BookForm"

const AddBookPage = () => {
  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header />
      <div className="flex-1">
        <BookForm mode="create" />
      </div>
      <Footer />
    </div>
  );
};

export default AddBookPage;