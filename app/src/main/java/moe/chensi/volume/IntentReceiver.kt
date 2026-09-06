package moe.chensi.volume

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class IntentReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "VolumeIntent"
        private const val ACTION_SET_APP_VOLUME = "moe.chensi.volume.action.SET_APP_VOLUME"
        private const val EXTRA_PACKAGE = "moe.chensi.volume.extra.PACKAGE"
        private const val EXTRA_VOLUME = "moe.chensi.volume.extra.VOLUME"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SET_APP_VOLUME) {
            return
        }

        val targetPackage = intent.getStringExtra(EXTRA_PACKAGE)
        if (targetPackage.isNullOrBlank()) {
            Log.w(TAG, "SET_APP_VOLUME broadcast is missing the $EXTRA_PACKAGE extra")
            return
        }

        if (!intent.hasExtra(EXTRA_VOLUME)) {
            Log.w(TAG, "SET_APP_VOLUME broadcast is missing the $EXTRA_VOLUME extra")
            return
        }
        val volumeMultiplier = intent.getFloatExtra(EXTRA_VOLUME, -1.0f)

        // Delegate to the shared Manager instance - the same one the UI and the
        // accessibility service read from. This persists the value (DataStore)
        // AND immediately applies it to any audio the target app is currently
        // playing, via Manager's already-Shizuku-privileged volume path.
        val manager = (context.applicationContext as MyApplication).manager
        val applied = manager.setAppVolume(targetPackage, volumeMultiplier)

        if (applied) {
            Log.d(TAG, "Set $targetPackage volume to $volumeMultiplier")
        } else {
            Log.w(
                TAG,
                "Could not set volume for $targetPackage (invalid value or package not installed)"
            )
        }
    }
}
