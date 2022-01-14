package com.github.arissa34.jnigenplugin.annotator

import com.github.arissa34.jnigenplugin.extention.getCommentText
import com.github.arissa34.jnigenplugin.extention.isJniComment
import com.github.arissa34.jnigenplugin.extention.isNativeJniFunctionComment
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.util.*
import java.util.regex.Pattern


class JniGenJavaAnnotator : Annotator {

    companion object{
        @JvmStatic val JNIGEN_COMMENT: TextAttributesKey =
            createTextAttributesKey("JNIGEN_COMMENT")
    }

    private val INITIAL_REGEX: Multimap<String, String> = ImmutableMultimap.Builder<String, String>()
        .putAll("HEADER_JNI", Arrays.asList("(?:.*include\\s)(<.*>\\s)", "(?:.*include\\s)(\".*\"\\s)"))
        .putAll("CPP_JNI", Arrays.asList("(=|\\+|<(?<!#include <)|>(?!\\s)|=|-|:)"))
        .putAll("OBJECT_JNI", Arrays.asList("(\\w*|\\w*\\*) (?:\\w*|\\w*\\*|\\*\\w*)(?: |)=(?:.*);"))
        .putAll("STRING_JNI", Arrays.asList("(?:.*)(\".*\")"))
        .putAll("NUMBER_JNI", Arrays.asList("(\\d+)(;|,| |\\))"))
        .putAll("COMMENT_JNI", Arrays.asList("(\\/\\/.*)(?:\\n)"))
        .build()

    private val INITIAL_TOKENS: Multimap<String, String> = ImmutableMultimap.Builder<String, String>()
        .putAll("INCLUDE_JNI", Arrays.asList("#include"))
        .putAll("DEFAULT_JNI", Arrays.asList("reinterpret_cast", "static_cast"))
        .putAll("FUNCTION_JNI", Arrays.asList("class", "void", "public", "private", "return"))
        .putAll(
            "VARIABLE_JNI", Arrays.asList(
                "NULL", "null", "nullptr", "auto", "jboolean", "jchar",
                "jshort", "jfloat", "jdouble", "jsize", "jint",
                "jlong", "const", "jobject", "jclass", "jthrowable",
                "jstring", "jarray", "jbooleanArray", "jbyteArray", "jcharArray",
                "jshortArray", "jintArray", "jlongArray", "jfloatArray", "jdoubleArray",
                "jobjectArray", "char", "signed", "unsigned", "short", "int",
                "long", "float", "double", "wchar_t"
            )
        )
        .putAll(
            "CONDITION_JNI", Arrays.asList(
                "if", "else", "else if", "while", "do", "switch",
                "case", "break", "default", "continue", "for"
            )
        )
        .build()

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        var startOffset: Int
        var commentText: String

        if(element.isJniComment()){
            commentText = element.text
            startOffset = element.textRange.startOffset
        }else if(element.isNativeJniFunctionComment()){
            val commentPsi: PsiElement = element.getCommentText() ?: return
            commentText = commentPsi.text
            startOffset = commentPsi.textRange.startOffset
        }else return

        /****************************/
        val textRange = TextRange(startOffset, startOffset + commentText.length)
        //val textAttributesKey = TextAttributesKey.createTextAttributesKey("JNIGEN_COMMENT")
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(textRange)
            .textAttributes(JNIGEN_COMMENT)
            .create()


        /****************************/
        INITIAL_REGEX.asMap().forEach { (token: String?, regexs: Collection<String?>) ->
            for (regex in regexs) {
                val matcher = Pattern.compile(regex).matcher(commentText)
                while (matcher.find()) {
                    if (matcher.groupCount() > 0) {
                        val textRange2 =
                            TextRange(startOffset + matcher.start(1), startOffset + matcher.end(1))
                        val textAttributesKey2 = TextAttributesKey.createTextAttributesKey(token!!)
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(textRange2)
                            .textAttributes(textAttributesKey2)
                            .create()
                    }
                }
            }
        }

        /****************************/
        val regexSeparator = "\\s+|\\(|\\)|-|<|>|::|\\{|}|,|;"
        INITIAL_TOKENS.asMap().forEach { (token: String?, words: Collection<String>) ->
            for (word in words) {
                val tokenList: Array<String> = commentText.split(regexSeparator.toRegex()).toTypedArray()
                for (t in tokenList) {
                    if (t != word) continue
                    val matcher =Pattern.compile("$word\\b").matcher(commentText)
                    while (matcher.find()) {
                        val textRange2 =
                            TextRange(startOffset + matcher.start(), startOffset + matcher.end())
                        val textAttributesKey2 = TextAttributesKey.createTextAttributesKey(token!!)
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(textRange2)
                            .textAttributes(textAttributesKey2)
                            .create()
                    }
                }
            }
        }
    }
}