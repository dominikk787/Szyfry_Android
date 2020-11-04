package com.dominikk787.szyfry

fun String.toLatin(): String {
    val original = arrayOf('Ą', 'ą', 'Ć', 'ć', 'Ę', 'ę', 'Ł', 'ł', 'Ń', 'ń', 'Ó', 'ó', 'Ś', 'ś', 'Ź', 'ź', 'Ż', 'ż')
    val normalized = arrayOf('A', 'a', 'C', 'c', 'E', 'e', 'L', 'l', 'N', 'n', 'O', 'o', 'S', 's', 'Z', 'z', 'Z', 'z')

    return this.map { char ->
        val index = original.indexOf(char)
        if (index >= 0) normalized[index] else char
    }.joinToString("")
}

fun Int.modulo(other: Int): Int {
    var m = this.rem(other)
    if(m < 0) m += other
    return m
}