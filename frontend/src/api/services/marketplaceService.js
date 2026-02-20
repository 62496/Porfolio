import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const marketplaceService = {
    /**
     * Get all books with their marketplace data in one call
     * @param {boolean} inStockOnly - Only return books with at least one seller
     * @returns {Promise<Array>} - Array of MarketplaceBookListing objects
     */
    getAllBooks: async (inStockOnly = false) => {
        const params = inStockOnly ? { inStockOnly: true } : {};
        const response = await apiClient.get(API_ENDPOINTS.MARKETPLACE.GET_ALL_BOOKS, { params });
        return response.data;
    },

    /**
     * Get marketplace summary for a book including book info, lowest price, seller count
     * @param {string} isbn - ISBN of the book
     * @returns {Promise<Object>} - MarketplaceSummary with book details and marketplace info
     */
    getBookSummary: async (isbn) => {
        const response = await apiClient.get(API_ENDPOINTS.MARKETPLACE.GET_BOOK_SUMMARY(isbn));
        return response.data;
    },

    /**
     * Get all seller listings for a book (sorted by price ascending)
     * @param {string} isbn - ISBN of the book
     * @returns {Promise<Array>} - Array of SellerListing objects
     */
    getBookSellers: async (isbn) => {
        const response = await apiClient.get(API_ENDPOINTS.MARKETPLACE.GET_BOOK_SELLERS(isbn));
        return response.data;
    },

    /**
     * Format marketplace book listing for display
     * @param {Object} listing - Raw MarketplaceBookListing from API
     * @returns {Object} - Formatted book with marketplace data
     */
    formatListingForDisplay: (listing) => {
        if (!listing) return null;

        // Helper to format image URL - use as-is if already absolute, otherwise prepend base URL
        const formatImageUrl = (url) => {
            if (!url) return null;
            if (url.startsWith('http://') || url.startsWith('https://')) {
                return url; // Already absolute URL
            }
            const baseUrl = (process.env.REACT_APP_API_BASE_URL || 'http://localhost:8081/api').replace(/\/api$/, '');
            return `${baseUrl}${url}`;
        };

        const authors = listing.authors || [];
        const authorNames = authors.map(a => `${a.firstName} ${a.lastName}`.trim());

        return {
            isbn: listing.isbn,
            title: listing.title,
            description: listing.description,
            publishingYear: listing.publishingYear,
            pages: listing.pages,
            imageUrl: formatImageUrl(listing.imageUrl),
            authors: authors.map(author => ({
                id: author.id,
                firstName: author.firstName,
                lastName: author.lastName,
                fullName: `${author.firstName} ${author.lastName}`.trim(),
                imageUrl: formatImageUrl(author.imageUrl),
            })),
            author: authorNames.join(', ') || 'Unknown Author',
            marketplace: {
                lowestPrice: listing.lowestPrice,
                sellerCount: listing.sellerCount,
                totalQuantityAvailable: listing.totalQuantityAvailable,
                inStock: listing.inStock,
            },
        };
    },

    /**
     * Format seller listing for display
     * @param {Object} seller - Raw seller listing from API
     * @returns {Object} - Formatted seller object
     */
    formatSellerForDisplay: (seller) => {
        if (!seller) return null;

        return {
            sellerId: seller.sellerId,
            sellerName: `${seller.sellerFirstName} ${seller.sellerLastName}`.trim(),
            sellerFirstName: seller.sellerFirstName,
            sellerLastName: seller.sellerLastName,
            sellerPicture: seller.sellerPicture,
            quantity: seller.quantity,
            pricePerUnit: seller.pricePerUnit,
        };
    },
};

export default marketplaceService;
