package dora.skin

import dora.skin.listener.ISkinChangeListener
import dora.skin.attr.SkinView
import android.content.Context
import dora.util.SPUtils
import kotlin.Throws
import android.content.res.AssetManager
import android.content.res.Resources
import dora.skin.exception.ApplySkinException
import dora.skin.listener.ISkinApplyListener
import dora.util.TextUtils
import dora.util.ThreadUtils
import dora.util.ThreadUtils.DoraTask
import java.io.File
import java.util.ArrayList
import java.util.HashMap

/**
 * 皮肤管理器。
 */
object SkinManager {

    private var skinLoader: SkinLoader? = null
    private var applyPlugin = false
    private lateinit var applicationContext: Context

    /**
     * 换肤资源的皮肤后缀。
     */
    private var suffix: String = ""
    private var curPluginPath: String? = null
    private var curPluginPkgName: String? = null
    private val skinViewMap: MutableMap<ISkinChangeListener, MutableList<SkinView>> = HashMap()
    private val skinListeners: MutableList<ISkinChangeListener> = ArrayList()

    fun init(context: Context) {
        applicationContext = context.applicationContext
        val skinPluginPath = SPUtils.readString(applicationContext, SkinConfig.PREFS_PLUGIN_PATH)
        val skinPluginPkgName = SPUtils.readString(applicationContext, SkinConfig.PREFS_PLUGIN_PKG_NAME)
        suffix = SPUtils.readString(applicationContext, SkinConfig.PREFS_PLUGIN_SUFFIX, "")
        if (TextUtils.isEmpty(skinPluginPath)) return
        if (!File(skinPluginPath).exists()) return
        try {
            loadPlugin(skinPluginPath, skinPluginPkgName, suffix)
            curPluginPath = skinPluginPath
            curPluginPkgName = skinPluginPkgName
        } catch (e: ApplySkinException) {
            SPUtils.remove(context, SkinConfig.PREFS_PLUGIN_PATH)
            SPUtils.remove(context, SkinConfig.PREFS_PLUGIN_PKG_NAME)
            SPUtils.remove(context, SkinConfig.PREFS_PLUGIN_SUFFIX)
            e.printStackTrace()
        }
    }

    fun getCurSkinSuffix() : String {
        return suffix
    }

    @Throws(ApplySkinException::class)
    private fun loadPlugin(skinPath: String, skinPkgName: String, suffix: String? = "") {
        val ok = checkPlugin(skinPath, skinPkgName)
        if (!ok) {
            throw ApplySkinException(skinPath, skinPkgName, suffix)
        }
        val assetManager = AssetManager::class.java.newInstance()
        val addAssetPath = assetManager.javaClass.getMethod("addAssetPath", String::class.java)
        addAssetPath.invoke(assetManager, skinPath)
        val superRes = applicationContext.resources
        val resources = Resources(assetManager, superRes.displayMetrics, superRes.configuration)
        skinLoader = SkinLoader(resources, skinPkgName, suffix)
        applyPlugin = true
    }

    private fun checkPlugin(skinPath: String, skinPkgName: String): Boolean {
        return TextUtils.isNotEmpty(skinPath) && TextUtils.isNotEmpty(skinPkgName)
    }

    fun removePlugin() {
        clearPluginInfo()
        notifyListeners()
    }

    fun needChangeSkin(): Boolean {
        return applyPlugin || TextUtils.isNotEmpty(suffix)
    }

    fun getLoader(): SkinLoader {
        if (!applyPlugin || skinLoader == null) {
            // 没有应用皮肤插件就创建一个ResourceManager
            skinLoader = SkinLoader(applicationContext.resources, applicationContext.packageName, suffix)
            applyPlugin = true
        }
        return skinLoader!!
    }

    private fun clearPluginInfo() {
        curPluginPath = null
        curPluginPkgName = null
        suffix = ""
        applyPlugin = false
        SPUtils.remove(applicationContext, SkinConfig.PREFS_PLUGIN_PATH)
        SPUtils.remove(applicationContext, SkinConfig.PREFS_PLUGIN_PKG_NAME)
        SPUtils.remove(applicationContext, SkinConfig.PREFS_PLUGIN_SUFFIX)
    }

    private fun updatePluginInfo(skinPluginPath: String, skinPluginPkgName: String, suffix: String) {
        SPUtils.writeString(applicationContext, SkinConfig.PREFS_PLUGIN_PATH, skinPluginPath)
        SPUtils.writeString(applicationContext, SkinConfig.PREFS_PLUGIN_PKG_NAME, skinPluginPkgName)
        SPUtils.writeString(applicationContext, SkinConfig.PREFS_PLUGIN_SUFFIX, suffix)
        curPluginPkgName = skinPluginPkgName
        curPluginPath = skinPluginPath
        this.suffix = suffix
    }

    /**
     * 应用内换肤，传入资源区别的后缀。
     */
    fun changeSkin(suffix: String) {
        clearPluginInfo()
        this.suffix = suffix
        SPUtils.writeString(applicationContext, SkinConfig.PREFS_PLUGIN_SUFFIX, suffix)
        notifyListeners()
    }

    /**
     * 根据suffix选择插件内某套皮肤，默认为""。
     */
    @JvmOverloads
    fun changeSkin(
        skinPluginPath: String,
        skinPluginPkgName: String,
        suffix: String? = "",
        callback: ISkinApplyListener? = ISkinApplyListener.DEFAULT
    ) {
        callback?.onStart()
        checkPlugin(skinPluginPath, skinPluginPkgName)
        if (skinPluginPath == curPluginPath && skinPluginPkgName == curPluginPkgName) {
            return
        }
        ThreadUtils.executeByCached(object :
            DoraTask<Boolean>(ThreadUtils.Consumer { ok: Boolean ->
                if (ok) {
                    updatePluginInfo(skinPluginPath, skinPluginPkgName, suffix ?: "")
                    notifyListeners()
                    callback?.onComplete()
                }
            }) {
            override fun doInBackground(): Boolean {
                return try {
                    loadPlugin(skinPluginPath, skinPluginPkgName, suffix)
                    true
                } catch (e: ApplySkinException) {
                    callback?.onError(e)
                    false
                }
            }
        })
    }

    fun addSkinView(listener: ISkinChangeListener, skinViews: MutableList<SkinView>) {
        skinViewMap[listener] = skinViews
    }

    fun getSkinViews(listener: ISkinChangeListener): MutableList<SkinView> {
        return skinViewMap[listener]!!
    }

    fun apply(listener: ISkinChangeListener) {
        val skinViews = getSkinViews(listener)
        for (skinView in skinViews) {
            skinView.apply()
        }
    }

    fun addChangedListener(listener: ISkinChangeListener) {
        skinListeners.add(listener)
    }

    fun removeChangedListener(listener: ISkinChangeListener) {
        skinListeners.remove(listener)
        skinViewMap.remove(listener)
    }

    fun notifyListeners() {
        for (listener in skinListeners) {
            listener.onSkinChanged(suffix)
        }
    }
}