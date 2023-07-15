package dora.lifecycle.application

import android.app.Application
import android.content.Context
import dora.skin.SkinManager

class SkinsAppLifecycle : ApplicationLifecycleCallbacks {

    override fun attachBaseContext(base: Context) {
    }

    override fun onCreate(application: Application) {
        SkinManager.init(application)
    }

    override fun onTerminate(application: Application) {
    }
}