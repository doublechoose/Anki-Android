package com.ichi2.anki

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import com.ichi2.anki.dialogs.SimpleMessageDialog
import com.ichi2.libanki.Collection

/**
 * Created by Wangzeshuang on 2018/8/1.
 */

public class AnkiActivityK : AppCompatActivity(),
        LoaderManager.LoaderCallbacks<Collection>,
        SimpleMessageDialog.SimpleMessageDialogListener {

    public val SIMPLE_NOTIFICATION_ID = 0
//    val mHandler = DialogHandlerK(this as AnkiActivityK)

    companion object {
        val REQUEST_REVIEW = 901

    }


    override fun onLoadFinished(loader: Loader<Collection>?, data: Collection?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Collection> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoaderReset(loader: Loader<Collection>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dismissSimpleMessageDialog(reload: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}