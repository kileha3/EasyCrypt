/**
 * Copyright 2018 Priyank Vasa
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pvryan.easycryptsample.action.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.hash.ECHash
import com.pvryan.easycrypt.hash.ECHashAlgorithms
import com.pvryan.easycryptsample.R
import kotlinx.android.synthetic.main.fragment_hash_file.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.onUiThread
import org.jetbrains.anko.support.v4.toast

class FragmentHashFile : Fragment(), AnkoLogger, ECResultListener {

    private val _rCHash = 2
    private val eCryptHash = ECHash()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hash_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSelectHashF.setOnClickListener {
            selectFile(_rCHash)
        }
    }

    private fun selectFile(requestCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            val fis = context?.contentResolver?.openInputStream(data?.data)

            progressBarF.visibility = View.VISIBLE

            when (requestCode) {
                _rCHash -> {
                    eCryptHash.calculate(fis, ECHashAlgorithms.SHA_512, this)
                }
            }
        }
    }

    override fun onProgress(newBytes: Int, bytesProcessed: Long) {
        progressBarF.progress = (bytesProcessed / 1024).toInt()
    }

    override fun <T> onSuccess(result: T) {
        onUiThread {
            progressBarF.visibility = View.INVISIBLE
            tvResultF.text = result as String
        }
    }

    override fun onFailure(message: String, e: Exception) {
        e.printStackTrace()
        onUiThread {
            progressBarF.visibility = View.INVISIBLE
            toast("Error: $message")
        }
    }

    companion object {
        fun newInstance(): Fragment = FragmentHashFile()
    }
}