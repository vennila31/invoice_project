package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.print.PdfPrint


import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.barteksc.pdfviewer.PDFView
import org.jetbrains.anko.doAsync
import java.io.File
import kotlin.math.roundToInt


class InvoiceActivity : AppCompatActivity() {


    var invoiceNumber: String  = ""
    var dateTime : String  = ""
    val rowNum = -1
    var compName : String  = ""
    var dispatch : String  = ""
    var lrNo : String  = ""
    var vehicleNo : String  = ""
    var delivery : String  = ""
    var sNo : String  = ""
    var isGst : String = ""
    var sellingPrice : Int = 0
    var description : String  = ""
    var hsnCode : String  = ""
    var charges :  Int = 0
    var qty :  Int = 0
    var subTotal : Int = 0
    var total : Int = 0
    var gstAmount : Double = 0.0

    lateinit var webView: WebView
    private lateinit var wvContent: PDFView
    private lateinit var pdfFileObjWithPath: File
    private lateinit var progressBar: ProgressBar
    private lateinit var textView : AppCompatTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_acitivity)

        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progress_bar)
        wvContent = findViewById<PDFView>(R.id.invoiceView)
        textView = findViewById(R.id.textView)

        setPermission()


    }

    private fun setPermission() {

        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("permission", "Permission to record denied")
            makeRequest()
        } else {
            getIntentData()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            100
        )
    }

    private fun getIntentData() {

        progressBar.visibility = View.VISIBLE

        invoiceNumber = intent.getStringExtra("InvoiceNumber").toString()
        dateTime = intent.getStringExtra("invoiceDate").toString()
        compName = intent.getStringExtra("compName").toString()
        dispatch = intent.getStringExtra("dispatch").toString()
        lrNo = intent.getStringExtra("LRNo").toString()
        vehicleNo = intent.getStringExtra("vehicleNumber").toString()
        delivery = intent.getStringExtra("delivery").toString()
        sNo = intent.getStringExtra("sNo").toString()
        isGst = intent.getStringExtra("gst").toString()
        sellingPrice = intent.getIntExtra("sellingPrice",0)
        description = intent.getStringExtra("description").toString()
        hsnCode = intent.getStringExtra("hsnCode").toString()
        charges = intent.getIntExtra("charges",0)
        qty = intent.getIntExtra("qty",0)



        subTotal = sellingPrice * qty



        pdfFileObjWithPath =
            File(Environment.getExternalStorageDirectory().absolutePath + "/.TASK/" + "invoice" + ".pdf")

        generatePDF()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun generatePDF() {


        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.builtInZoomControls = true

        webView.loadUrl("file:///android_asset/www/invoice.html")

        webView.webViewClient = object : WebViewClient(){

            @SuppressLint("NewApi")
            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun onPageFinished(view: WebView?, url: String?) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
                {

                    total = if(isGst == "true"){
                        val gst  = (0.18)
                        gstAmount = subTotal * gst
                        (((subTotal + gstAmount) + charges).roundToInt())
                    } else {
                        subTotal + charges
                    }

                    view?.evaluateJavascript("getDetails('$invoiceNumber','$dateTime','$compName','$dispatch','$lrNo','$vehicleNo','$delivery')",null)
//                    view?.evaluateJavascript("loadProductTable('$rowNum','$sNo','$description','$hsnCode','$sellingPrice','$qty,'$subTotal')",null)
                      view?.evaluateJavascript("loadProductTable('$rowNum','$sNo','$description','$hsnCode','$sellingPrice','$qty','$subTotal','$charges','$gstAmount','$total')",null)


                }

                createWebPrintJob(webView)

            }
        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createWebPrintJob(webView: WebView){

        val printManager = this
            .getSystemService(Context.PRINT_SERVICE) as PrintManager

        val printAdapter = webView.createPrintDocumentAdapter("MyDocument")

        val jobName = getString(R.string.app_name) +
                " Print Test"

        printManager.print(
            jobName, printAdapter,
            PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
        )
        progressBar.visibility = View.GONE
        textView.visibility = View.VISIBLE


        /*try{

        val jobName = getString(R.string.app_name) + " Document"
        val attributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

            val path = File(Environment.getExternalStorageDirectory().absolutePath + "/.MYAPPLICATION/")
            pdfFileObjWithPath =
                File(Environment.getExternalStorageDirectory().absolutePath + "/.MYAPPLICATION/" + "invoice" + ".pdf")

            val isNewFileCreated: Boolean = pdfFileObjWithPath.exists()

            if (isNewFileCreated) {

                pdfFileObjWithPath.delete()
            }


            Log.d("pdfName", "in createWebPrintJob " + "invoice")

            val pdfPrint = PdfPrint(attributes)
            pdfPrint.print(
                webView.createPrintDocumentAdapter(jobName),
                path,
                "invoice" + ".pdf"
            )
            openPDFViewer()
        } catch (e: Exception) {
            Log.e("error", "trying to find whazt is happening...." + e.printStackTrace())
        }*/
    }

    override fun onBackPressed() {

        finish()

    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun openPDFViewer() {
        try {

            doAsync {

                Thread.sleep(1500)
                Log.d("pdfName", "in openPdfViewr in exception " + "invoice")
                pdfFileObjWithPath =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/.TASK/" + "invoice" + ".pdf")
                val isNewFileCreated: Boolean = pdfFileObjWithPath.exists()

                if (isNewFileCreated) {


                    wvContent.fromFile(pdfFileObjWithPath)
                        .defaultPage(0)
                        .enableSwipe(true)
                        // .scrollHandle(DefaultScrollHandle(this))
                        .enableAnnotationRendering(true)
                        .spacing(5)
                        .load()

                    progressBar.visibility = View.GONE
                } else
                {
                    createWebPrintJob(webView)
                }

            }


            /*  runOnUiThread {
                  progress?.dismiss()

              }*/
        } catch (e: Exception) {
            openPDFViewer()
            e.printStackTrace()
        }

    }



}
