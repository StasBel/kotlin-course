package ru.spbau.mit

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class TestSource {
    private fun assertRaw(
            expected: String,
            rawInput: String,
            message: String? = null,
            solver: Solver = GrammarLessons
    ) {
        assertEquals(expected, solver.solve(rawInput), message)
    }

    private object PCodedRandom {
        private fun nextAlpha(): String {
            return UUID.randomUUID().toString().filter { it.isLetter() && it.isLowerCase() }
        }

        private fun nextWord(pos: PoS, gender: Gender): String {
            return nextAlpha() + Language.RULES.first { (it.pos == pos) && (it.gender == gender) }.suffix
        }

        private fun nextWords(desc: List<Pair<PoS, Gender>>): String {
            return desc.joinToString(" ") { nextWord(it.first, it.second) }
        }

        private fun parseDesc(code: String): Pair<PoS, Gender> {
            return when (code) {
                "AM" -> Pair(PoS.ADJ, Gender.MALE)
                "AF" -> Pair(PoS.ADJ, Gender.FEMALE)
                "NM" -> Pair(PoS.NOUN, Gender.MALE)
                "NF" -> Pair(PoS.NOUN, Gender.FEMALE)
                "VM" -> Pair(PoS.VERB, Gender.MALE)
                "VF" -> Pair(PoS.VERB, Gender.FEMALE)
                else -> throw IllegalArgumentException("Wrong code")
            }
        }

        fun next(codedInput: String): String {
            return codedInput.split(" ").flatMap {
                when {
                    it.length > 2 -> {
                        val num = Integer.valueOf(it.slice(0..(it.length - 3)))
                        val desc = parseDesc(it.slice((it.length - 2)..(it.length - 1)))
                        List(num) { desc }
                    }
                    else -> listOf(parseDesc(it))
                }
            }.let { nextWords(it) }
        }
    }

    private fun assertCoded(
            expected: String,
            codedInput: String,
            message: String? = null
    ) = assertRaw(expected, PCodedRandom.next(codedInput), message)

    @Test
    fun testSimple() {
        assertRaw("NO", "a", "One word")
        assertRaw("NO", "a b", "Two words")
        assertRaw("NO", "a b c", "Three words")
    }

    @Test
    fun testExamples() {
        assertRaw("YES", "petr", "Example #1")
        assertRaw("NO", "etis atis animatis etis atis amatis", "Example #2")
        assertRaw("YES", "nataliala kataliala vetra feinites", "Example #3")
    }

    @Test
    fun testSuffix() {
        assertRaw("YES", "lios", "Labeled suffix #1")
        assertRaw("YES", "liala", "Labeled suffix #2")
        assertRaw("YES", "etr", "Labeled suffix #3")
        assertRaw("YES", "etra", "Labeled suffix #4")
        assertRaw("YES", "initis", "Labeled suffix #5")
        assertRaw("YES", "inites", "Labeled suffix #6")
    }

    @Test
    fun testComplex() {
        assertRaw("NO", "asilios posinities", "Different genus")
        assertRaw("NO", "perinitis giliala", "First verb, then adj")
        assertRaw("YES", "fffetr", "Single noun")
        assertRaw("YES", "alios blios clios ketr", "Zero verbs")
        assertRaw("YES", "letra finites dinites", "Zero adjs")
        assertRaw("NO", "ulios retr oetr sinitis jinitis", "More then one nouns")
        assertRaw("NO", "jliala hinites kliala eetra", "Verb before noun")
        assertRaw("NO", "eetra opainites wqeliala yinites", "Adj after noun")
    }

    @Test
    fun testCoded() {
        assertCoded("YES", "AM NM VM", "Simple male")
        assertCoded("YES", "AF NF VF", "Simple female")
        assertCoded("YES", "3AM NM 5VM", "Correct single sentence")
        assertCoded("NO", "3AM NM 2VM VF 2VM", "One female genus and bunch of males")
        assertCoded("NO", "111AM 345VM", "Absent of noun")
        assertCoded("NO", "VM 567AM NM 987VM", "Verb before adj")
        assertCoded("YES", "NF 1342VF", "Zero adjs")
        assertCoded("YES", "11345AF NF", "Zero verbs")
        assertCoded("NO", "11345AF NF 762AF NF 10VF", "Noun inside adjs")
        assertCoded("NO", "980AM NM 555VM NM 345VM", "Noun inside verbs")
        assertCoded("NO", "137AF 728AM 236AF NM 263VM 3216VF 7VM", "Mix of genders")
    }
}
