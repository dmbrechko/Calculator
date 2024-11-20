package com.example.calculator

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val regex = "([1-9][0-9]*[hH])?([0-5]?[0-9]?[mM])?([0-5]?[0-9]?[sS])?".toRegex()
        assertTrue("134h32m14s".matches(regex))
        assertTrue("32m14s".matches(regex))
        assertTrue("14s".matches(regex))
        assertTrue("1h30m".matches(regex))
        assertFalse("034h32m14s".matches(regex))
        assertFalse("34h32m14".matches(regex))
    }

    fun parseTimeFromString(input: String): TimeHolder {
        val timeHolder = TimeHolder()
        val accum = mutableListOf<Char>()
        for (ch in input) {
            if (ch.isDigit()) {
                accum.add(ch)
            } else {
                val num = accum.joinToString("").toInt()
                when (ch) {
                    'h', 'H' -> timeHolder.hours = num
                    'm', 'M' -> timeHolder.minutes = num
                    's', 'S' -> timeHolder.seconds = num
                }
                accum.clear()
            }
        }
        return timeHolder
    }

    @Test
    fun toAndBack(){
        val str = "12h14m11s"
        val holder = parseTimeFromString(str)
        val res = timeInSecondsToString(holder.hours * 3600 + holder.minutes * 60 + holder.seconds)
        assertEquals("", str, res)
    }

    fun timeInSecondsToString(time: Int): String {
        val hours = time / 3600
        val minutes = (time - hours * 3600) / 60
        val seconds = time - hours * 3600 - minutes * 60
        return "${hours}h${minutes}m${seconds}s"
    }


}