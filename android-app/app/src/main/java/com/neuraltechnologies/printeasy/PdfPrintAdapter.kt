package com.neuraltechnologies.printeasy

import android.content.Context
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PdfPrintAdapter(private val context: Context, private val filePath: String) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        val info = PrintDocumentInfo.Builder(File(filePath).name)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()
        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out android.print.PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        try {
            val input = FileInputStream(filePath)
            val output = FileOutputStream(destination?.fileDescriptor)
            input.copyTo(output)
            input.close()
            output.close()
            callback?.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        }
    }
}