package com.ichi2.anki.dialogs

import android.os.Handler
import android.os.Message
import com.ichi2.anki.*
import com.ichi2.async.Connection
import com.ichi2.libanki.Utils
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Created by Wangzeshuang on 2018/8/1.
 * 我们不允许从Loader.onLoadCompleted（）提交fragment事务，而且从AsyncTask onComplete
 * 事件提交它们是不安全的，所以我们通过使用消息处理程序来解决这个问题。
 */
class DialogHandlerK(activity: AnkiActivity) : Handler() {
    companion object {
        const val INTENT_SYNC_MIN_INTERVAL = 2 * 60000

        const val MSG_SHOW_COLLECTION_LOADING_ERROR_DIALOG = 0
        const val MSG_SHOW_COLLECTION_IMPORT_REPLACE_DIALOG = 1
        const val MSG_SHOW_COLLECTION_IMPORT_ADD_DIALOG = 2
        const val MSG_SHOW_SYNC_ERROR_DIALOG = 3
        const val MSG_SHOW_EXPORT_COMPLETE_DIALOG = 4
        const val MSG_SHOW_MEDIA_CHECK_COMPLETE_DIALOG = 5
        const val MSG_SHOW_DATABASE_ERROR_DIALOG = 6
        const val MSG_SHOW_FORCE_FULL_SYNC_DIALOG = 7
        const val MSG_DO_SYNC = 8

        private var sStoredMessage: Message? = null

        fun storeMessage(message: Message) {
            Timber.d("Storing persistent message")
            sStoredMessage = message
        }
    }

    val mActivity = WeakReference(activity)

    override fun handleMessage(msg: Message?) {
        val msgData = msg!!.data
        if (msg.what == MSG_SHOW_COLLECTION_LOADING_ERROR_DIALOG) {
            (mActivity.get() as DeckPicker).showDatabaseErrorDialog(DatabaseErrorDialog.DIALOG_LOAD_FAILED)

        } else if (msg.what == MSG_SHOW_COLLECTION_IMPORT_REPLACE_DIALOG) run {
            // Handle import of collection package APKG
            (mActivity.get() as DeckPicker).showImportDialog(ImportDialog.DIALOG_IMPORT_REPLACE_CONFIRM, msgData.getString("importPath"))
        } else if (msg.what == MSG_SHOW_COLLECTION_IMPORT_ADD_DIALOG) {
            //handle import of deck package APKG
            (mActivity.get() as DeckPicker).showImportDialog(ImportDialog.DIALOG_IMPORT_ADD_CONFIRM, msgData.getString("importPath"))
        } else if (msg.what == MSG_SHOW_SYNC_ERROR_DIALOG) {
            val id = msgData.getInt("dialogType")
            val message = msgData.getString("dialogMessage")
            (mActivity.get() as DeckPicker).showSyncErrorDialog(id, message)
        } else if (msg.what == MSG_SHOW_EXPORT_COMPLETE_DIALOG) {
            // Export complete
            val f = DeckPickerExportCompleteDialog.newInstance(msgData.getString("exportPath"))
            (mActivity.get() as DeckPicker).showAsyncDialogFragment(f)
        } else if (msg.what == MSG_SHOW_MEDIA_CHECK_COMPLETE_DIALOG) {
            // Media check results
            val id = msgData.getInt("dialogType")
            if (id != MediaCheckDialog.DIALOG_CONFIRM_MEDIA_CHECK) {
                val checkList = ArrayList<List<String>>()
                checkList.add(msgData.getStringArrayList("nohave"))
                checkList.add(msgData.getStringArrayList("unused"))
                checkList.add(msgData.getStringArrayList("invalid"))
                (mActivity.get() as DeckPicker).showMediaCheckDialog(id, checkList)
            }
        } else if (msg.what == MSG_SHOW_DATABASE_ERROR_DIALOG) {
            // Database error dialog
            (mActivity.get() as DeckPicker).showDatabaseErrorDialog(msgData.getInt("dialogType"))
        } else if (msg.what == MSG_SHOW_FORCE_FULL_SYNC_DIALOG) {
            // Confirmation dialog for forcing full sync
            val dialog = ConfirmationDialog()
            val confirm = Runnable {
                // Bypass the check once the user confirms
                CollectionHelper.getInstance().getCol(AnkiDroidApp.getInstance())!!.modSchemaNoCheck()
            }
            dialog.setConfirm(confirm)
            dialog.setArgs(msgData.getString("message"))
            (mActivity.get() as DeckPicker).showDialogFragment(dialog)
        } else if (msg.what == MSG_DO_SYNC) {
            val preferences = AnkiDroidApp.getSharedPrefs(mActivity.get())
            val res = mActivity.get()!!.resources
            val hkey = preferences.getString("hkey", "")
            val limited = Utils.intNow(1000) - preferences.getLong("lastSyncTime", 0) < INTENT_SYNC_MIN_INTERVAL
            if (!limited && hkey!!.length > 0 && Connection.isOnline()) {
                (mActivity.get() as DeckPicker).sync()
            } else {
                val err = res.getString(R.string.sync_error)
                if (limited) {
                    (mActivity.get() as DeckPicker).showSimpleNotification(err, res.getString(R.string.sync_too_busy))
                } else {
                    (mActivity.get() as DeckPicker).showSimpleNotification(err, res.getString(R.string.youre_offline))
                }
            }
            (mActivity.get() as DeckPicker).finishWithoutAnimation()
        }
    }

    fun readMessage() {
        Timber.d("Reading persistent message")
        if (sStoredMessage != null) {
            sendMessage(sStoredMessage)
        }
        sStoredMessage = null
    }

}