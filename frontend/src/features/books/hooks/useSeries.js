import { useCallback, useEffect, useState } from 'react';
import seriesService from '../../../api/services/seriesService';

export const useSeries = (autoFetch = true) => {
    const [series, setSeries] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchSeries = useCallback(async (params = {}) => {
        setLoading(true);
        setError(null);
        try {
            const data = await seriesService.getAll(params);
            setSeries(Array.isArray(data) ? data : []);
            return data;
        } catch (err) {
            setError(err);
            console.error('useSeries: fetchSeries error', err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (autoFetch) {
            fetchSeries().catch(() => {});
        }
  }, [autoFetch, fetchSeries]);

  return { series, loading, error, fetchSeries, setSeries };
};

export default useSeries;