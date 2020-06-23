package com.example.demo.utils

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterJob
import java.util.*
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttributeSet
import kotlin.math.roundToInt

class PrinterService : Printable {

    val printers: List<String>
        get() {
            val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
            val pRas: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
            val printServices = PrintServiceLookup.lookupPrintServices(flavor, pRas)
            val printerList: MutableList<String> = ArrayList()
            for (printerService in printServices) {
                printerList.add(printerService.name)
            }
            return printerList
        }

    private var items: List<Item> = emptyList()
    private var clientName = ""
    private lateinit var orderTime: DateTime
    private var ticketNumber = ""

    override fun print(graphics: Graphics, pageFormat: PageFormat, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE

        var total = 0.0

        val headerMainLeft = Section(0.45f).addRows(Row("MINISTRY OF PUBLIC HEALTH"), Row("CENTER REGIONAL DELEGATION"), Row("P.O. BOX: 1113 EFOULAN - YAOUNDE"), Row("TEL: +237 22 31 26 98"))
        val headerMainCenter = Section(0.1f).addRows(Row("", RowType.IMAGE))
        val headerMainRight = Section(0.45f).addRows(Row("MINISTRY OF PUBLIC HEALTH"), Row("CENTER REGIONAL DELEGATION"), Row("P.O. BOX: 1113 EFOULAN - YAOUNDE"), Row("TEL: +237 22 31 26 98"))

        val dateFormatter = DateTimeFormat.forPattern("dd/MM/yy - HH:mm:ss")
        val sub = Section(1f).addRows(Row("TICKET NO: $ticketNumber    OF    ${orderTime.toString(dateFormatter)}"))

        val info = Section(1f, Alignment.LEFT).addRows(
                Row("CLIENT: $clientName")
        )

        val bodyOne = Section(0.70f, Alignment.LEFT).addRows(Row("ITEM"))
        val bodyTwo = Section(0.06f, Alignment.CENTER).addRows(Row("QTY"))
        val bodyThree = Section(0.12f, Alignment.RIGHT).addRows(Row("PRICE"))
        val bodyFour = Section(0.12f, Alignment.RIGHT).addRows(Row("AMOUNT"))
        items.forEach {
            total += it.price * it.quantity
            bodyOne.addRows(Row(it.label))
            bodyTwo.addRows(Row(it.quantity.toString()))
            bodyThree.addRows(Row(it.price.roundToInt().toString()))
            bodyFour.addRows(Row((it.price * it.quantity).roundToInt().toString()))
        }

        bodyOne.addRows(Row("TOTAL"))
        bodyTwo.addRows(Row(""))
        bodyThree.addRows(Row(""))
        bodyFour.addRows(Row(total.roundToInt().toString()))

        val header = Container().addSections(headerMainLeft, headerMainCenter, headerMainRight)
        val subHead = Container().addSections(sub)
        val subInfo = Container().addSections(info)
        val body = Container(true).addSections(bodyOne, bodyTwo, bodyThree, bodyFour)

        with(Receipt(graphics as Graphics2D, pageFormat)) {
            this.ticket = Ticket().addContainers(header, subHead, subInfo, body)
            drawTicket()
        }
        return Printable.PAGE_EXISTS
    }

    fun printReceipt(items: List<Item>, clientName: String, ticketNumber: String, orderTime: DateTime) {
        this.items = items
        this.clientName = clientName
        this.ticketNumber = ticketNumber
        this.orderTime = orderTime

        //val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        //val pRas: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        //val printService = PrintServiceLookup.lookupPrintServices(flavor, pRas)
        //val service = findPrintService("RICOH Aficio MP C4000 (Copy 1)", printService)

        val job: PrinterJob = PrinterJob.getPrinterJob()

        //job.printService = service

        job.setPrintable(this, getPageFormat(job, items.size))

        job.print()
    }

    fun printBytes(printerName: String, bytes: ByteArray?) {
        val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        val pRas: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        val printService = PrintServiceLookup.lookupPrintServices(flavor, pRas)
        val service = findPrintService(printerName, printService)
        val job = service?.createPrintJob()

        try {
            val doc: Doc = SimpleDoc(bytes, flavor, null)
            job?.print(doc, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPageFormat(job: PrinterJob, bodyHeight: Int): PageFormat {
        val pf = job.defaultPage()

        val height = pf.height
        val width = pf.width
        val margin = 1.5.cm.toDouble()

        val paper = with(pf.paper) {
            setSize(width, height)
            setImageableArea(margin, margin, width - (margin * 2), height - (margin * 2))
            this
        }

        pf.paper = paper
        pf.orientation = PageFormat.PORTRAIT
        return pf
    }

    private fun findPrintService(printerName: String, services: Array<PrintService>): PrintService? {
        for (service in services) if (service.name.equals(printerName, ignoreCase = true)) return service
        return null
    }
}