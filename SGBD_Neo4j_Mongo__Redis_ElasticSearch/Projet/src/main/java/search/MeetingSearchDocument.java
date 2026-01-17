package search;

/**
 * Elasticsearch document model for meeting search.
 *
 * @param meetingId unique meeting identifier
 * @param userA     name of the first participant
 * @param userB     name of the second participant
 * @param interest  the shared interest of the meeting
 */
public record MeetingSearchDocument(
    long meetingId,
    String userA,
    String userB,
    String interest
) {}