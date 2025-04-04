package entity

/**
 * The different types of cards available from the board of the game
 *
 * each card influences gameplay differently
 */

enum class CardType {
    // A card that lets the current player play more tiles in the cultivate step
    GROWTHCARD,

    // A card that lets the current player play tiles at acquisition
    HELPERCARD,

    // A card that gives one to three tiles to the current player upon acquisition
    MASTERCARD,

    // A card that lets the current player keep more tiles before having to discard
    TOOLCARD,

    // A card that gives the current player victory at the end of the game
    PARCHMENTCARD
}
