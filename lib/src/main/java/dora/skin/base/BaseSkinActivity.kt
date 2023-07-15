package dora.skin.base

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.ArrayMap
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.LayoutInflaterFactory
import dora.skin.SkinManager
import dora.skin.attr.SkinAttr
import dora.skin.attr.SkinAttrSupport
import dora.skin.attr.SkinView
import dora.skin.listener.ISkinChangeListener
import dora.util.LogUtils
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

abstract class BaseSkinActivity : AppCompatActivity(),
    ISkinChangeListener, LayoutInflaterFactory {

    private val constructorArgs = arrayOfNulls<Any>(2)

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        val delegate = delegate
        var view: View? = null
        try {
            // public View createView
            // (View parent, final String name, @NonNull Context context, @NonNull AttributeSet attrs)
            if (createViewMethod == null) {
                val methodOnCreateView = delegate.javaClass.getMethod("createView", *createViewSignature)
                createViewMethod = methodOnCreateView
            }
            val obj = createViewMethod!!.invoke(delegate, parent, name, context, attrs)
            if (obj != null) {
                view = obj as View
            }
        } catch (e: NoSuchMethodException) {
            LogUtils.e(e.toString())
        } catch (e: InvocationTargetException) {
            LogUtils.e(e.toString())
        } catch (e: IllegalAccessException) {
            LogUtils.e(e.toString())
        }
        val skinAttrList = SkinAttrSupport.getSkinAttrs(attrs, context)
        if (skinAttrList.isEmpty()) {
            return view
        }
        if (view == null) {
            view = createViewFromTag(context, name, attrs)
        }
        injectSkin(view, skinAttrList)
        return view
    }

    private fun injectSkin(view: View?, skinAttrList: MutableList<SkinAttr>) {
        if (skinAttrList.isNotEmpty()) {
            val skinViews = SkinManager.getSkinViews(this)
            SkinManager.addSkinView(this, skinViews)
            skinViews.add(SkinView(view, skinAttrList))
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
            if (-1 == name.indexOf('.')) {
                // try the android.widget prefix first...
                createView(context, name, "android.widget.")
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
                        if (prefix != null) prefix + name else name).asSubclass(View::class.java)
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
        LayoutInflaterCompat.setFactory(layoutInflater, this)
        super.onCreate(savedInstanceState)
        SkinManager.addChangedListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinManager.removeChangedListener(this)
    }

    override fun onSkinChanged(suffix: String) {
        SkinManager.apply(this)
    }

    companion object {
        val constructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)
        private val constructorMap: MutableMap<String, Constructor<out View>?> = ArrayMap()
        private var createViewMethod: Method? = null
        val createViewSignature = arrayOf(View::class.java, String::class.java,
                Context::class.java, AttributeSet::class.java)
    }
}