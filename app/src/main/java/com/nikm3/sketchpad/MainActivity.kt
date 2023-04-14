package com.nikm3.sketchpad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class MainActivity : AppCompatActivity() {

    private lateinit var canvas: MyCanvasView
    private lateinit var colorButton: FloatingActionButton
    private lateinit var backgroundButton: FloatingActionButton
    private lateinit var trashButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        canvas = findViewById(R.id.myCanvasView)
        colorButton = findViewById(R.id.paintColorButton)
        backgroundButton = findViewById(R.id.backgroundColorButton)
        trashButton = findViewById(R.id.trashButton)

        colorButton.setOnClickListener {
            val obj = object : OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    /* Intentionally left blank */
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    canvas.setPaintColor(color)
                }

            }
            AmbilWarnaDialog(
                this,
                canvas.getPaintColor(),
                obj
            ).show()
        }



        backgroundButton.setOnClickListener {
            val obj = object : OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                    /* Intentionally left blank */
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    canvas.setBackgroundColor(color)
                }

            }
            AmbilWarnaDialog(
                this,
                canvas.getBackgroundColor(),
                obj
            ).show()
        }

        trashButton.setOnClickListener {
            canvas.reset()
        }
    }
}