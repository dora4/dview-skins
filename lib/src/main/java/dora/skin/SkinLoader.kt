package dora.skin

import android.content.res.Resources.NotFoundException
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import dora.util.LogUtils

class SkinLoader(

    private val resources: Resources, private val pluginPkgName: String, private
        var suffix: String? = "") : ISkinLoader {

    /**
     * 如果你需要改变suffix，你应该调用SkinManager的changeSkin。
     */
    internal fun setSuffix(suffix: String? = "") {
        this.suffix = suffix
    }

    fun getBitmap(resName: String): Bitmap? {
        return BitmapFactory.decodeResource(
            resources,
            resources.getIdentifier(
                appendSuffix(resName),
                DEF_TYPE_DRAWABLE,
                pluginPkgName
            )
        )
    }

    fun getDrawable(resName: String): Drawable? {
        return try {
            resources.getDrawable(
                resources.getIdentifier(
                    appendSuffix(resName),
                    DEF_TYPE_DRAWABLE,
                    pluginPkgName
                )
            )
        } catch (e: NotFoundException) {
            LogUtils.e(e.toString())
            null
        }
    }

    fun getColor(resName: String): Int {
        return try {
            resources.getColor(resources.getIdentifier(appendSuffix(resName), DEF_TYPE_COLOR, pluginPkgName))
        } catch (e: NotFoundException) {
            LogUtils.e(e.toString())
            -1
        }
    }

    fun getColorStateList(resName: String): ColorStateList? {
        return try {
            resources.getColorStateList(
                resources.getIdentifier(
                    appendSuffix(resName),
                    DEF_TYPE_COLOR,
                    pluginPkgName
                )
            )
        } catch (e: NotFoundException) {
            LogUtils.e(e.toString())
            null
        }
    }

    /**
     * 追加皮肤后缀。
     */
    private fun appendSuffix(name: String): String {
        var nameWithSuffix = name
        if (!TextUtils.isEmpty(suffix)) nameWithSuffix += "_$suffix"
        LogUtils.i("SkinLoader appendSuffix:$suffix, result=$nameWithSuffix")
        return nameWithSuffix
    }

    override fun setImageDrawable(imageView: ImageView, resName: String) {
        val drawable = getDrawable(resName) ?: return
        imageView.setImageDrawable(drawable)
    }

    override fun setImageDrawable(imageView: ImageView, resName: String?, fallback: Int) {
        if (resName == null) {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, fallback))
            return
        }
        val drawable = getDrawable(resName) ?: return
        imageView.setImageDrawable(drawable)
    }

    override fun setBackgroundDrawable(view: View, resName: String) {
        val drawable = getDrawable(resName) ?: return
        view.background = drawable
    }

    override fun setBackgroundDrawable(view: View, resName: String?, fallback: Int) {
        if (resName == null) {
            view.background = ContextCompat.getDrawable(view.context, fallback)
            return
        }
        val drawable = getDrawable(resName) ?: return
        view.background = drawable
    }

    override fun setBackgroundColor(view: View, resName: String) {
        val color = getColor(resName)
        view.setBackgroundColor(color)
    }

    override fun setBackgroundColor(view: View, resName: String?, fallback: Int) {
        if (resName == null) {
            view.setBackgroundColor(fallback)
            return
        }
        val color = getColor(resName)
        view.setBackgroundColor(color)
    }

    companion object {
        private const val DEF_TYPE_DRAWABLE = "drawable"
        private const val DEF_TYPE_COLOR = "color"
    }
}