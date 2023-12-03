package group10.com.guesstheera.backend

data class Score(
    val user_id: String,
    val difficulty: String,
    var wins: Int,
    var losses: Int
)
