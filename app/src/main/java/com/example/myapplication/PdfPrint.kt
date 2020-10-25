package android.print

import android.os.Build
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

class PdfPrint(printAttributes: PrintAttributes) {

    private val TAG = PdfPrint::class.java.simpleName
    var printAttributes: PrintAttributes = printAttributes


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun print(printAdapter: PrintDocumentAdapter, path: File, fileName: String): String {

        printAdapter.onLayout(
            null,
            printAttributes,
            null,
            @RequiresApi(Build.VERSION_CODES.KITKAT)
            object : PrintDocumentAdapter.LayoutResultCallback() {
                override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                    printAdapter.onWrite(
                        arrayOf(PageRange.ALL_PAGES),
                        getOutputFile(path, fileName),
                        CancellationSignal(),
                        object : PrintDocumentAdapter.WriteResultCallback() {

                            override fun onWriteFinished(pages: Array<PageRange>) {
                                super.onWriteFinished(pages)
                                Log.d("tag","onWriteFinished working")

                            }
                        })
                    Log.d("tag","onLayoutFinished working over")


                }

            },
            null
        )
        printAdapter.onFinish()


        return "success"
    }




    private fun getOutputFile(path: File, fileName: String): ParcelFileDescriptor? {
        if (!path.exists()) {
            path.mkdirs()
        }
        Log.e("file",fileName)
        val f = File(path, fileName)
        try {

            f.createNewFile()

            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_WRITE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e)
        }

        return null
    }


}
