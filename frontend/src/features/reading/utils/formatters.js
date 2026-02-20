export const formatDuration = (seconds, isActive = false) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (seconds === 0) return 'No time tracked yet';

    if (isActive) {
        if (hours > 0) {
            return `${hours} hr ${minutes} min`;
        } else if (minutes > 0) {
            return `${minutes} min · ${secs} sec`;
        } else {
            return `${secs} sec`;
        }
    }

    if (hours > 0) {
        return `~${hours} ${hours === 1 ? 'hour' : 'hours'}`;
    } else if (minutes > 0) {
        return `~${minutes} ${minutes === 1 ? 'minute' : 'minutes'}`;
    } else {
        return `${secs} ${secs === 1 ? 'second' : 'seconds'}`;
    }
};

export const formatEventType = (eventType) => {
    const typeMap = {
        'STARTED_READING': 'Started Reading',
        'RESTARTED_READING': 'Restarted Reading',
        'FINISHED_READING': 'Finished Reading',
        'ABANDONED_READING': 'Abandoned Reading'
    };
    return typeMap[eventType] || eventType;
};

export const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

export const formatSessionDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    }) + ' · ' + date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
    });
};

export const getSessionStatusInfo = (status) => {
    const statusMap = {
        'ONGOING_SESSION': {
            label: 'Ongoing',
            color: 'bg-blue-100 text-blue-800',
            icon: '🟢'
        },
        'COMPLETED_SESSION': {
            label: 'Completed',
            color: 'bg-green-100 text-green-800',
            icon: '✓'
        },
        'ABANDONED_SESSION': {
            label: 'Abandoned',
            color: 'bg-red-100 text-red-800',
            icon: '✗'
        }
    };
    return statusMap[status] || {
        label: status.replace(/_/g, ' '),
        color: 'bg-gray-100 text-gray-800',
        icon: '•'
    };
};

export const calculatePagesPerMinute = (pagesRead, durationSeconds) => {
    if (!pagesRead || !durationSeconds || durationSeconds === 0) return null;
    const minutes = durationSeconds / 60;
    const ppm = pagesRead / minutes;
    return ppm.toFixed(1);
};
