package com.example.olx.Util

import java.text.*
import java.util.*

class GetMask {

    companion object {

        fun getValor(valor: Double): String {
            val nf: NumberFormat = DecimalFormat(
                "#,##0.00",
                DecimalFormatSymbols(Locale("pt", "BR"))
            )
            return nf.format(valor)
        }

        fun getDate(time: Long): String {

            val dateFormat = DateFormat.getDateTimeInstance()
            val netDate = Date(time)
            dateFormat.format(netDate)

            val dia = SimpleDateFormat("dd").format(netDate)
            var mes = SimpleDateFormat("MM").format(netDate)

            mes = when (mes) {
                "01" -> "janeiro"
                "02" -> "fevereiro"
                "03" -> "março"
                "04" -> "abril"
                "05" -> "maio"
                "06" -> "junho"
                "07" -> "julho"
                "08" -> "agosto"
                "09" -> "setembro"
                "10" -> "outubro"
                "11" -> "novembro"
                "12" -> "novembro"
                else -> ""
            }

            val dataStr = StringBuilder()

            dataStr
                .append(dia)
                .append(" ")
                .append(mes)

            return dataStr.toString()

        }

    }

}