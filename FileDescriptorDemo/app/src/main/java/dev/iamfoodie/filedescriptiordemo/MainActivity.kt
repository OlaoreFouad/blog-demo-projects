package dev.iamfoodie.filedescriptiordemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dev.iamfoodie.filedescriptiordemo.views.FileDescriptor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fileDescriptor: FileDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fileDescriptor = findViewById(R.id.file_descriptor)
        file_select_button.setOnClickListener {
            selectFile()
        }
    }

    private fun selectFile() {
        val selectFileIntent = Intent(Intent.ACTION_GET_CONTENT)
        selectFileIntent.type = "*/*"
        startActivityForResult(selectFileIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val fileUri = data.data
                fileUri?.let {
                    fileDescriptor.fileUri = it
                }
            }
        }
    }

}
