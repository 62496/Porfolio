import { useState, useEffect, useCallback } from "react";
import bookCollectionsService from "../../../api/services/bookCollectionsService";

export const useBookCollections = (autoFetch = true) => {
  const [collections, setCollections] = useState([]);
  const [collectionsAllowed, setCollectionsAllowed] = useState([]);
  const [currentCollection, setCurrentCollection] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Fetch all Collections created by current user
   */
  const fetchBookCollections = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await bookCollectionsService.getOwnCollections();
      setCollections(data);
      return data;
    } catch (err) {
      setError(err.response?.data?.message || "Failed to fetch collections");
      console.error("Error fetching collections:", err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Fetch all Collections allowed for current user
   */
  const fetchBookCollectionsAllowed = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await bookCollectionsService.getAllowedCollections();
      setCollectionsAllowed(data);
      return data;
    } catch (err) {
      setError(err.response?.data?.message || "Failed to fetch collections");
      console.error("Error fetching collections:", err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const CreateBookCollection = useCallback(
    async (name, description) => {
      setLoading(true);
      setError(null);

      try {
        const data = await bookCollectionsService.create({ name, description });
        fetchBookCollections();
        fetchBookCollectionsAllowed();
        return data;
      } catch (err) {
        setError(err.response?.data?.message || "Failed to create collection");
        console.error("Error creating collection:", err);
      } finally {
        setLoading(false);
      }
    },
    [fetchBookCollections, fetchBookCollectionsAllowed]
  );

  /**
   * Fetch a Collection if user has access
   */
  const fetchABookCollection = useCallback(async ({ collectionId }) => {
    setLoading(true);
    setError(null);

    try {
      const data = await bookCollectionsService.getById(collectionId);
      setCurrentCollection(data);
      return data;
    } catch (err) {
      setError(err.response?.data?.message || "Failed to fetch collection");
      console.error("Error fetching collection:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  const deleteBookCollection = useCallback(async ({ collectionId }) => {
    setLoading(true);
    setError(null);

    try {
      const data = await bookCollectionsService.delete(collectionId);
      setCurrentCollection(null);
      return data;
    } catch (err) {
      setError(err.response?.data?.message || "Failed to delete collection");
      console.error("Error deleting collection:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  const shareCollection = useCallback(async (collectionId, userEmail) => {
    setLoading(true);
    setError(null);
    try {
      const data = await bookCollectionsService.shareWithUser(collectionId, userEmail);
      setCurrentCollection(data);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.message ||
          "Failed to share collection"
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const unshareCollection = useCallback(async (collectionId, userId) => {
    setLoading(true);
    setError(null);
    try {
      const data = await bookCollectionsService.unshareWithUser(collectionId, userId);
      setCurrentCollection(data);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message ||
          err.message ||
          "Failed to unshare collection"
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const addBookToCollection = useCallback(async (collectionId, isbn) => {
    setLoading(true);
    setError(null);
    try {
      const data = await bookCollectionsService.addBook(collectionId, isbn);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message || err.message || "Failed to add book"
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const removeBookFromCollection = useCallback(async (collectionId, isbn) => {
    setLoading(true);
    setError(null);
    try {
      const data = await bookCollectionsService.removeBook(collectionId, isbn);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message || err.message || "Failed to remove book"
      );
    } finally {
      setLoading(false);
    }
  }, []);

  const collectionContainsBook = useCallback(async (collectionId, isbn) => {
    try {
      const data = await bookCollectionsService.containsBook(collectionId, isbn);
      return data;
    } catch (err) {
      setError(
        err.response?.data?.message || err.message || "Failed to check if collection contains book"
      );
    }
  }, []);

  useEffect(() => {
    if (autoFetch) {
      fetchBookCollections();
      fetchBookCollectionsAllowed();
    }
  }, [autoFetch, fetchBookCollections, fetchBookCollectionsAllowed]);

  return {
    collections,
    collectionsAllowed,
    currentCollection,
    loading,
    error,
    fetchBookCollections,
    fetchBookCollectionsAllowed,
    CreateBookCollection,
    fetchABookCollection,
    deleteBookCollection,
    shareCollection,
    unshareCollection,
    addBookToCollection,
    removeBookFromCollection,
    collectionContainsBook,
  };
};

export default useBookCollections;
