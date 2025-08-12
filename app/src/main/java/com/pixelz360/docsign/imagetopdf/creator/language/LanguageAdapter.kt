package com.findphone.whistleclapfinder.languageClasess

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pixelz360.docsign.imagetopdf.creator.HomeActivity
import com.pixelz360.docsign.imagetopdf.creator.R
import com.pixelz360.docsign.imagetopdf.creator.language.LanguageHelper


class LanguageAdapter(
    private val context: Context,
    private val languages: List<LanguageModel>,
    private val onItemClickListener: OnLanguageItemClickListener // Add interface for click listener
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    var languageCode:String = ""
    var lanugageNameCheck:String = ""





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.language_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languages[position]
        holder.bind(language)
    }

    override fun getItemCount(): Int {
        return languages.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val languageName = itemView.languageName
//        private val radioButton = itemView.radioButton

        var languageIcon: ImageView
        var languageName: TextView
//        var radioButton: RadioButton
        var radioButton: ImageView
//        var languageItemMianLayout: LinearLayout


        init {
            languageIcon = itemView.findViewById(R.id.languageIcon)
            languageName = itemView.findViewById(R.id.languageName)
            radioButton = itemView.findViewById(R.id.radioBtn)
//            languageItemMianLayout = itemView.findViewById(R.id.languageItemMianLayout)

        }





        fun bind(language: LanguageModel) {
            languageName.text = language.name
            languageIcon.setImageResource(language.flagResource)


            val selecteLanguageCode = LanguageHelper.loadLocale(context)

            if (selecteLanguageCode!=null || languageCode!=null){

                if (languageCode == ""){

                    if(selecteLanguageCode.equals(language.code)){
//                        languageItemMianLayout.background = ContextCompat.getDrawable(context,R.drawable.language_selected_item_bg)
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.white))
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.black))


//                        radioButton.isChecked = true
                        radioButton.setImageDrawable(context.resources.getDrawable(R.drawable.language_check_box_icon));


                        Log.d("bitmapbitmap", "VISIBLE   " + language.code);
                    } else{
//                        languageItemMianLayout.background = ContextCompat.getDrawable(context,R.drawable.language_unselected_item_bg)
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.black))
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.headingtextcolor))



                        Log.d("bitmapbitmap", "INVISIBLE");
//                        radioButton.isChecked = false
                        radioButton.setImageDrawable(context.resources.getDrawable(R.drawable.check_box_un_selected_icon));

                    }



                }else{

                    if(languageCode.equals(language.code)){
//                        languageItemMianLayout.background = ContextCompat.getDrawable(context,R.drawable.language_selected_item_bg)
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.white))
//                        radioButton.setTextColor(ContextCompat.getColor(context,R.color.white))
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.black))

//                        radioButton.isChecked = true
                        radioButton.setImageDrawable(context.resources.getDrawable(R.drawable.language_check_box_icon));

                        Log.d("bitmapbitmap", "VISIBLE   " + language.code);
                    } else{
//                        languageItemMianLayout.background = ContextCompat.getDrawable(context,R.drawable.language_unselected_item_bg)
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.black))
                        languageName.setTextColor(ContextCompat.getColor(context,R.color.headingtextcolor))
                        Log.d("bitmapbitmap", "INVISIBLE");
//                        radioButton.isChecked = false
                        radioButton.setImageDrawable(context.resources.getDrawable(R.drawable.check_box_un_selected_icon));
                    }


                }





            }


//            radioButton.setOnClickListener {
//
//
//                languageCode = language.code
//                lanugageNameCheck = language.name
//                val context = context as AppCompatActivity
//
//                context.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
//
//                Log.d("checklanguagename", language.name)
//
//
//
//
//
//                notifyDataSetChanged()
//
//                onItemClickListener.onItemClick(language,languageCode,lanugageNameCheck)
//
//
//
//            }

            itemView.setOnClickListener {


                languageCode = language.code
                lanugageNameCheck = language.name
                val context = context as AppCompatActivity

                context.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

                Log.d("checklanguagename", language.name)


                notifyDataSetChanged()


                onItemClickListener.onItemClick(language,languageCode,lanugageNameCheck)



                radioButton.setOnClickListener {


                    languageCode = language.code
                    lanugageNameCheck = language.name
                    val context = context as AppCompatActivity

                    context.window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

                    Log.d("checklanguagename", language.name)





                    notifyDataSetChanged()

                    onItemClickListener.onItemClick(language,languageCode,lanugageNameCheck)



                }



            }




            Log.d("bitmapbitmap", "checkBtn click  " + languageCode);
            }


        }

    private fun setLocale(context: Context, languageCode: String, languageName: String) {

        LanguageHelper.setLocale(context, languageCode)  // SET LANGUAGE FUNCTION
        LanguageHelper.addMyPrefsLanguageName(context,languageName) // SET LANGUAGE NAME FUNCTION
        Log.d("checklanguagename",languageName)


        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()


    }


    interface OnLanguageItemClickListener {
        fun onItemClick(language: LanguageModel, languageCode: String, lanugageNameCheck: String)
    }




}