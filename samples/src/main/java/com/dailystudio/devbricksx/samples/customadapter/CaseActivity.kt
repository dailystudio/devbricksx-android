package com.dailystudio.devbricksx.samples.customadapter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.customadapter.model.ChatRecordViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CaseActivity : BaseCaseActivity() {

    companion object {
        val RANDOM_SEED = Random(System.currentTimeMillis())
    }

    private var userInput: EditText? = null
    private var sendButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_custom_adapter)

        setupViews()
    }

    private fun setupViews() {
        userInput = findViewById(R.id.input)
        userInput?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendButton?.isEnabled = !(s == null || s.isEmpty())
            }
        })

        sendButton = findViewById(R.id.send_button)
        sendButton?.setOnClickListener{
            val editable = userInput?.text ?: return@setOnClickListener
            val text = editable.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                sendMessage(text)
                receiveReply(text)
            }

            editable.clear()
        }
    }

    private fun sendMessage(text: String) {
        val viewModel = ViewModelProvider(this).get(ChatRecordViewModel::class.java)

        val record = ChatRecord(System.currentTimeMillis(),
                text,
                MessageType.Send)

        viewModel.insertChatRecord(record)
    }

    private suspend fun receiveReply(text: String) {
        delay(RANDOM_SEED.nextLong(100, 1000))
        val viewModel = ViewModelProvider(this).get(ChatRecordViewModel::class.java)

        val record = ChatRecord(System.currentTimeMillis(),
                "$text",
                MessageType.Receive)

        viewModel.insertChatRecord(record)
    }

}
