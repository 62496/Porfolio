export const API_ENDPOINTS = {
    BOOKS: {
        GET_ALL: '/books',
        GET_BY_ID: (id) => `/books/${id}`,
        GET_BY_ISBN: (isbn) => `/books/${isbn}`,
        GET_BY_AUTHOR: (authorId) => `/books/author/${authorId}`,
        CREATE: '/books',
        UPDATE: (id) => `/books/${id}`,
        DELETE: (id) => `/books/${id}`,
        SEARCH: '/books/search',
        FILTER: '/books/filter',
        BY_SERIES: (seriesId) => `/books/series/${seriesId}`,
        CREATE_BOOK_READING_EVENT: (id) => `/books/${id}/read-events`,
        GET_LATEST_BOOK_READING_EVENT: (isbn) => `/books/${isbn}/read-event/latest`,
        GET_ALL_BOOK_READING_SESSIONS: (isbn) => `/books/${isbn}/reading-sessions`,
        GET_ALL_BOOK_READING_EVENTS: (isbn) => `/books/${isbn}/reading-events`
    },

    READING_SESSION: {
        CREATE: '/reading-sessions',
        DELETE: (id) => `/reading-sessions/${id}`,
        PAUSE: (id) => `/reading-sessions/${id}/pause`,
        RESUME: (id) => `/reading-sessions/${id}/resume`,
        END: (id) => `/reading-sessions/${id}/end`,
    },

    REPORTS: {
        CREATE_BOOK_REPORT: (id) => `/books/${id}/reports`,
        GET_ALL: '/reports',
        RESOLVE_BOOK_REPORT: (id) => `/reports/books/${id}/resolve`,
        DISMISS_REPORT: (id) => `/reports/books/${id}/dismiss`
    },

    INVENTORY: {
        GET_ALL: '/inventory',
        CREATE: '/inventory',
        UPDATE: (bookIsbn) => `/inventory/${bookIsbn}`,
        DELETE: (bookIsbn) => `/inventory/${bookIsbn}`,
    },

    AUTHORS: {
        GET_ALL: '/authors',
        GET_BY_ID: (id) => `/authors/${id}`,
        CREATE: '/authors',
        UPDATE: (id) => `/authors/${id}`,
        DELETE: (id) => `/authors/${id}`,
    },

    SUBJECTS: {
        GET_ALL: '/subjects',
        GET_BY_ID: (id) => `/subjects/${id}`,
        CREATE: '/subjects',
        UPDATE: (id) => `/subjects/${id}`,
        DELETE: (id) => `/subjects/${id}`,
    },

    SERIES: {
        GET_ALL: '/series',
        GET_BY_ID: (id) => `/series/${id}`,
        GET_BY_AUTHOR: (authorId) => `/series/author/${authorId}`,
        GET_BOOKS: (id) => `/series/${id}/books`,
        CREATE: '/series',
        UPDATE: (id) => `/series/${id}`,
        DELETE: (id) => `/series/${id}`,
        ADD_BOOK: (id, isbn) => `/series/${id}/books/${isbn}`,
        REMOVE_BOOK: (id, isbn) => `/series/${id}/books/${isbn}`,
    },

    AUTH: {
        GOOGLE: '/auth/google',
        REFRESH: '/auth/refresh',
        LOGOUT: '/auth/logout',
        ME: '/auth/me',
    },

    USERS: {
        SEARCH_GOOGLE: '/users/search-google',

        ADD_FAVORITE: (isbn) => `/users/favorites/${isbn}`,
        REMOVE_FAVORITE: (isbn) => `/users/favorites/${isbn}`,
        GET_FAVORITES: '/users/favorites',

        FOLLOW_AUTHOR: (authorId) => `/users/follow/author/${authorId}`,
        UNFOLLOW_AUTHOR: (authorId) => `/users/follow/author/${authorId}`,
        GET_FOLLOWED_AUTHORS: '/users/followed-authors',
        IS_FOLLOWING_AUTHOR: (authorId) => `/users/follow/author/${authorId}`,

        FOLLOW_SERIES: (seriesId) => `/users/follow/series/${seriesId}`,
        UNFOLLOW_SERIES: (seriesId) => `/users/follow/series/${seriesId}`,
        GET_FOLLOWED_SERIES: '/users/followed-series',
        IS_FOLLOWING_SERIES: (seriesId) => `/users/follow/series/${seriesId}`,

        GET_OWNED_BOOKS: (userId) => `/users/${userId}/owned-books`,
        ADD_OWNED_BOOK: (userId, isbn) => `/users/${userId}/owned-books/${isbn}`,
        REMOVE_OWNED_BOOK: (userId, isbn) => `/users/${userId}/owned-books/${isbn}`,
    },

    CURRENT_USER: {
        GET_BOOKS_WITH_READ_STATUS: '/me/books',
        GET_BOOKS_WITH_EXISTING_READ_STATUS: '/me/reading-progress/books'
    },

    MESSAGES: {
        LIST_CONVERSATIONS: (userId) => `/messages/conversations?userId=${userId}`,
        GET_CONVERSATION: (conversationId, userId) => `/messages/conversations/${conversationId}?userId=${userId}`,
        SEND: '/messages',
        MARK_AS_READ: (conversationId) => `/messages/conversations/${conversationId}/read`,
    },

    COLLECTIONS: {
        CREATE: '/collections',
        UPDATE: (id) => `/collections/${id}`,
        DELETE: (id) => `/collections/${id}`,
        GET_BY_ID: (id) => `/collections/${id}`,

        GET_OWN: '/collections',
        GET_ALLOWED: '/collections/allowed',
        GET_PUBLIC: '/collections/public',
        GET_SHARED: '/collections/shared',

        CHECK_ACCESS: (id) => `/collections/${id}/access`,

        SHARE: (id, email) => `/collections/${id}/share/${email}`,
        UNSHARE: (id, userId) => `/collections/${id}/share/${userId}`,

        ADD_BOOK: (id, isbn) => `/collections/${id}/books/${isbn}`,
        REMOVE_BOOK: (id, isbn) => `/collections/${id}/books/${isbn}`,
        CONTAINS_BOOK: (id, isbn) => `/collections/${id}/books/${isbn}`,

        GET_IMAGE: (id) => `/images/collections/${id}`,
    },

    MARKETPLACE: {
        GET_ALL_BOOKS: '/marketplace/books',
        GET_BOOK_SUMMARY: (isbn) => `/marketplace/books/${isbn}/summary`,
        GET_BOOK_SELLERS: (isbn) => `/marketplace/books/${isbn}/sellers`,
    },

    ADMIN: {
        GET_ALL_USERS: '/admin/users',
        GET_USER_BY_ID: (id) => `/admin/users/${id}`,
        GET_ALL_ROLES: '/admin/roles',
        ADD_ROLE: (userId, roleName) => `/admin/users/${userId}/roles/${roleName}`,
        REMOVE_ROLE: (userId, roleName) => `/admin/users/${userId}/roles/${roleName}`,
    },
};

export default API_ENDPOINTS;
