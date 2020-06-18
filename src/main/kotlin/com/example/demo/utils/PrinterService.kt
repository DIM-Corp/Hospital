package com.example.demo.utils

import com.example.demo.data.model.OrderItemModel
import com.example.demo.data.model.PatientEntryModel
import javafx.collections.ObservableList
import tornadofx.*
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterJob
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttributeSet

class PrinterService : Printable {

    var orderItems: ObservableList<OrderItemModel> by singleAssign()
    var patientEntryModel: PatientEntryModel by singleAssign()

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

    override fun print(graphics: Graphics, pageFormat: PageFormat, pageIndex: Int): Int {
        if (pageIndex > 0) return Printable.NO_SUCH_PAGE

        with(Receipt(graphics as Graphics2D, pageFormat)) {
            addHeader()

            addText("EFOULAN DISTRICT HOSPITAL", Receipt.Alignment.CENTER)
            addUnderLine()
            addText("HOPITAL DE DISTRICT D'EFOULAN", Receipt.Alignment.CENTER)
            addUnderLine()

            newLine()

            val now = LocalDateTime.now()
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.FRANCE)
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.FRANCE)

            addPair("Tel: 673477745", "Receipt no: 50")
            addPair(now.format(dateFormatter), now.format(timeFormatter))
            addPair("Cashier: Forntoh", "Client: ${patientEntryModel.name.value.simplify()}")

            //TODO: Cashier name
            //TODO: Receipt number
            //TODO: Order Total
            //TODO: Order Balance

            newLine()

            addPair("ITEM", "PRICE")

            addDivider()

            var total = 0.0
            orderItems.forEach {
                addReceiptItem(it.label.value, it.qtyTemp.value.toInt(), it.price.value.toDouble())
                total += it.amtCalc.value.toDouble()
            }

            addDivider()

            addPair("Total", total.toInt().toString())
            addDivider()
            addPair("Cash", "10000")
            addPair("Balance", "7500")
            addDivider()
        }
        return Printable.PAGE_EXISTS
    }

    fun printReceipt() {
        //val flavor: DocFlavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        //val pRas: PrintRequestAttributeSet = HashPrintRequestAttributeSet()
        //val printService = PrintServiceLookup.lookupPrintServices(flavor, pRas)
        //val service = findPrintService("RICOH Aficio MP C4000 (Copy 1)", printService)

        val job: PrinterJob = PrinterJob.getPrinterJob()

        //job.printService = service

        job.setPrintable(this, getPageFormat(job))

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

    private fun getPageFormat(job: PrinterJob): PageFormat {
        val pf = job.defaultPage()

        val headerHeight = 5.0
        val bodyHeight = 5.0
        val footerHeight = 5.0

        val height = (headerHeight + bodyHeight + footerHeight).cm

        with(pf) {
            paper.setSize(7.6.cm.toDouble(), height.toDouble())
            paper.setImageableArea(0.0, 10.0, 7.6.cm.toDouble(), (height - 1.cm).toDouble())
            orientation = PageFormat.PORTRAIT
        }
        return pf
    }

    private fun findPrintService(printerName: String, services: Array<PrintService>): PrintService? {
        for (service in services) if (service.name.equals(printerName, ignoreCase = true)) return service
        return null
    }
}