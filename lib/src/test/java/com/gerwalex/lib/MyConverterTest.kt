package com.gerwalex.lib

import com.gerwalex.lib.database.MyConverter
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.math.BigDecimal

class MyConverterTest {

    @Test
    fun currencyConverterLongVsBigDecimalReturnsTrue() {
        val a = MyConverter.convertCurrency(1.00f)
        val b = MyConverter.convertCurrency(BigDecimal(0.01))
        val c = MyConverter.convertCurrency(1L)
        assertTrue(a == b)
        assertTrue(a == c)
        assertTrue(b == c)
    }
}