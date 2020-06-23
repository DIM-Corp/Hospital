package com.example.demo.utils

import java.awt.*
import java.awt.print.PageFormat
import javax.swing.ImageIcon

class Receipt(
        private val graphics2D: Graphics2D,
        pageFormat: PageFormat
) {

    lateinit var ticket: Ticket

    private val font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
    private val fontMetrics: FontMetrics = graphics2D.getFontMetrics(font)

    private var pageWidth = pageFormat.imageableWidth.toFloat()
    private var lineSpacing = 0.1.cm
    private val lineHeight = fontMetrics.height + lineSpacing
    private var yPos = lineHeight

    init {
        graphics2D.font = font
        graphics2D.translate(pageFormat.imageableX.toInt(), pageFormat.imageableY.toInt())
    }

    fun drawTicket() {
        ticket.containers.forEach { container ->
            //
            // Reset the x position for each header item
            //
            var xPos = 0f
            //
            // Loop through each section
            //
            val tableHeight = (lineHeight * container.maxRowCount() + lineSpacing).toInt()
            container.sections.forEach { section ->
                //
                // Copy the xPosition
                //
                val sectionStart = xPos
                //
                // Calculate the end x position of the section
                //
                val sectionEnd = sectionStart + (section.weight * pageWidth)
                //
                // Calculate the section width
                //
                val sectionWidth = sectionEnd - sectionStart

                if (container.isTable) {
                    graphics2D.drawRect(sectionEnd.toInt(), (yPos - fontMetrics.height).toInt(), sectionEnd.toInt(), tableHeight)
                }
                //
                // Initialize the row y position
                //
                var rowY = yPos
                //
                // Loop through each row
                //
                section.rows.forEachIndexed { index, row ->
                    graphics2D.font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
                    if (index == 0 && container.isTable) {
                        graphics2D.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
                        graphics2D.drawLine(sectionStart.toInt(), (lineSpacing * 2 + rowY).toInt(), sectionEnd.toInt(), (lineSpacing * 2 + rowY).toInt())
                    } else if (index == section.rows.size - 1 && container.isTable) {
                        graphics2D.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
                        graphics2D.drawLine(sectionStart.toInt(), (rowY - fontMetrics.height).toInt(), sectionEnd.toInt(), (rowY - fontMetrics.height).toInt())
                    }
                    //
                    // Calculate the text width
                    //
                    var textWidth = fontMetrics.stringWidth(row.label)
                    //
                    // Check alignment
                    //
                    val rowX = when (section.alignment) {
                        Alignment.RIGHT -> if (container.isTable) sectionEnd - textWidth - 6 else sectionEnd - textWidth
                        Alignment.CENTER -> (sectionWidth / 2f + sectionStart) - (textWidth / 2f)
                        else -> if (container.isTable) sectionStart + 6 else sectionStart
                    }
                    //
                    // draw the text
                    //
                    if (row.type == RowType.TEXT) {
                        var text = row.label
                        while (textWidth >= sectionWidth - 2.cm && sectionWidth > 3.cm) {
                            text = text.dropLast(4)
                            textWidth = fontMetrics.stringWidth(text)
                        }
                        graphics2D.drawString(text, rowX, rowY)
                    } else {
                        val icon = ImageIcon(javaClass.classLoader.getResourceAsStream("header.png")?.readBytes())
                        val width = sectionWidth.toInt()
                        val height = (lineHeight * container.maxRowCount()).toInt()
                        val imgX = (sectionEnd - width).toInt()
                        graphics2D.drawImage(icon.image, imgX, (yPos - (lineHeight / 2)).toInt(), width, height, null)
                    }
                    //
                    // Go to the next row
                    //
                    rowY += lineHeight
                }
                //
                // Move to next column
                //
                xPos += section.weight * pageWidth
            }

            if (container.isTable) {
                graphics2D.drawRect(1, (yPos - fontMetrics.height).toInt(), (pageWidth - 1).toInt(), tableHeight)
            }

            //
            // Prepare y position for next header item
            //
            yPos += lineHeight * container.maxRowCount()
            newLine()
        }
    }

    fun addDivider(isUnderLine: Boolean = false): Receipt {
        //yPos += (lineSpacing * 2)

        val dashed: Stroke = BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, floatArrayOf(1f, 2f), 0f)
        val original = graphics2D.stroke

        graphics2D.stroke = dashed
        graphics2D.drawLine(
                if (isUnderLine) lineSpacing.toInt() * 16 else 0,
                yPos.toInt(),
                if (isUnderLine) (pageWidth - (lineSpacing.toInt() * 16)).toInt() else pageWidth.toInt(),
                yPos.toInt()
        )
        graphics2D.stroke = original

        return this
    }

    fun addUnderLine() = addDivider(true)

    private fun addText(mText: String, goToNextLine: Boolean = true, alignment: Alignment = Alignment.LEFT, padding: Float = 0f): Receipt {
        var text = mText
        var textWidth = fontMetrics.stringWidth(text)

        while (textWidth >= pageWidth - 2.cm) {
            text = text.dropLast(2)
            textWidth = fontMetrics.stringWidth(text)
        }

        val xPos = when (alignment) {
            Alignment.LEFT -> padding
            Alignment.RIGHT -> pageWidth - textWidth
            Alignment.CENTER -> (pageWidth / 2f) - (textWidth / 2f)
        }

        if (goToNextLine) yPos += (fontMetrics.height + lineSpacing)
        graphics2D.drawString(text, xPos, yPos)

        return this
    }

    fun addText(text: String, alignment: Alignment = Alignment.LEFT) = addText(text, true, alignment)

    fun addPair(key: String, value: String, goToNextLine: Boolean = false, padding: Float = 0f): Receipt {
        addText(key, true, Alignment.LEFT, padding)
        addText(value, goToNextLine, Alignment.RIGHT)
        return this
    }

    private fun addReceiptItem(label: String, qty: Int, amount: Double): Receipt {
        addText(label)
        addPair("$qty x ${amount.toInt()}", (qty * amount).toInt().toString(), false, 0.8.cm)

        if (pageWidth > 7.6.cm) {
            addPair("$qty x ${amount.toInt()}", (qty * amount).toInt().toString(), false, 0.8.cm)
        }

        return this
    }

    fun newLine(): Receipt {
        yPos += lineHeight
        return this
    }

}