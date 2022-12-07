package com.example.auth.adaptor

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.auth.Data
import com.example.auth.MainActivity
import com.example.auth.R
import com.example.auth.entity.SecretInfo
import com.google.android.material.progressindicator.CircularProgressIndicator
import dev.turingcomplete.kotlinonetimepassword.GoogleAuthenticator


class SecretInfoAdaptor(private val dataSet: List<SecretInfo>) :
    RecyclerView.Adapter<SecretInfoAdaptor.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            // Define click listener for the ViewHolder's View.
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_secret, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val view = viewHolder.itemView
        val secretInfo = dataSet[position]
        val description = view.findViewById<EditText>(R.id.description)
        description.setText(secretInfo.nickname)

        val img = view.findViewById<ImageView>(R.id.check)
        img.setOnClickListener {
            secretInfo.nickname = description.text.toString()
            description.clearFocus()
            Data.dao!!.update(secretInfo)
            Data.secrets = Data.dao!!.getAll()
            img.visibility = View.GONE
            description.clearFocus()
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }

        view.setOnLongClickListener {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.pop_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.delete_item) {
                    secretInfo.id?.let { it1 -> Data.dao!!.deleteById(it1) }
                    Data.secrets.removeAt(position)
                    val intent = Intent(view.context,MainActivity::class.java)
                    view.context.startActivity(intent)

                }
                if (it.itemId == R.id.modify_item) {
                    description.isEnabled = true
                    description.requestFocus()
                    img.visibility = View.VISIBLE
                    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(description,0)

                }
                true
            }
            popupMenu.show()
            true
        }




        val code = view.findViewById<TextView>(R.id.code)
        val googleAuthenticator = GoogleAuthenticator(secretInfo.secret.toByteArray(Charsets.UTF_8))
        val pro = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)
        val animator = ValueAnimator.ofInt(100, 0)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener {
            val p = ((30 - System.currentTimeMillis() / 1000 % 30) / 30f * 100).toInt()
            Log.d("PROGRESS:", "onBindViewHolder: $p")
            if (p <= 20) {
                pro.setIndicatorColor(view.resources.getColor(R.color.red_code, null))
                code.setTextColor(view.resources.getColor(R.color.red_code, null))
            } else {
                pro.setIndicatorColor(view.resources.getColor(R.color.green_code, null))
                code.setTextColor(view.resources.getColor(R.color.green_code, null))
            }
            pro.progress = p
            val verifyCode = googleAuthenticator.generate()
            code.text = verifyCode.substring(0, 3) + " " + verifyCode.substring(3)
        }
        animator.start()

        // copy code
        view.setOnClickListener {
            val clipboard = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", code.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(view.context,"复制成功",Toast.LENGTH_SHORT).show()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}