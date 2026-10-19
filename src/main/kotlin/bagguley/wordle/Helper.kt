package bagguley.wordle

fun main() {
    val allWords = loadWords("allWords.txt")

    // Set to false if words can be reused
    val excludePastWords = true
    val pastWords = if (excludePastWords) loadWords("pastWords.txt") else emptySet()

    // Letters which are not in the answer
    val invalidLetters = ""

    // Letters in the correct place e.g. h_p_y, must be 5 characters long
    val correctLetters = "_____"

    // Letters in the answer but in the wrong place, must be 5 characters long
    val misplacedLetters : List<String> = listOf(
    )

    println("Possible Wordle Answers")
    println("=".repeat(23))

    findCandidates(allWords - pastWords, invalidLetters, correctLetters, misplacedLetters)
        .forEach { println(it) }
}

fun findCandidates(
    words: Set<String>,
    invalidLetters: String,
    correctLetters: String,
    misplacedLetters: List<String>,
): List<String> {
    val correctPairs = correctLetters.processed()
    val requiredCounts = requiredCounts(correctLetters, misplacedLetters)

    return words.filter { word ->
        word.none { it in invalidLetters } &&
            requiredCounts.all { (c, count) -> word.count { it == c } >= count } &&
            correctPairs.all { (index, c) -> word[index] == c } &&
            misplacedLetters.all { row -> row.processed().none { (index, c) -> word[index] == c } }
    }
}

fun loadWords(resource: String): Set<String> =
    requireNotNull(ClassLoader.getSystemResourceAsStream(resource)) { "Missing resource: $resource" }
        .bufferedReader()
        .use { reader -> reader.lineSequence().map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet() }

fun String.processed():List<Pair<Int, Char>> {
    return this.lowercase().mapIndexed { index, c -> index to c}
        .filter { it.second != '_' }
}

// Minimum occurrences of each letter: those in the correct place plus the most in any one misplaced row
fun requiredCounts(correct: String, misplaced: List<String>): Map<Char, Int> {
    val correctCounts = correct.lowercase().filter { it != '_' }.groupingBy { it }.eachCount()
    val misplacedCounts = misplaced
        .map { row -> row.lowercase().filter { it != '_' }.groupingBy { it }.eachCount() }
        .fold(emptyMap<Char, Int>()) { acc, row ->
            (acc.keys + row.keys).associateWith { maxOf(acc[it] ?: 0, row[it] ?: 0) }
        }
    return (correctCounts.keys + misplacedCounts.keys)
        .associateWith { (correctCounts[it] ?: 0) + (misplacedCounts[it] ?: 0) }
}
