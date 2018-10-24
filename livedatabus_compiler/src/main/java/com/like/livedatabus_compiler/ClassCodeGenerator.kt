package com.like.livedatabus_compiler

import com.like.livedatabus_annotations.BusObserver
import com.squareup.javapoet.*
import java.io.IOException
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
public class MainActivity_Proxy extends Bridge {
@Override
protected void autoGenerate(@NotNull LifecycleOwner owner) {
observe(owner, tag1, tag2, isSticky, new Observer<String>() {
@Override
public void onChanged(@Nullable String s) {
// 调用@BusObserver注解的接收数据的方法
}
});
}
}
 */
class ClassCodeGenerator {
    companion object {
        private const val CLASS_UNIFORM_MARK = "_Proxy"
        // 因为java工程中没有下面这些类(Android中的类)，所以只能采用ClassName的方式。
        private val BRIDGE = ClassName.get("com.like.livedatabus", "Bridge")
        private val OBSERVER = ClassName.get("android.arch.lifecycle", "Observer")
        private val LIFECYCLE_OWNER = ClassName.get("android.arch.lifecycle", "LifecycleOwner")
    }

    private var mPackageName = ""// 生成的类的包名
    private var mOwnerClassName: ClassName? = null// 宿主的类名
    private val mMethodInfoList = mutableSetOf<MethodInfo>()// 类中的所有方法

    fun create() {
        if (mMethodInfoList.isEmpty() || mPackageName.isEmpty() || mOwnerClassName == null) {
            return
        }
        // 创建包名及类的注释
        val javaFile = JavaFile.builder(mPackageName, createClass())
                .addFileComment(" This codes are generated automatically by LiveDataBus. Do not modify!")// 类的注释
                .build()

        try {
            javaFile.writeTo(ProcessUtils.filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 创建类
     * public class MainActivity_Proxy extends Bridge {}
     */
    private fun createClass(): TypeSpec =
            TypeSpec.classBuilder(mOwnerClassName?.simpleName() + CLASS_UNIFORM_MARK)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(BRIDGE)
                    .addMethod(createMethod())
                    .build()

    /**
     * 创建autoGenerate方法
     * @Override
     * protected void autoGenerate(@NotNull LifecycleOwner owner) {}
     */
    private fun createMethod(): MethodSpec {
        val builder = MethodSpec.methodBuilder("autoGenerate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(LIFECYCLE_OWNER, "owner", Modifier.FINAL)
                .addAnnotation(Override::class.java)
        for (binder in mMethodInfoList) {
            builder.addCode(createMethodCodeBlock(binder))
        }
        return builder.build()
    }

    /**
     * 创建autoGenerate方法中调用的方法
     * observe(owner, tag1, tag2, isSticky, observer)
     */
    private fun createMethodCodeBlock(methodInfo: MethodInfo): CodeBlock {
        val builder = CodeBlock.builder()
        val tag1 = methodInfo.tag1
        tag1?.forEach {
            val tag2 = methodInfo.tag2
            val isSticky = methodInfo.isSticky

            val codeBlockBuilder = CodeBlock.builder()
            codeBlockBuilder.addStatement("observe(owner\n,\$S\n,\$S\n,\$L\n,\$L)", it, tag2, isSticky, createObserverParam(methodInfo))
            builder.add(codeBlockBuilder.build())
        }
        return builder.build()
    }

    /**
     * 创建第四个参数observer，是一个匿名内部类。
    new Observer<String>() {
    @Override
    public void onChanged(@Nullable String s) {
    // 调用@BusObserver注解的接收数据的方法((MainActivity) owner).observer1(t);
    }
    }
     */
    private fun createObserverParam(methodInfo: MethodInfo): TypeSpec {
        // 获取onChanged方法的参数类型
        val paramType = methodInfo.paramType
        var typeName: TypeName
        if (paramType!!.kind.isPrimitive) {
            typeName = TypeName.get(paramType)
            if (!typeName.isBoxedPrimitive)
                typeName = typeName.box()
        } else
            typeName = ClassName.get(paramType)
        print("onChanged方法的参数类型：$typeName")

        // 创建onChanged方法
        val methodBuilder = MethodSpec.methodBuilder("onChanged")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
        methodBuilder.addParameter(typeName, "t")
        // ((MainActivity) owner).observer1(t);
        methodBuilder.addStatement("((MainActivity) owner)." + methodInfo.methodName + "(t)")
        // 创建匿名内部类
        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(OBSERVER, typeName))
                .addMethod(methodBuilder.build())
                .build()
    }

    /**
     * 添加元素，用于生成类
     */
    fun addElement(element: Element) {
        if (mOwnerClassName == null) {
            mOwnerClassName = ClassName.get(element.enclosingElement as TypeElement)// getEnclosingElement()所在类的对象信息
            mPackageName = mOwnerClassName!!.packageName()
        }

        val busObserverAnnotationClass = BusObserver::class.java
        val methodInfo = MethodInfo()
        methodInfo.methodName = element.simpleName.toString()
        methodInfo.tag1 = element.getAnnotation(busObserverAnnotationClass).value
        methodInfo.tag2 = element.getAnnotation(busObserverAnnotationClass).tag2
        methodInfo.isSticky = element.getAnnotation(busObserverAnnotationClass).isSticky

        val executableElement = element as ExecutableElement
        if (executableElement.parameters.size == 1) {
            val ve = executableElement.parameters[0]
            methodInfo.paramType = ve.asType()
        }
        mMethodInfoList.add(methodInfo)
    }
}