package com.github.arissa34.jnigenplugin.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class JniGenColorSettings : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
        AttributesDescriptor("JniGen code", JNIGEN_COMMENT),
        AttributesDescriptor("Header include tag (#include)", INCLUDE_JNI),
        AttributesDescriptor("Header library name (<jni.h>)", HEADER_JNI),
        AttributesDescriptor("Cpp code", CPP_JNI),
        AttributesDescriptor("Object", OBJECT_JNI),
        AttributesDescriptor("String", STRING_JNI),
        AttributesDescriptor("Number", NUMBER_JNI),
        AttributesDescriptor("Comment", COMMENT_JNI),
        AttributesDescriptor("Default jni function", DEFAULT_JNI),
        AttributesDescriptor("Function", FUNCTION_JNI),
        AttributesDescriptor("Variables", VARIABLE_JNI),
        AttributesDescriptor("Condition", CONDITION_JNI)
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "JniGen"

    override fun getIcon(): Icon? = null

    override fun getHighlighter(): SyntaxHighlighter = PlainSyntaxHighlighter()

    override fun getDemoText(): String = ""

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null
}