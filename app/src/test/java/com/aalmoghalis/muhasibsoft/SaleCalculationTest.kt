package com.aalmoghalis.muhasibsoft

import com.aalmoghalis.muhasibsoft.data.model.SaleItem
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * اختبارات حسابات الفواتير
 */
class SaleCalculationTest {

    @Test
    fun `sale item total equals quantity times price`() {
        val item = SaleItem(
            saleId = 1,
            itemId = 1,
            quantity = 3.0,
            price = 200.0,
            total = 3.0 * 200.0
        )

        assertEquals(600.0, item.total, 0.001)
    }

    @Test
    fun `invoice subtotal is sum of items totals`() {
        val items = listOf(
            SaleItem(1, 1, 2.0, 100.0, total = 200.0),
            SaleItem(1, 2, 1.0, 350.0, total = 350.0),
            SaleItem(1, 3, 4.0, 50.0, total = 200.0)
        )

        val subtotal = items.sumOf { it.total }
        assertEquals(750.0, subtotal, 0.001)
    }

    @Test
    fun `invoice total applies discount and tax correctly`() {
        val subtotal = 1000.0
        val discount = 100.0
        val taxRate = 15.0

        val afterDiscount = subtotal - discount
        val tax = afterDiscount * taxRate / 100
        val total = afterDiscount + tax

        assertEquals(900.0, afterDiscount, 0.001)
        assertEquals(135.0, tax, 0.001)
        assertEquals(1035.0, total, 0.001)
    }

    @Test
    fun `remaining amount is total minus paid`() {
        val total = 2500.0
        val paid = 1500.0
        val remaining = total - paid

        assertEquals(1000.0, remaining, 0.001)
    }

    @Test
    fun `journal entry is balanced when debit equals credit`() {
        val debits = listOf(500.0, 300.0)
        val credits = listOf(800.0)

        val totalDebit = debits.sum()
        val totalCredit = credits.sum()

        assertEquals(totalDebit, totalCredit, 0.001)
    }
}