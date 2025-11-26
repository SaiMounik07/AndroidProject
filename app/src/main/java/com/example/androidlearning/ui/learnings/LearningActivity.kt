package com.example.androidlearning.ui.learnings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlearning.R
import com.example.androidlearning.base.constants.Constants.CANCEL
import com.example.androidlearning.base.constants.Constants.CLICK_NO
import com.example.androidlearning.base.constants.Constants.CLICK_YES
import com.example.androidlearning.base.constants.Constants.DIALOG_BOX
import com.example.androidlearning.base.constants.Constants.EXIT_MESSAGE
import com.example.androidlearning.base.constants.Constants.NO
import com.example.androidlearning.base.constants.Constants.YES
import com.example.androidlearning.databinding.ActivityLearningBinding
import com.example.androidlearning.ui.login.ConstraintLoginActivity
import com.example.androidlearning.ui.login.LinearLoginActivity
import com.example.androidlearning.ui.login.RelativeLoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class LearningActivity: AppCompatActivity() {
    lateinit var binding: ActivityLearningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dialogButton.setOnClickListener {
            showBottomSheet()
        }
        binding.linearlayout.setOnClickListener {
            startActivity(Intent(this, LinearLoginActivity::class.java))
        }
        binding.relativelayout.setOnClickListener {
            startActivity(Intent(this, RelativeLoginActivity::class.java))
        }
        binding.constraintlayout.setOnClickListener {
            startActivity(Intent(this, ConstraintLoginActivity::class.java))
        }
        binding.dialogButton.setOnClickListener {
            showDialogBox()
        }
    }
    fun showBottomSheet(){
        val dialog= BottomSheetDialog(this)
        val dialogView= LayoutInflater.from(this).inflate(R.layout.card_product,null)
        val btn1=dialogView.findViewById<Button>(R.id.btn_close1)
        val btn2=dialogView.findViewById<Button>(R.id.btn_close2)
        val closeBtn=dialogView.findViewById<ImageView>(R.id.iv_close)

        btn1.setOnClickListener {
            Toast.makeText(this,"Btn1 cliked", Toast.LENGTH_SHORT).show()
        }
        btn2.setOnClickListener {
            Toast.makeText(this, "Btn2 cliked", Toast.LENGTH_SHORT).show()
        }
        closeBtn?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        dialog.show()
    }
    fun showDialogBox() {
        AlertDialog.Builder(this)
            .setTitle(DIALOG_BOX)
            .setMessage(EXIT_MESSAGE)
            .setPositiveButton(YES) { _, _ ->
                Toast.makeText(this, CLICK_YES, Toast.LENGTH_SHORT).show()
              finishAffinity()
            }
            .setNegativeButton(NO) { _, _ ->
                Toast.makeText(this, CLICK_NO, Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton(CANCEL) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}