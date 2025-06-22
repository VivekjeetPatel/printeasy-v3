package com.neuraltechnologies.printeasy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import android.util.Log
import android.widget.TextView
import android.net.Uri
import androidx.core.content.FileProvider

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if logged in
        val sharedPref = getSharedPreferences("PrintEasyPrefs", Context.MODE_PRIVATE)
        val shopName = sharedPref.getString("shopName", null)
        if (shopName == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Update UI
        val listeningTextView: TextView = findViewById(R.id.listeningTextView)
        listeningTextView.text = "Listening for print jobs for: $shopName"
        Log.d("PrintEasy", "Logged in with shopName: $shopName. Starting to monitor documents.")
        Toast.makeText(this, "Listening for jobs for: $shopName", Toast.LENGTH_LONG).show()

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Start monitoring for new documents for the logged-in shop
        monitorNewDocuments(shopName)
    }

    private fun monitorNewDocuments(shopName: String) {
        db.collection("files")
            .whereEqualTo("shopName", shopName)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PrintEasy", "Firestore listener error:", error)
                    Toast.makeText(this, "Listener Error: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.d("PrintEasy", "No new pending documents found for $shopName.")
                    return@addSnapshotListener
                }

                Log.d("PrintEasy", "Found ${snapshot.size()} new document(s) for $shopName.")
                Toast.makeText(this, "Found ${snapshot.size()} new document(s).", Toast.LENGTH_SHORT).show()

                snapshot.documents.forEach { doc ->
                    lifecycleScope.launch {
                        try {
                            val url = doc.getString("url") ?: return@launch
                            val fileName = doc.getString("name") ?: "document"
                            downloadAndPrintDocument(url, fileName)
                            // Mark as printed
                            doc.reference.update(
                                "status", "printed",
                                "printedAt", FieldValue.serverTimestamp()
                            )
                        } catch (e: Exception) {
                            Log.e("PrintEasy", "Error processing document: ${doc.id}", e)
                            Toast.makeText(this@MainActivity, "Error processing document: ${e.message}", Toast.LENGTH_LONG).show()
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
    val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
    val printAdapter = PdfPrintAdapter(this, file.absolutePath)
    printManager.print("PrintEasy Document", printAdapter, null)
}
}