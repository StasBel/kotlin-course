package ru.spbau.mit

enum class PoS { ADJ, NOUN, VERB }

enum class Gender { MALE, FEMALE }

data class Rule(val suffix: String, val pos: PoS, val gender: Gender)

object Language {
    val RULES = setOf(
            Rule("lios", PoS.ADJ, Gender.MALE),
            Rule("liala", PoS.ADJ, Gender.FEMALE),
            Rule("etr", PoS.NOUN, Gender.MALE),
            Rule("etra", PoS.NOUN, Gender.FEMALE),
            Rule("initis", PoS.VERB, Gender.MALE),
            Rule("inites", PoS.VERB, Gender.FEMALE)
    )
}

class PWord(raw: String) {
    private val firstRule = Language.RULES.singleOrNull { raw.endsWith(it.suffix) }

    val pos: PoS? by lazy { firstRule?.pos }

    val gender: Gender? by lazy { firstRule?.gender }
}

class PMessage(private val words: List<PWord>) {
    private fun oneGoodWord() = (words.size == 1) && (words.single().let { (it.pos != null) && (it.gender != null) })

    private fun sameGender() = words.map { it.gender }.let { (it.all { it != null }) && (it.distinct().size == 1) }

    private fun notNounNumMatch(): Boolean {
        val adjPrefixNum = words.map { it.pos }.takeWhile { it == PoS.ADJ }.size
        val verbSuffixNum = words.map { it.pos }.reversed().takeWhile { it == PoS.VERB }.size
        return adjPrefixNum + verbSuffixNum == words.size - 1
    }

    private fun singleNoun() = words.count { it.pos == PoS.NOUN } == 1

    fun singleSentence(): Boolean = oneGoodWord() || (sameGender() && notNounNumMatch() && singleNoun())
}

interface Solver {
    fun solve(rawInput: String): String
}

// http://codeforces.com/problemset/problem/113/A
object GrammarLessons : Solver {
    private fun parseWords(rawInput: String): List<PWord> = rawInput.split(" ").map { PWord(it) }

    override fun solve(rawInput: String): String {
        val message = PMessage(words = parseWords(rawInput))
        return if (message.singleSentence()) "YES" else "NO"
    }
}

interface Runner {
    fun run(solver: Solver)
}

object ConsoleRunner : Runner {
    private fun readRawInput(): String = readLine()!!

    override fun run(solver: Solver) = print(solver.solve(readRawInput()))
}

fun main(args: Array<String>) = ConsoleRunner.run(GrammarLessons)