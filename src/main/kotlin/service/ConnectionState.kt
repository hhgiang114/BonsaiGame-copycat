package service

/**
 * Enum to distinguish between bonsai [ConnectionState]s
 */
enum class ConnectionState {
    CONNECTED,
    DISCONNECTED,
    WAITING_FOR_HOST_CONFIRMATION,
    WAITING_FOR_JOIN_CONFIRMATION,
    WAITING_FOR_GUEST,
    WAITING_FOR_INIT,
    PLAYING_MY_TURN,
    WAITING_FOR_OPPONENT
}
