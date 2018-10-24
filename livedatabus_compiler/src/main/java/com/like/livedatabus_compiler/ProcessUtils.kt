package com.like.livedatabus_compiler

import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

import javax.lang.model.element.ElementKind.CLASS
import javax.lang.model.element.ElementKind.METHOD
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC

/**
 * 用于验证目标类及方法的正确性
 */
object ProcessUtils {
    var typeUtils: Types? = null// 用来处理TypeMirror的工具
    var elementUtils: Elements? = null// 用来处理Element的工具
    var filer: Filer? = null// 用来生成我们需要的.java文件的工具
    var messager: Messager? = null// 提供给注解处理器一个报告错误、警告以及提示信息的途径

    /**
     * 判断该元素的上层元素是否符合目标元素的上层元素
     * <p>
     * 判断宿主类是否为类，而且是否为public修饰
     * 然后判断包名，android.和java.开头的不行
     *
     * @param element
     * @return
     */
    fun verifyEncloseingClass(element: Element): Boolean {
        val encloseingElement = element.enclosingElement as TypeElement
        if (encloseingElement.kind != CLASS) {
            error(element, "%s 不属于CLASS类型", element.simpleName.toString())
            return false
        }
        if (!encloseingElement.modifiers.contains(PUBLIC)) {
            error(element, "%s 类必须被public修饰", element.simpleName.toString())
            return false
        }
        val qualifiedName = encloseingElement.qualifiedName.toString()
        if (qualifiedName.startsWith("android.") || qualifiedName.startsWith("java.")) {
            error(element, "%s 类的名称不能以`android.`或者`java.`开头", element.simpleName.toString())
            return false
        }
        return true
    }

    /**
     * 判断是否为目标方法
     * <p>
     * 元素类型必须为method，必须public修饰，不能为static。方法的参数最多只能是1个
     *
     * @param element
     * @return
     */
    fun verifyMethod(element: Element): Boolean {
        if (element.kind != METHOD) {
            error(element, "%s 不属于METHOD类型", element.simpleName.toString())
            return false
        }

        if (!element.modifiers.contains(PUBLIC) || element.modifiers.contains(STATIC)) {
            error(element, "%s 方法必须被public修饰，且不能为static", element.simpleName.toString())
            return false
        }

        val executableElement = element as ExecutableElement
        val size = executableElement.parameters.size
        if (size > 1) {
            error(executableElement, "%s 方法的参数最多只能有1个", executableElement.simpleName.toString())
            return false
        }

        return true
    }

    fun error(element: Element, format: String, vararg args: Any) {
        var format = format
        if (args.isNotEmpty())
            format = String.format(format, *args)
        ProcessUtils.messager!!.printMessage(Diagnostic.Kind.ERROR, format, element)
    }

}