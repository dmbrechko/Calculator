package com.example.calculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.databinding.ActivityMainBinding
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.apply {
            plusBTN.setOnClickListener {
                checkAndGo(this) { first, second -> first + second}
            }
            minusBTN.setOnClickListener {
                checkAndGo(this) { first, second -> first - second}
            }
        }
    }

    private fun checkInput(input: String): Boolean {
        return input.matches("([1-9][0-9]*[hH])?([0-5]?[0-9]?[mM])?([0-5]?[0-9]?[sS])?".toRegex())
    }

    private fun parseTimeFromString(input: String): Int {
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
        return timeHolder.run { hours * HOURS + minutes * MINUTES + seconds }
    }

    private fun timeInSecondsToString(time: Int): String {
        val prefix = if (time < 0) "-" else ""
        val hours = abs(time) / HOURS
        val minutes = (abs(time) - hours * HOURS) / MINUTES
        val seconds = abs(time) - hours * HOURS - minutes * MINUTES
        return "$prefix${hours}h${minutes}m${seconds}s"
    }

    private fun calculate(first: String, second: String, op: (Int, Int) -> Int): String {
        val firstTimeInMillis = parseTimeFromString(first)
        val secondTimeInMillis = parseTimeFromString(second)
        val result = op(firstTimeInMillis, secondTimeInMillis)
        return timeInSecondsToString(result)
    }

    private fun checkAndGo(binding: ActivityMainBinding, op: (Int, Int) -> Int) {
        binding.apply {
            val first = firstOperandET.text.toString()
            val second = secondOperandET.text.toString()
            if (checkInput(first) && checkInput(second)) {
                resultTV.text = calculate(first, second, op)
            } else {
                Toast.makeText(this@MainActivity,
                    getString(R.string.wrong_input_try_again), Toast.LENGTH_LONG).show()
            }
        }
    }
}

const val HOURS = 3600
const val MINUTES = 60

data class TimeHolder(var hours: Int = 0, var minutes: Int = 0, var seconds: Int = 0)