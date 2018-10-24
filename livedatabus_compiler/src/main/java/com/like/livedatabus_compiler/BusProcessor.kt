package com.like.livedatabus_compiler

import com.google.auto.common.SuperficialValidation
import com.google.auto.service.AutoService
import com.like.livedatabus_annotations.BusObserver
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * RxBus注解处理器。每一个注解处理器类都必须有一个空的构造函数，默认不写就行;
 */
@AutoService(Processor::class)
class BusProcessor : AbstractProcessor() {
    companion object {
        private val CODE_BUILDER_MAP = mutableMapOf<TypeElement, ClassCodeGenerator>()
    }

    /**
     * init()方法会被注解处理工具调用，并输入ProcessingEnviroment参数。
     * ProcessingEnviroment提供很多有用的工具类Elements, Types 和 Filer
     *
     * @param processingEnv 提供给 processor 用来访问工具框架的环境
     */
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        ProcessUtils.typeUtils = processingEnv.typeUtils
        ProcessUtils.elementUtils = processingEnv.elementUtils
        ProcessUtils.filer = processingEnv.filer
        ProcessUtils.messager = processingEnv.messager
    }

    /**
     * 这相当于每个处理器的主函数main()，你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     * 输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素
     *
     * @param annotations 请求处理的注解类型
     * @param roundEnv    有关当前和以前的信息环境
     * @return 如果返回 true，则这些注解已声明并且不要求后续 BusProcessor 处理它们；
     * 如果返回 false，则这些注解未声明并且可能要求后续 BusProcessor 处理它们
     */
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // 返回使用给定注解类型的元素
        val elements = roundEnv.getElementsAnnotatedWith(BusObserver::class.java) as Set<Element>
        for (element in elements) {
            try {
                // 验证有效性
                if (!SuperficialValidation.validateElement(element))
                    continue
                if (!ProcessUtils.verifyEncloseingClass(element) || !ProcessUtils.verifyMethod(element))
                    continue
                // 添加类(包含有被BusObserver注解的方法的类)
                val enclosingElement = element.enclosingElement as TypeElement// 获取直接上级
                var classCodeGenerator = CODE_BUILDER_MAP[enclosingElement]
                if (classCodeGenerator == null) {
                    classCodeGenerator = ClassCodeGenerator()
                    CODE_BUILDER_MAP[enclosingElement] = classCodeGenerator
                }
                // 添加类下面的方法(被BusObserver注解的方法)
                classCodeGenerator.addElement(element)
            } catch (e: Exception) {
                e.printStackTrace()
                ProcessUtils.error(element, e.message ?: "")
            }
        }

        // 生成代码
        CODE_BUILDER_MAP.forEach { typeElement, classCodeGenerator ->
            classCodeGenerator.create()
        }
        return true
    }

    /**
     * 这里必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称
     *
     * @return 注解器所支持的注解类型集合，如果没有这样的类型，则返回一个空集合
     */
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(BusObserver::class.java.canonicalName)
    }

    /**
     * 指定使用的Java版本，通常这里返回SourceVersion.latestSupported()，默认返回SourceVersion.RELEASE_6
     *
     * @return 使用的Java版本
     */
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

}