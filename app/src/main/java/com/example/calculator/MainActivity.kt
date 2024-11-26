package com.example.calculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
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
            setSupportActionBar(toolbar)
            plusBTN.setOnClickListener {
                checkAndGo(this) { first, second -> first + second}
            }
            minusBTN.setOnClickListener {
                checkAndGo(this) { first, second -> first - second}
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.clear -> {
                binding.apply {
                    firstOperandET.text.clear()
                    secondOperandET.text.clear()
                    resultTV.text = getString(R.string.result)
                    resultTV.setTextColor(getColor(R.color.black))
                }
                makeToast(R.string.data_cleared)
                true
            }
            R.id.exit -> {
                makeToast(getString(R.string.app_closed))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                val result = String.format(getString(R.string.result_placeholder), calculate(first, second, op))
                resultTV.text = result
                resultTV.setTextColor(getColor(R.color.red_dark))
                makeToast(result)
            } else {
                Toast.makeText(this@MainActivity,
                    getString(R.string.wrong_input_try_again), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun makeToast(@StringRes strId: Int) {
        Toast.makeText(this, getString(strId), Toast.LENGTH_LONG).show()
    }

    private fun makeToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }
}

const val HOURS = 3600
const val MINUTES = 60

data class TimeHolder(var hours: Int = 0, var minutes: Int = 0, var seconds: Int = 0)