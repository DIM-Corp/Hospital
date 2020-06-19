package com.example.demo.utils

enum class Alignment {
    LEFT, RIGHT, CENTER
}

enum class RowType {
    IMAGE, TEXT
}

data class Item(val label: String, val quantity: Int, val price: Double)


/*
 * Head
 */
data class Row(val label: String, val type: RowType = RowType.TEXT)

data class Section(val weight: Float = 0f, val alignment: Alignment = Alignment.CENTER) {

    val rows = mutableListOf<Row>()

    fun addRow(row: Row) = rows.add(row)
}

class Header {

    val maxRowCount: Int
        get() {
            var tempRowCount = 0
            sections.forEach { if (it.rows.size > tempRowCount) tempRowCount = it.rows.size }
            return tempRowCount
        }

    val sections = mutableListOf<Section>()

    fun addSection(section: Section) = sections.add(section)
}

data class Ticket(val body: List<Item> = emptyList()) {

    val headers = mutableListOf<Header>()

    fun addHeader(header: Header) = headers.add(header)

}