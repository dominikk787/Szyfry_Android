package com.dominikk787.szyfry

import java.util.*

object Morse {
    private val morse_codes = mapOf('a' to ".-", 'b' to "-...", 'c' to "-.-.", 'd' to "-..",
            'e' to ".", 'f' to "..-.", 'g' to "--.", 'h' to "....", 'i' to "..", 'j' to ".---",
            'k' to "-.-", 'l' to ".-..", 'm' to "--", 'n' to "-.", 'o' to "---", 'p' to ".--.",
            'q' to "--.-", 'r' to ".-.", 's' to "...", 't' to "-", 'u' to "..-", 'v' to "...-",
            'w' to ".--", 'x' to "-..-", 'y' to "-.--", 'z' to "--..", '.' to ".-.-.-", ',' to "--..--",
            '\'' to ".----.", '"' to ".-..-.", '_' to "..--.-", ':' to "---...", ';' to "-.-.-.",
            '?' to "..--..", '!' to "-.-.--", '-' to "-....-", '+' to ".-.-.", '/' to "-..-.",
            '(' to "-.--.", ')' to "-.--.-", '=' to "-...-", '@' to ".--.-.", 'ą' to ".-.-",
            'ć' to "-.-..", 'ę' to "..-..", 'ł' to ".-..-", 'ń' to "--.--", 'ó' to "---.",
            'ś' to "...-.", 'ż' to "--..-.", 'ź' to "--..-", '0' to "-----", '1' to ".----",
            '2' to "..---", '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....",
            '7' to "--...", '8' to "---..", '9' to "----.", ' ' to "")

    private fun code2ch(code: String) : Char {
        for((ch, m_code) in morse_codes) {
            println("$code $m_code")
            if(code == m_code) {
                return ch
            }
        }
        return '^'
    }

    fun encode(string: String) : String {
        val instr = string.trimIndent()
        println(instr)
        val outlist = mutableListOf<String>()
        for(inch in instr) {
            outlist.add(morse_codes[inch] ?: "error")
        }
        println(outlist)
        var outstr = outlist.joinToString("/", postfix = "/")
        if(!outstr.endsWith("//")) outstr += "/"
        return outstr
    }

    fun decode(string: String) : String {
        var instr = string.toLowerCase(Locale.getDefault())
        instr = instr.replace(' ', '/').replace("/{3,}".toRegex(), "//")
        instr = instr.filter {
            it == '.' || it == '-' || it == '/'
        }
        println(instr)
        var outstr = ""
        for(code in instr.split('/')) {
            outstr += code2ch(code)
        }
        println(outstr)
        return outstr
    }
}