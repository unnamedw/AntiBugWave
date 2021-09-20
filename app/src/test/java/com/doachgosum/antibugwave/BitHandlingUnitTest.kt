package com.doachgosum.antibugwave

import org.junit.Test

class BitHandlingUnitTest {

    @Test
    fun shiftingTest() {
        val bit = -10
        println(Integer.toBinaryString(bit))
        println(Integer.toBinaryString(bit.shr(1)))
    }
}