package com.example.composecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composecalculator.ui.theme.ComposeCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeCalculatorTheme {
                Calculator()
            }
        }
    }
}

@Composable
fun Calculator() {
    var result by remember { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(text = result, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(20.dp))

            val buttons = listOf("7", "8", "9", "/",
                                "4", "5", "6", "*",
                                "1", "2", "3", "-",
                                "C", "0", "=", "+")

            buttons.chunked(4).forEach {row -> Row { row.forEach { button ->
                Button(onClick = {result = calculate(result, button)},
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp))
                {
                    Text(button)
                }
            }}}
        }
    }
}

fun calculate(currentResult: String, input: String): String {
    if (currentResult == "Error") return input

    val operations = arrayOf("/", "*", "-", "+")

    val data = currentResult.split(" ").filter { it.isNotEmpty() }
    val lastInserted = data.lastOrNull()

    if (!lastInserted.isNullOrEmpty() && operations.contains(lastInserted) && operations.contains(input)) {
        if (lastInserted == input) return currentResult
        val resultWithoutLast = data.dropLast(1).joinToString(" ")
        return "$resultWithoutLast $input "
    }

    val regex = operations.joinToString(separator = "|") { "\\" + it }.toRegex()

    val size = currentResult.split(regex).filter { it.isNotEmpty() }.size

    if (input == "=") {
        if (data.size < 3) return currentResult
        return calculateResult(data)
    }
    if (input == "C") return ""

    if (size == 2 && operations.contains(input)) {
        val result = calculateResult(data)
        return "$result $input "
    }

    if (operations.contains(input)) return "$currentResult $input "
    return "$currentResult$input"
}

fun calculateResult(result: List<String>): String {
    val n1 = result[0].toDouble()
    val operator = result[1]
    val n2 = result[2].toDouble()


    if (operator == "/") {
        if (n2.toInt() == 0) return "Error"
        return (n1 / n2).toInt().toString()
    }

    return when (operator) {
        "+" -> n1 + n2
        "-" -> n1 - n2
        "*" -> n1 * n2
        else -> throw UnsupportedOperationException("Operator not supported")
    }.toInt().toString()
}