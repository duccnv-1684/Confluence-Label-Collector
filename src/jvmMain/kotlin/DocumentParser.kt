package com.techcombank.business.labelconverter.tcblabelconverter

import LabelData
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File


object DocumentParser {

    fun parseDocument(source: File): List<LabelData> {
        val data = mutableListOf<LabelData>()
        val document: Document = Jsoup.parse(source)
        val tableElements = document.select("table")
        tableElements.forEachIndexed { index, element ->
            val tHead = element.select("thead")
            val tBody = element.select("tbody")

            val headerRow = if (tHead.size == 0) {
                tBody.select("tr").first()
            } else {
                tHead.select("tr").first()
            }
            if (headerRow == null) return@forEachIndexed
            val contentRows = if (tHead.size == 0) {
                tBody.select("tr").drop(1)
            } else {
                tBody.select("tr")
            }
            val titleElements = headerRow.select("th")
            val columnCount = titleElements.size
            val enIndex = titleElements.indexOfFirst { element ->
                element.text().contains("Label EN", true)
            }
            val viIndex = titleElements.indexOfFirst { element ->
                element.text().contains("Label VN", true)
            }
            val idIndex = titleElements.indexOfFirst { element ->
                element.text().contains("Label ID", true)
            }

            if (enIndex == 0 || viIndex == 0) return@forEachIndexed
            contentRows.forEachIndexed { _, element ->
                val tds = element.select("td")
                val offset = columnCount - tds.size
                try {
                    val enContent = tds[enIndex - offset].text()
                    val viContent = tds[viIndex - offset].text()
                    val id = tds[idIndex - offset].text()
                    data.add(LabelData(id, enContent, viContent))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return data
    }

    fun generateContent(data: List<LabelData>, isIOS: Boolean, isVi: Boolean): String {
        return data.map {
            if (isIOS) {
                "\"${it.id.replace("_", ".")}\" = \"${if (isVi) it.vi else it.en}\";"
            } else {
                "\t<string name=\"${it.id.replace(".", "_")}\">${if (isVi) it.vi else it.en}</string>"
            }
        }.joinToString(separator = "\n")
    }

}