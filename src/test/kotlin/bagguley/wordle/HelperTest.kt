package bagguley.wordle

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HelperTest {

    private val none = "_____"

    private fun find(
        words: Set<String>,
        invalid: String = "",
        correct: String = none,
        misplaced: List<String> = listOf(none),
    ) = findCandidates(words, invalid, correct, misplaced)

    @Test
    fun `no constraints returns every word`() {
        val words = setOf("adorn", "crane", "stale")
        assertEquals(words, find(words).toSet())
    }

    @Test
    fun `invalid letters exclude words containing them`() {
        assertEquals(listOf("adorn"), find(setOf("adorn", "stale", "crane"), invalid = "stleikc"))
    }

    @Test
    fun `correct letters must be in the exact position`() {
        val words = setOf("crane", "grane", "trace")
        assertEquals(setOf("crane", "grane"), find(words, correct = "_rane").toSet())
    }

    @Test
    fun `correct letters do not match when letters are only permuted`() {
        assertTrue(find(setOf("adorn"), correct = "rando").isEmpty())
    }

    @Test
    fun `misplaced letters must be present but not in that position`() {
        assertEquals(listOf("adorn"), find(setOf("adorn", "rando", "crane"), invalid = "stleik", misplaced = listOf("rando")))
    }

    @Test
    fun `misplaced letter in the same position is rejected`() {
        assertTrue(find(setOf("crane"), misplaced = listOf("c____")).isEmpty())
    }

    @Test
    fun `misplaced letter missing from the word is rejected`() {
        assertTrue(find(setOf("crane"), misplaced = listOf("____z")).isEmpty())
    }

    @Test
    fun `multiple misplaced rows all apply`() {
        val words = setOf("abbey", "babes", "bleak", "crane")
        // a not at 0 and b not at 1, both must be present
        val result = find(words, misplaced = listOf("a____", "_b___"))
        assertEquals(setOf("babes", "bleak"), result.toSet())
    }

    @Test
    fun `letter in correct and misplaced positions requires two occurrences`() {
        val words = setOf("speed", "creek", "wheat", "eerie")
        // e fixed at index 2, and another e that is not at index 0
        val result = find(words, correct = "__e__", misplaced = listOf("e____"))
        assertEquals(setOf("speed", "creek"), result.toSet())
    }

    @Test
    fun `second occurrence cannot be in the misplaced position`() {
        val words = setOf("geese", "speed", "creek")
        val result = find(words, correct = "__e__", misplaced = listOf("___e_"))
        assertEquals(setOf("geese"), result.toSet())
    }

    @Test
    fun `a correct letter alone does not require a second occurrence`() {
        val result = find(setOf("wheat", "speed"), correct = "__e__")
        assertEquals(setOf("wheat", "speed"), result.toSet())
    }

    @Test
    fun `case is ignored in patterns`() {
        assertEquals(listOf("crane"), find(setOf("crane", "grane"), correct = "C____"))
    }

    @Test
    fun `empty word set gives no results`() {
        assertTrue(find(emptySet(), correct = "c____").isEmpty())
    }

    @Test
    fun `processed returns index and letter pairs skipping blanks`() {
        assertEquals(listOf(0 to 'h', 2 to 'p', 3 to 'p'), "h_pp_".processed())
        assertEquals(listOf(1 to 'a'), "_A___".processed())
        assertTrue(none.processed().isEmpty())
    }

    @Test
    fun `requiredCounts counts correct plus the largest misplaced row`() {
        val counts = requiredCounts("__e__", listOf("e____", "ee___", "_r___"))
        assertEquals(mapOf('e' to 3, 'r' to 1), counts)
    }

    @Test
    fun `requiredCounts is empty when nothing is known`() {
        assertTrue(requiredCounts(none, listOf(none)).isEmpty())
    }

    @Test
    fun `loadWords reads trimmed lowercase non-empty lines`() {
        val words = loadWords("allWords.txt")
        assertTrue("adorn" in words)
        assertTrue(words.none { it.isEmpty() || it != it.trim().lowercase() })
    }

    @Test
    fun `loadWords fails clearly for a missing resource`() {
        val e = assertFailsWith<IllegalArgumentException> { loadWords("nope.txt") }
        assertTrue("nope.txt" in e.message!!)
    }
}
