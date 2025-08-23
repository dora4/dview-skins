package dora.skin.base

import android.content.Context
import android.os.Bundle
import android.util.ArrayMap
import android.util.AttributeSet
import android.view.InflateException
import android.view.LayoutInflater
import android.view.LayoutInflater.Factory2
import android.view.View
import androidx.core.view.LayoutInflaterCompat
import androidx.databinding.ViewDataBinding
import dora.BaseActivity
import dora.skin.SkinManager
import dora.skin.attr.SkinAttr
import dora.skin.attr.SkinAttrSupport
import dora.skin.attr.SkinView
import dora.skin.listener.ISkinChangeListener
import dora.util.ReflectionUtils
import java.lang.reflect.Constructor
import java.lang.reflect.Method

abstract class BaseSkinBindingActivity<T : ViewDataBinding> : BaseActivity<T>(),
    ISkinChangeListener, Factory2 {

    private val constructorArgs = arrayOfNulls<Any>(2)

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        // Factory2 必须实现的两个重载，转调到四参版本，确保两条创建路径都被拦截
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (createViewMethod == null) {
            val methodOnCreateView = ReflectionUtils.findMethod(
                delegate.javaClass, false,
                "createView", *createViewSignature
            )
            createViewMethod = methodOnCreateView
        }
        var view: View? = ReflectionUtils.invokeMethod(
            delegate, createViewMethod, parent, name,
            context, attrs
        ) as View?
        if (view == null) {
            view = createViewFromTag(context, name, attrs)
        }
        val skinAttrList = SkinAttrSupport.getSkinAttrs(attrs, context)
        if (skinAttrList.isEmpty()) {
            return view
        }
        injectSkin(view, skinAttrList)
        return view
    }

    private fun injectSkin(view: View?, skinAttrList: MutableList<SkinAttr>) {
        if (skinAttrList.isNotEmpty()) {
            var skinViews = SkinManager.getSkinViews(this)
            if (skinViews == null) {
                skinViews = arrayListOf()
            }
            skinViews.add(SkinView(view, skinAttrList))
            SkinManager.addSkinView(this, skinViews)
            if (SkinManager.needChangeSkin()) {
                SkinManager.apply(this)
            }
        }
    }

    private fun createViewFromTag(context: Context, viewName: String, attrs: AttributeSet): View? {
        var name = viewName
        if (name == "view") {
            name = attrs.getAttributeValue(null, "class")
        }
        return try {
            constructorArgs[0] = context
            constructorArgs[1] = attrs
            if (name.indexOf('.') == -1) {
                // 多前缀尝试，命中率更高
                createView(context, name, "android.widget.")
                    ?: createView(context, name, "android.view.")
                    ?: createView(context, name, "android.webkit.")
            } else {
                createView(context, name, null)
            }
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            null
        } finally {
            // Don't retain references on context.
            constructorArgs[0] = null
            constructorArgs[1] = null
        }
    }

    @Throws(InflateException::class)
    private fun createView(context: Context, name: String, prefix: String?): View? {
        var constructor = constructorMap[name]
        return try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = context.classLoader.loadClass(
                    if (prefix != null) prefix + name else name
                ).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*constructorSignature)
                constructorMap[name] = constructor
            }
            constructor!!.isAccessible = true
            constructor.newInstance(*constructorArgs)
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, this)
        super.onCreate(savedInstanceState)
        SkinManager.addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.removeListener(this)
    }

    override fun onSkinChanged(suffix: String) {
        SkinManager.apply(this)
    }

    companion object {
        val constructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
        private val constructorMap: MutableMap<String, Constructor<out View>> = ArrayMap()
        private var createViewMethod: Method? = null
        val createViewSignature = arrayOf(
            View::class.java, String::class.java,
            Context::class.java, AttributeSet::class.java
        )
    }
}