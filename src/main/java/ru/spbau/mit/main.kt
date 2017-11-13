package ru.spbau.mit

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.*


private fun FunParser.setupErrorListener() {
    removeErrorListeners()
    addErrorListener(object : BaseErrorListener() {
        override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?
        ) = throw ParseCancellationException("error occurred at $line:$charPositionInLine", e)
    })
}


fun interpret(inputStream: InputStream, outputStream: OutputStream) {
    val lexer = FunLexer(CharStreams.fromStream(inputStream))
    val parser = FunParser(BufferedTokenStream(lexer))
    parser.setupErrorListener()
    val visitor = EvalVisitor(outputStream)
    visitor.visit(parser.file())
}


private fun eprintln(message: String) = System.err.println(message)


fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Please, provide only the path to a source file.")
        return
    }

    try {
        interpret(
                inputStream = FileInputStream(args.first()),
                outputStream = System.out
        )
    } catch (exception: IOException) {
        eprintln("IO exception: ${exception.message}")
    } catch (exception: ParseCancellationException) {
        eprintln("Parsing exception: ${exception.message}")
    } catch (exception: EvalException) {
        eprintln("Evaluation exception: ${exception.message}")
    }
}