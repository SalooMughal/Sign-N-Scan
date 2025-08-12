package com.pixelz360.docsign.imagetopdf.creator.pdf_file_view_direct_device_side

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.pixelz360.docsign.imagetopdf.creator.R

class PdfTextAdapter(private val textDataList: MutableList<PdfTextData>) :
    RecyclerView.Adapter<PdfTextAdapter.TextViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf_text, parent, false)
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val textData = textDataList[position]
        holder.editText.setText(textData.text)

        holder.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textDataList[position] = textData.copy(text = s.toString())
            }
        })
    }

    override fun getItemCount(): Int = textDataList.size

    fun getUpdatedTextData(): List<PdfTextData> = textDataList

    class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val editText: EditText = view.findViewById(R.id.editText)
    }
}
