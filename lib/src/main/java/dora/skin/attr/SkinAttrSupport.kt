package dora.skin.attr

import android.content.Context
import android.util.AttributeSet
import dora.skin.SkinConfig
import dora.util.TextUtils
import java.util.ArrayList

/**
 * 皮肤属性工具类。
 */
object SkinAttrSupport {

    /**
     * 从xml的属性集合中获取皮肤相关的属性。
     */
    fun getSkinAttrs(attrs: AttributeSet, context: Context): MutableList<SkinAttr> {
        val skinAttrs: MutableList<SkinAttr> = ArrayList()
        var skinAttr: SkinAttr
        for (i in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(i)
            val attrValue = attrs.getAttributeValue(i)
            val attrType = getSupportAttrType(attrName) ?: continue
            if (attrValue.startsWith("@")) {
                val ref = attrValue.substring(1)
                if (TextUtils.isEqualTo(ref, "null")) {
                    // 跳过@null
                    continue
                }
                val id = ref.toInt()
                // 获取资源id的实体名称
                val entryName = context.resources.getResourceEntryName(id)
                if (entryName.startsWith(SkinConfig.ATTR_PREFIX)) {
                    skinAttr = SkinAttr(attrType, entryName)
                    skinAttrs.add(skinAttr)
                }
            }
        }
        return skinAttrs
    }

    private fun getSupportAttrType(attrName: String): SkinAttrType? {
        for (attrType in SkinAttrType.values()) {
            if (attrType.attrType == attrName) return attrType
        }
        return null
    }
}