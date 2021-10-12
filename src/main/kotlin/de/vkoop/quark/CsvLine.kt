package de.vkoop.quark

import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class CsvLine(
    val date: ZonedDateTime,
    val type: String,
    val amoumt: BigDecimal,
    val currency: String,
    val amountFiat: BigDecimal,
    val fiatCurrency: String,
    val transactionId: String,
    val withdrawalAddress: String,
    val reference: String,
    val relatedReferenceId: String
) {

    fun toCsv(): String {
        val amountString = this.amoumt.toPlainString()//.replace(".", ",")
        val amountFiatString = amountFiat.toPlainString()//.replace(".", ",")

        return """"$date","$type","$amountString","$currency","$amountFiatString","$fiatCurrency","$withdrawalAddress","$transactionId","$reference","$relatedReferenceId""""
    }

    companion object {
        fun from(it: String): CsvLine {
            val l = it.split(",").map { it.substring(1, it.length - 1) }
            val date = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(l[0])

            return CsvLine(
                ZonedDateTime.from(date),
                l[1],
                l[2].toBigDecimal(),
                l[3],
                l[4].toBigDecimal(),
                l[5],
                l[6],
                l[7],
                l[8],
                l[9]
            )
        }
    }
}
