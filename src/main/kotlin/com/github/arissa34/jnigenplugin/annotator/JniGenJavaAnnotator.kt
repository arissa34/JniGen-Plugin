package com.github.arissa34.jnigenplugin.annotator

import com.github.arissa34.jnigenplugin.extention.isJniComment
import com.github.arissa34.jnigenplugin.extention.isNativeJniFunctionComment
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import com.intellij.codeInspection.InspectionsBundle
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Pattern.MULTILINE


class JniGenJavaAnnotator : Annotator {

    companion object {
        @JvmStatic
        val JNIGEN_COMMENT: TextAttributesKey =
            createTextAttributesKey("JNIGEN_COMMENT")
    }

    private val INITIAL_REGEX: Multimap<String, String> = ImmutableMultimap.Builder<String, String>()
        .putAll("HEADER_JNI", Arrays.asList("(?:.*include\\s)(<.*>\\s)", "(?:.*include\\s)(\".*\"\\s)"))
        .putAll("CPP_JNI", Arrays.asList("(=|\\+|<(?<!#include <)|>(?!\\s)|=|-|:)"))
        .putAll("OBJECT_JNI", Arrays.asList("(\\w*|\\w*\\*) (?:\\w*|\\w*\\*|\\*\\w*)(?: |)=(?:.*);"))
        .putAll("STRING_JNI", Arrays.asList("(?:.*)(\".*\")"))
        .putAll("NUMBER_JNI", Arrays.asList("(\\d+)(;|,| |\\))"))
        .putAll(
            "CONDITION_JNI", Arrays.asList(
                "\\V+(if|else|for|while|do|switch|case|break|default|continue)\\b",
                "\\V+(else if)",
            )
        )
        .putAll("INCLUDE_JNI", Arrays.asList("(#include)\\b", "(#define)\\b", "(#pragma)\\b"))
        .putAll("DEFAULT_JNI", Arrays.asList("(reinterpret_cast)\\b", "(static_cast)\\b"))
        .putAll(
            "FUNCTION_JNI",
            Arrays.asList(
                "(class)\\b",
                "(static)\\b",
                "(struct)\\b",
                "(void)\\b",
                "(public)\\b",
                "(private)\\b",
                "(return)\\b"
            )
        )
        .putAll(
            "VARIABLE_JNI", Arrays.asList(
                "(?<!(?:\\w|-))(NULL)\\b",
                "(?<!(?:\\w|-))(null)\\b",
                "(?<!(?:\\w|-))(nullptr)\\b",
                "(?<!(?:\\w|-))(auto)\\b",
                "(?<!(?:\\w|-))(jboolean)\\b",
                "(?<!(?:\\w|-))(jchar)\\b",
                "(?<!(?:\\w|-))(jshort)\\b",
                "(?<!(?:\\w|-))(jfloat)\\b",
                "(?<!(?:\\w|-))(jdouble)\\b",
                "(?<!(?:\\w|-))(jsize)\\b",
                "(?<!(?:\\w|-))(jint)\\b",
                "(?<!(?:\\w|-))(jlong)\\b",
                "(?<!(?:\\w|-))(const)\\b",
                "(?<!(?:\\w|-))(jobject)\\b",
                "(?<!(?:\\w|-))(jclass)\\b",
                "(?<!(?:\\w|-))(jthrowable)\\b",
                "(?<!(?:\\w|-))(jstring)\\b",
                "(?<!(?:\\w|-))(jarray)\\b",
                "(?<!(?:\\w|-))(jbooleanArray)\\b",
                "(?<!(?:\\w|-))(jbyteArray)\\b",
                "(?<!(?:\\w|-))(jcharArray)\\b",
                "(?<!(?:\\w|-))(jshortArray)\\b",
                "(?<!(?:\\w|-))(jintArray)\\b",
                "(?<!(?:\\w|-))(jlongArray)\\b",
                "(?<!(?:\\w|-))(jfloatArray)\\b",
                "(?<!(?:\\w|-))(jdoubleArray)\\b",
                "(?<!(?:\\w|-))(jobjectArray)\\b",
                "(?<!(?:\\w|-))(char)\\b",
                "(?<!(?:\\w|-))(signed)\\b",
                "(?<!(?:\\w|-))(unsigned)\\b",
                "(?<!(?:\\w|-))(short)\\b",
                "(?<!(?:\\w|-))(int)\\b",
                "(?<!(?:\\w|-))(long)\\b",
                "(?<!(?:\\w|-))(float)\\b",
                "(?<!(?:\\w|-))(double)\\b",
                "(?<!(?:\\w|-))(wchar_t)\\b",
                "(?<!(?:\\w|-))(bool)\\b",
                "(?<!(?:\\w|-))(JavaVM)\\b",
                "(?<!(?:\\w|-))(jmethodID)\\b",
                "(?<!(?:\\w|-))(JNIEnv)\\b"
            )
        )
        .putAll("COMMENT_JNI", Arrays.asList("(\\/\\/.*)(?:\\n)"))
        .build()

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        val startOffset: Int
        var commentText: String

        if (element.isJniComment() || element.isNativeJniFunctionComment()) {
            commentText = element.text
            startOffset = element.textRange.startOffset
        } else return

        /****************************/
        val textRange = TextRange(startOffset, startOffset + commentText.length)
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(textRange)
            .textAttributes(JNIGEN_COMMENT)
            .create()

        INITIAL_REGEX.asMap().forEach { (token: String, regexs: Collection<String?>) ->
            for (regex in regexs) {
                val matcher = Pattern.compile(regex, MULTILINE).matcher(commentText)
                while (matcher.find()) {
                    if (matcher.groupCount() > 0) {
                        val textRange2 =
                            TextRange(startOffset + matcher.start(1), startOffset + matcher.end(1))
                        val textAttributesKey2 = createTextAttributesKey(token)
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(textRange2)
                            .textAttributes(textAttributesKey2)
                            .create()
                    }
                }

            }
        }
        /****************************/

    }

}