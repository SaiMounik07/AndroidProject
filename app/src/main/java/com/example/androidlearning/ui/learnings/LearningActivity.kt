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
import com.example.androidlearning.ui.login.ConstraintLoginActivity
import com.example.androidlearning.ui.login.LinearLoginActivity
import com.example.androidlearning.ui.login.RelativeLoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

class LearningActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_learning)
//        binding = HomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        with (binding) {
//            linearlayout.setOnClickListener {
//
//            }
//            relativelayout.setOnClickListener {
//
//            }
//            constraintlayout.setOnClickListener {
//
//            }
//            showButton.setOnClickListener {
//
//            }
//        }

        val linear = findViewById<Button>(R.id.linearlayout)
        val relative = findViewById<Button>(R.id.relativelayout)
        val constraint = findViewById<Button>(R.id.constraintlayout)
        val showDialog=findViewById<MaterialButton>(R.id.show_button)
        val dialogButton=findViewById<MaterialButton>(R.id.dialog_button)
        showDialog.setOnClickListener {
            showBottomSheet()
        }
        linear.setOnClickListener {
            startActivity(Intent(this, LinearLoginActivity::class.java))
        }
        relative.setOnClickListener {
            startActivity(Intent(this, RelativeLoginActivity::class.java))
        }
        constraint.setOnClickListener {
            startActivity(Intent(this, ConstraintLoginActivity::class.java))
        }
        dialogButton.setOnClickListener {
            showDialogBox()
        }
    }
    fun showBottomSheet(){
        val dialog= BottomSheetDialog(this)
        val dialogView= LayoutInflater.from(this).inflate(R.layout.customsheet_bottom,null)
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
            .setTitle("Dialog Box")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { dialog, _ ->
                Toast.makeText(this, "Clicked Yes", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "Clicked No", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}