package com.neuraltechnologies.printeasy

import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Start monitoring for new documents
        monitorNewDocuments()
    }

    private fun monitorNewDocuments() {
        db.collection("files")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                snapshot?.documents?.forEach { doc ->
                    val data = doc.data
                    if (data != null && !data.containsKey("printed")) {
                        lifecycleScope.launch {
                            try {
                                val url = data["url"] as String
                                val fileName = data["name"] as String
                                downloadAndPrintDocument(url, fileName)
                                // Mark as printed
                                doc.reference.update("printed", true)
                            } catch (e: Exception) {
                                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
    }

    private suspend fun downloadAndPrintDocument(url: String, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                // Download the file
                val storageRef = storage.getReferenceFromUrl(url)
                val localFile = File(cacheDir, fileName)
                storageRef.getFile(localFile).await()

                // Print the document
                withContext(Dispatchers.Main) {
                    printDocument(localFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun printDocument(file: File) {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        val jobName = "PrintEasy_${System.currentTimeMillis()}"

        val webView = WebView(this)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter(jobName)
                printManager.print(
                    jobName,
                    printAdapter,
                    PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                )
            }
        }

        // Load the file into WebView
        webView.loadUrl("file://${file.absolutePath}")
    }
} 