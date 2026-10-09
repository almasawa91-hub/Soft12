package com.aalmoghalis.muhasibsoft.data.repository

import com.aalmoghalis.muhasibsoft.data.local.VoucherDao
import com.aalmoghalis.muhasibsoft.data.model.Voucher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepository @Inject constructor(
    private val voucherDao: VoucherDao
) {

    fun getAllVouchers(): Flow<List<Voucher>> = voucherDao.getAllVouchers()

    fun getVouchersByType(type: String): Flow<List<Voucher>> =
        voucherDao.getVouchersByType(type)

    suspend fun getVoucherById(id: Long): Voucher? = voucherDao.getVoucherById(id)

    suspend fun insertVoucher(voucher: Voucher): Long = voucherDao.insertVoucher(voucher)

    suspend fun updateVoucher(voucher: Voucher) = voucherDao.updateVoucher(voucher)

    suspend fun deleteVoucher(id: Long) = voucherDao.deleteVoucher(id)
}