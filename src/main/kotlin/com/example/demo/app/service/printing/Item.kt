package com.example.demo.app.service.printing

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

    fun addRows(vararg row: Row): Section {
        rows.addAll(row)
        return this
    }
}

class Container(val isTable: Boolean = false) {
    fun maxRowCount(): Int {
        var tempRowCount = 0
        sections.forEach { if (it.rows.size > tempRowCount) tempRowCount = it.rows.size }
        return tempRowCount
    }

    val sections = mutableListOf<Section>()

    fun addSections(vararg section: Section): Container {
        sections.addAll(section)
        return this
    }
}

class Ticket {

    var isDrawDivider = true

    val containers = mutableListOf<Container>()

    fun addContainers(vararg header: Container): Ticket {
        containers.addAll(header)
        return this
    }

}