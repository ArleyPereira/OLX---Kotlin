package com.example.olx.util

import java.text.*
import java.util.*

class GetMask {

    companion object {

        const val DIA_MES: Int = 1
        const val DIA_MES_HORA: Int = 2

        fun getValor(valor: Double): String {
            val nf: NumberFormat = DecimalFormat(
                "#,##0.00",
                DecimalFormatSymbols(Locale("pt", "BR"))
            )
            return nf.format(valor)
        }

        fun getDate(time: Long, tipo: Int): String {

            // 1 -> dia/mes (26 outubro)
            // 2 -> dia/mes hora (26/10 às 07:45)

            val dateFormat = DateFormat.getDateTimeInstance()
            val netDate = Date(time)
            dateFormat.format(netDate)

            val dia = SimpleDateFormat("dd", Locale.ROOT).format(netDate)
            var mes = SimpleDateFormat("MM", Locale.ROOT).format(netDate)

            val hora = SimpleDateFormat("HH", Locale.ROOT).format(netDate)
            val minuto = SimpleDateFormat("mm", Locale.ROOT).format(netDate)

            if (tipo == DIA_MES) {
                mes = when (mes) {
                    "01" -> "de janeiro"
                    "02" -> "de fevereiro"
                    "03" -> "de março"
                    "04" -> "de abril"
                    "05" -> "de maio"
                    "06" -> "de junho"
                    "07" -> "de julho"
                    "08" -> "de agosto"
                    "09" -> "de setembro"
                    "10" -> "de outubro"
                    "11" -> "de novembro"
                    "12" -> "de dezembro"
                    else -> ""
                }
            }

            val diaMes = StringBuilder()
                .append(dia)
                .append(" ")
                .append(mes)

            val diaMesHora = StringBuilder()
                .append(dia)
                .append("/")
                .append(mes)
                .append(" às ")
                .append(hora)
                .append(":")
                .append(minuto)

            return when (tipo) {
                DIA_MES -> diaMes.toString()
                DIA_MES_HORA -> diaMesHora.toString()
                else -> {
                    ""
                }
            }
        }

    }

}