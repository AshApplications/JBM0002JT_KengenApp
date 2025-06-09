package com.example.matrix

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.matrix.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val MAX_COL = 10
        const val MAX_ROW = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        keepGoing()

        binding.chkMultipleBlink.setOnCheckedChangeListener { buttonView, isChecked ->
            (binding.rvMatrix.adapter as MyAdapter?)?.setMultipleBlink(isChecked)
        }
    }

    private fun keepGoing() {

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@MainActivity).setMessage("Are you sure? You want to exit?")
                    .setPositiveButton("Yes") { dialog, which ->
                        finish()
                    }.setNegativeButton("No", null).create().show()
            }
        })

        binding.tvDisplay.setOnClickListener {
            if (binding.etRow.text.toString().trim().isEmpty() || binding.etColumn.text.toString()
                    .trim().isEmpty()
            ) {
                Toast.makeText(this, "Please enter valid value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var row = binding.etRow.text.toString().toInt()
            var column = binding.etColumn.text.toString().toInt()
            if (row == 0 || column == 0) {
                Toast.makeText(this, "Please enter valid value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (row > MAX_ROW) {
                row = MAX_ROW
                Toast.makeText(this, "Set row to its max limit $MAX_ROW", Toast.LENGTH_SHORT).show()
                binding.etRow.setText(MAX_ROW.toString())
            }
            if (column > MAX_COL) {
                column = MAX_COL
                Toast.makeText(this, "Set column to its max limit $MAX_COL", Toast.LENGTH_SHORT)
                    .show()
                binding.etRow.setText(MAX_COL.toString())
            }
            hideKeyboard()
            val matrix: Array<IntArray> = Utils.createMatrix(row, column)
            binding.rvMatrix.layoutManager = GridLayoutManager(this@MainActivity, column)
            val height =
                resources.displayMetrics.heightPixels - resources.getDimension(R.dimen.top_margin)
            binding.rvMatrix.adapter =
                MyAdapter(this@MainActivity, matrix, row, column, height.toInt())
        }
    }

    fun hideKeyboard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}