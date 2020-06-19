package com.example.demo.utils

import java.awt.*
import java.awt.print.PageFormat
import javax.swing.ImageIcon

class Receipt(
        private val graphics2D: Graphics2D,
        pageFormat: PageFormat
) {

    var ticket: Ticket = Ticket()

    private val font = Font("Consolas", Font.PLAIN, 10)
    private val fontMetrics: FontMetrics = graphics2D.getFontMetrics(font)

    private var pageWidth = pageFormat.imageableWidth.toFloat()
    private var lineSpacing = 0.1.cm
    private val lineHeight = fontMetrics.height + lineSpacing
    private var yPos = lineHeight

    init {
        graphics2D.font = font
        graphics2D.translate(pageFormat.imageableX.toInt(), pageFormat.imageableY.toInt())
    }

    fun drawTicketHeaders() {
        ticket.headers.forEach { head ->
            //
            // Reset the x position for each header item
            //
            var xPos = 0f
            //
            // Loop through each section
            //
            head.sections.forEach { section ->
                //
                // Copy the xPosition
                //
                val sectionStart = xPos
                //
                // Calculate the end x position of the section
                //
                val sectionEnd = sectionStart + (section.weight * pageWidth)
                //
                // Initialize the row y position
                //
                var rowY = yPos
                //
                // Loop through each row
                //
                section.rows.forEach { row ->
                    //
                    // Calculate the text width
                    //
                    val textWidth = fontMetrics.stringWidth(row.label)
                    //
                    // Check alignment
                    //
                    val rowX = when (section.alignment) {
                        Alignment.RIGHT -> sectionEnd - textWidth
                        Alignment.CENTER -> ((sectionEnd - sectionStart) / 2f + sectionStart) - (textWidth / 2f)
                        else -> sectionStart
                    }
                    //
                    // draw the text
                    //
                    if (row.type == RowType.TEXT) {
                        graphics2D.drawString(row.label, rowX, rowY)
                    } else {
                        val icon = ImageIcon(javaClass.classLoader.getResource("header.png")?.path?.replace("/", "\\\\"))
                        val width = (sectionEnd - sectionStart).toInt()
                        val height = (lineHeight * head.maxRowCount).toInt()
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
            //
            // Prepare y position for next header item
            //
            yPos += lineHeight * head.maxRowCount
        }
    }


    fun addHeader(): Receipt {
        val imagePath = javaClass.classLoader.getResource("header.png")?.path?.replace("/", "\\\\")
        val icon = ImageIcon(imagePath?.substring(2, imagePath.length))
        graphics2D.drawImage(icon.image, (pageWidth / 2 - 15).toInt(), yPos.toInt(), 30, 40, null)
        yPos += (lineSpacing * 2 + 40)
        return this
    }

    fun addDivider(isUnderLine: Boolean = false): Receipt {
        yPos += (lineSpacing * 2)

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

    fun addReceiptItem(label: String, qty: Int, amount: Double): Receipt {
        addText(label)
        addPair("$qty x ${amount.toInt()}", (qty * amount).toInt().toString(), false, 0.8.cm)
        return this
    }

    fun newLine(): Receipt {
        yPos += (fontMetrics.height + lineSpacing)
        return this
    }

}