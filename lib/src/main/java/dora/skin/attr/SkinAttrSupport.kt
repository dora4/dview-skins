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
        for (i in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(i)
            val attrValue = attrs.getAttributeValue(i)
            val attrType = getSupportAttrType(attrName) ?: continue
            // 必须是资源引用
            if (!attrValue.startsWith("@")) {
                continue
            }
            // 跳过 @null
            if (attrValue == "@null") {
                continue
            }
            // 防御非法值
            val ref = attrValue.substring(1)
            val id = ref.toIntOrNull() ?: continue
            // 跳过无效资源
            if (id == 0) {
                continue
            }
            // 安全获取资源名
            val entryName = runCatching {
                context.resources.getResourceEntryName(id)
            }.getOrNull() ?: continue
            if (entryName.startsWith(SkinConfig.ATTR_PREFIX)) {
                skinAttrs.add(SkinAttr(attrType, entryName))
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