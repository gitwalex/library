package com.gerwalex.library.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Stack

@Composable
fun CalculatorScreen() {
    var input by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = input.ifEmpty { "0" },
            fontSize = 48.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("C", "0", "=", "+")
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { label ->
                        CalculatorButton(
                            label = label,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            when (label) {
                                "C" -> input = ""
                                "=" -> input = evaluateExpression(input)
                                else -> input += label
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (label in listOf(
                    "+",
                    "-",
                    "*",
                    "/",
                    "="
                )
            ) Color(0xFF1976D2) else Color(0xFFE0E0E0),
            contentColor = if (label in listOf(
                    "+",
                    "-",
                    "*",
                    "/",
                    "="
                )
            ) Color.White else Color.Black
        )
    ) {
        Text(text = label, fontSize = 24.sp)
    }
}

fun evaluateExpression(expression: String): String {
    return try {
        val result = simpleEvaluate(expression)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun simpleEvaluate(expr: String): Double {
    val numbers = Stack<Double>()
    val operations = Stack<Char>()
    var i = 0
    while (i < expr.length) {
        when (val ch = expr[i]) {
            in '0'..'9', '.' -> {
                val sb = StringBuilder()
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                numbers.push(sb.toString().toDouble())
                continue
            }

            '+', '-', '*', '/' -> {
                while (operations.isNotEmpty() && hasPrecedence(ch, operations.peek())) {
                    val b = numbers.pop()
                    val a = numbers.pop()
                    val op = operations.pop()
                    numbers.push(applyOp(op, a, b))
                }
                operations.push(ch)
            }
        }
        i++
    }

    while (operations.isNotEmpty()) {
        val b = numbers.pop()
        val a = numbers.pop()
        val op = operations.pop()
        numbers.push(applyOp(op, a, b))
    }

    return numbers.pop()
}

fun hasPrecedence(op1: Char, op2: Char): Boolean {
    return !((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
}

fun applyOp(op: Char, a: Double, b: Double): Double {
    return when (op) {
        '+' -> a + b
        '-' -> a - b
        '*' -> a * b
        '/' -> a / b
        else -> throw UnsupportedOperationException("Unknown operator: $op")
    }
}