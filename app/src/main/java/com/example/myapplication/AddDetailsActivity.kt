package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class AddDetailsActivity : AppCompatActivity() {

    lateinit var addInvoice : AppCompatButton
    lateinit var invoiceNo : AppCompatTextView
    lateinit var dateTime : AppCompatTextView
    lateinit var compName : AppCompatEditText
    lateinit var dispatch : AppCompatEditText
    lateinit var lrNo : AppCompatEditText
    lateinit var vehicleNo : AppCompatEditText
    lateinit var delivery : AppCompatEditText
    lateinit var sNo : AppCompatTextView
    lateinit var isGst : AppCompatCheckBox
    lateinit var sellingPrice : AppCompatEditText
    lateinit var description : AppCompatEditText
    lateinit var hsnCode : AppCompatEditText
    lateinit var charges : AppCompatEditText
    lateinit var qty : AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_details)

        addInvoice = findViewById(R.id.addInvoice)
        invoiceNo = findViewById(R.id.invoice_number)
        dateTime = findViewById(R.id.receipt_date)
        compName = findViewById(R.id.companyName)
        dispatch = findViewById(R.id.dispatch)
        lrNo = findViewById(R.id.lrNo)
        vehicleNo = findViewById(R.id.vehicleNumber)
        delivery = findViewById(R.id.termsDelivery)
        sNo = findViewById(R.id.sNumberVal)
        isGst = findViewById(R.id.inclusiveGst)
        sellingPrice = findViewById(R.id.sellingPrice)
        description = findViewById(R.id.productDes)
        hsnCode = findViewById(R.id.hsnCode)
        charges = findViewById(R.id.addCharges)
        qty = findViewById(R.id.qty)

        getData()


            addInvoice.setOnClickListener {



                    val i = Intent( this , InvoiceActivity::class.java).putExtra("InvoiceNumber",invoiceNo.text.toString())
                        .putExtra("invoiceDate",dateTime.text)
                        .putExtra("compName",compName.text.toString())
                        .putExtra("dispatch",dispatch.text.toString())
                        .putExtra("LRNo",lrNo.text.toString())
                        .putExtra("vehicleNumber",vehicleNo.text.toString())
                        .putExtra("delivery",delivery.text.toString())
                        .putExtra("sNo",sNo.text.toString())
                        .putExtra("gst",isGst.isChecked.toString())
                        .putExtra("sellingPrice",Integer.parseInt(sellingPrice.text.toString()))
                        .putExtra("description",description.text.toString())
                        .putExtra("hsnCode",hsnCode.text.toString())
                        .putExtra("charges",Integer.parseInt(charges.text.toString()))
                        .putExtra("qty",Integer.parseInt(qty.text.toString()))



                    startActivity(i)


            }




    }

    private fun getData() {

        val currentTime: Date = Calendar.getInstance().time

        invoiceNo.text = NotificationID.iD.toString()
        sNo.text = invoiceNo.text.toString()
        dateTime.text = currentTime.toString()


        Log.e("daterime",currentTime.toString())

    }

    object NotificationID {
        private val c: AtomicInteger = AtomicInteger(0)
        val iD: Int
            get() = c.incrementAndGet()
    }


}