package com.example.nfcreader

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        when {
            nfcAdapter == null -> resultText.text = getString(R.string.nfc_not_supported)
            nfcAdapter?.isEnabled == false -> resultText.text = getString(R.string.nfc_disabled)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag) {
        val uid = tag.id.joinToString(":") { byte -> "%02X".format(byte) }
        val technologies = tag.techList.joinToString(separator = "\n") { tech ->
            "- ${tech.substringAfterLast('.')}"
        }

        val payloadText = readNdefText(tag)
        val result = buildString {
            appendLine("读取成功")
            appendLine("UID: $uid")
            appendLine()
            appendLine("Tech 列表:")
            appendLine(technologies)
            appendLine()
            appendLine("NDEF 文本:")
            append(payloadText ?: "未检测到NDEF文本记录")
        }

        runOnUiThread {
            resultText.text = result
        }
    }

    private fun readNdefText(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        return try {
            ndef.connect()
            val message: NdefMessage = ndef.ndefMessage ?: return null
            message.records
                .firstNotNullOfOrNull { record -> parseTextRecord(record) }
        } finally {
            if (ndef.isConnected) {
                ndef.close()
            }
        }
    }

    private fun parseTextRecord(record: NdefRecord): String? {
        if (record.tnf != NdefRecord.TNF_WELL_KNOWN || !record.type.contentEquals(NdefRecord.RTD_TEXT)) {
            return null
        }

        val payload = record.payload
        if (payload.isEmpty()) {
            return null
        }

        val status = payload[0].toInt()
        val languageCodeLength = status and 0x3F
        val encoding = if (status and 0x80 == 0) Charsets.UTF_8 else Charset.forName("UTF-16")

        if (payload.size <= languageCodeLength + 1) {
            return null
        }

        return String(
            payload,
            languageCodeLength + 1,
            payload.size - languageCodeLength - 1,
            encoding
        )
    }
}
