import { useState, useEffect, useCallback } from 'react';
import inventoryService from '../../../api/services/inventoryService';

export const useInventory = (autoFetch = true) => {
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchInventoryByUser = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await inventoryService.getAllBookByUser();
      setInventory(data);
      return data;
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to fetch inventory');
      console.error('Error fetching inventory:', err);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const createInventory = useCallback(
    async (params = {}) => {
      setLoading(true);
      setError(null);

      try {
        const data = await inventoryService.createInventory(params);
        // Optionnel : on recharge la liste après création
        await fetchInventoryByUser();
        return data;
      } catch (err) {
        setError(
          err?.response?.data?.message || 'Failed to create inventory entry'
        );
        console.error('Error creating inventory:', err);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [fetchInventoryByUser]
  );

  const incrementInventory = useCallback(
    async (isbn, params = {}) => {
      setLoading(true);
      setError(null);

      try {
        const data = await inventoryService.incrementInventory(isbn, params);
        await fetchInventoryByUser();
        return data;
      } catch (err) {
        setError(
          err?.response?.data?.message || 'Failed to increment inventory'
        );
        console.error('Error incrementing inventory:', err);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [fetchInventoryByUser]
  );

  const decrementInventory = useCallback(
    async (isbn, params = {}) => {
      setLoading(true);
      setError(null);

      try {
        const data = await inventoryService.decrementInventory(isbn, params);
        await fetchInventoryByUser();
        return data;
      } catch (err) {
        setError(
          err?.response?.data?.message || 'Failed to decrement inventory'
        );
        console.error('Error decrementing inventory:', err);
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [fetchInventoryByUser]
  );

  useEffect(() => {
    if (autoFetch) {
      fetchInventoryByUser();
    }
  }, [autoFetch, fetchInventoryByUser]);

  return {
    inventory,
    loading,
    error,
    fetchInventoryByUser, 
    createInventory,      
    incrementInventory,   
    decrementInventory,   
  };
};
