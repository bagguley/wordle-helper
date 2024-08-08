package bagguley.wordle

fun main() {
    val allWordsStream = ClassLoader.getSystemResourceAsStream("allWords.txt")
    val allWords = allWordsStream!!.bufferedReader().use { it.readText() }.split("\n").toSet()

    val pastWordsStream = ClassLoader.getSystemResourceAsStream("pastWords.txt")
    val pastWords = pastWordsStream!!.bufferedReader().use { it.readText() }.split("\n").toSet()

    // Letters which are not in the answer
    val invalidLetters = ""

    // Letters in the correct place e.g. h_p_y, must be 5 characters long
    val correctLetters = "_____"

    // Letters in the answer but in the wrong place, must be 5 characters long
    val incorrectLetters = listOf(
        "_____"
    )

    println("Possible Wordle Answers")
    println("=".repeat(23))

    val correctLetterPairs = correctLetters.processed()
    val incorrectLetterSet = incorrectLetters.letters()

    allWords.minus(pastWords)
        .asSequence().filter {
            word -> word.none { c -> c in invalidLetters.toList() }
        }.filter {
            word -> incorrectLetterSet
                .all { c -> word.contains(c) }
        }.filter {
            word -> correctLetterPairs
                .all { (index, c) -> word[index] == c }
        }.filter {
            word -> incorrectLetters.all {
                it.processed()
                    .none { (index, c) -> word[index] == c }
            }
        }.toList().forEach { println(it) }
}

fun String.processed(): List<Pair<Int, Char>> {
    return this.mapIndexed { index, c -> index to c}
        .filter { it.second != '_' }
}

fun List<String>.letters(): Set<Char> {
    return this.flatMap { it.toList().filter { it != '_' } }.toSet()
}