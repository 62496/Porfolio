import useBookCollections from "../../collections/hooks/useBookCollections";
import { useNavigate } from "react-router-dom";
import React, { useEffect, useState } from "react";
import authService from "../api/services/authService";


/*
  This component displays all collections in a grid for quick access
*/
export function CollectionGrid({ collections }) {
  const navigate = useNavigate();

  const onSelect = (e) => {
    navigate(`/profile/${e.id}`);
  };

  const authId = authService.getCurrentUser().id

  return (
    <div className="p-4">
      <h2 className="text-2xl font-semibold mb-6">Collections</h2>

      {collections.length === 0 ? (
        <div className="text-gray-500 italic">Empty</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {collections.map((c) => (
            <div
              key={c.id}
              onClick={() => onSelect(c)}
              className="p-5 border border-gray-200 rounded-2xl shadow-sm cursor-pointer hover:shadow-md hover:bg-gray-50 transition-all duration-200"
            >
              <h3 className="text-lg font-medium text-gray-800">{c.name}</h3>
              {authId !== c.owner.id && (
              <div className="text-xs italic text-gray-400 mt-1">
                shared by {c?.owner.firstName} {c?.owner.lastName} 
              </div>
            )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/*
  This component allows managing books by clicking on a collection
*/
export default function BookCollectionModalChildList({ bookIsbn }) {
  const {
    collections,
    addBookToCollection,
    removeBookFromCollection,
    collectionContainsBook,
  } = useBookCollections(true);

  const [collectionsStatus, setCollectionsStatus] = useState({});

  useEffect(() => {
    const fetchStatus = async () => {
      const status = {};
      for (const c of collections) {
        status[c.id] = await collectionContainsBook(c.id, bookIsbn);
      }
      setCollectionsStatus(status);
    };

    if (collections?.length > 0 && bookIsbn) {
      fetchStatus();
    }
  }, [collections, bookIsbn, collectionContainsBook]);

  const handleSelect = async (collectionId) => {
    const alreadyInside = collectionsStatus[collectionId];

    if (alreadyInside) {
      await removeBookFromCollection(collectionId, bookIsbn);
      setCollectionsStatus((prev) => ({ ...prev, [collectionId]: false }));
    } else {
      await addBookToCollection(collectionId, bookIsbn);
      setCollectionsStatus((prev) => ({ ...prev, [collectionId]: true }));
    }
  };

  if (!collections || collections.length === 0) {
    return <p className="text-gray-600">No collections available.</p>;
  }

  return (
    <ul className="space-y-2 max-h-64 overflow-y-auto">
      {collections.map((c) => {
        const isInCollection = collectionsStatus[c.id];
        return (
          <li key={c.id}>
            <button
              onClick={() => handleSelect(c.id)}
              className={`w-full text-left px-3 py-2 border rounded-lg transition-colors
                ${
                  isInCollection
                    ? "bg-gray-400 text-white hover:bg-red-600"
                    : "bg-gray-100 hover:bg-gray-200 text-gray-900"
                }
              `}
            >
              {c.name}
            </button>
          </li>
        );
      })}
    </ul>
  );
}
