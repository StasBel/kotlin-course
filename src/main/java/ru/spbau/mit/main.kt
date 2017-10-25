package ru.spbau.mit

enum class PoS { ADJ, NOUN, VERB }

enum class Gender { MALE, FEMALE }

data class Rule(val suffix: String, val pos: PoS, val gender: Gender)

val rules = setOf(
        Rule("lios", PoS.ADJ, Gender.MALE),
        Rule("liala", PoS.ADJ, Gender.FEMALE),
        Rule("etr", PoS.NOUN, Gender.MALE),
        Rule("etra", PoS.NOUN, Gender.FEMALE),
        Rule("initis", PoS.VERB, Gender.MALE),
        Rule("inites", PoS.VERB, Gender.FEMALE)
)

class PWord(raw: String) {
    private val firstRule by lazy {
        rules.firstOrNull { raw.endsWith(it.suffix) }
    }

    val pos: PoS? by lazy {
        firstRule?.pos
    }

    val gender: Gender? by lazy {
        firstRule?.gender
    }
}

class PMessage(words: List<PWord>) {
    private val oneGoodWord by lazy {
        (words.size == 1) && (words.single().let { (it.pos != null) && (it.gender != null) })
    }

    private val sameGender by lazy {
        words.map { it.gender }.let { (it.all { it != null }) && (it.distinct().size == 1) }
    }

    private val notNounNumMatch by lazy {
        val adjPrefixNum = words.map { it.pos }.takeWhile { it == PoS.ADJ }.size
        val verbSuffixNum = words.map { it.pos }.reversed().takeWhile { it == PoS.VERB }.size
        adjPrefixNum + verbSuffixNum == words.size - 1
    }

    private val singleNoun by lazy {
        words.map { it.pos }.count { it == PoS.NOUN } == 1
    }

    val singleSentence: Boolean by lazy {
        oneGoodWord || (sameGender && notNounNumMatch && singleNoun)
    }
}

interface Solver {
    fun solve(rawInput: String): String
}

// http://codeforces.com/problemset/problem/113/A
object GrammarLessons : Solver {
    private fun parseWords(rawInput: String): List<PWord> = rawInput.split(" ").map { PWord(it) }

    override fun solve(rawInput: String): String {
        val message = PMessage(words = parseWords(rawInput))
        return if (message.singleSentence) "YES" else "NO"
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