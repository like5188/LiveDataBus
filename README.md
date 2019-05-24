#### 最新版本

模块|livedatabus-api|livedatabus_compiler
---|---|---
最新版本|[![Download](https://jitpack.io/v/like5188/LiveDataBus.svg)](https://jitpack.io/#like5188/LiveDataBus)|[![Download](https://jitpack.io/v/like5188/LiveDataBus.svg)](https://jitpack.io/#like5188/LiveDataBus)

## 功能介绍
1、该项目基于LiveData开发的。

    ①、避免内存泄漏。观察者被绑定到组件的生命周期上，当被绑定的组件销毁（destroy）时，观察者会立刻自动清理自身的数据，使用者不用显示调用反注册方法。并且绑定生命周期后，组件在不活跃状态时，不会收到数据。当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。
    ②、解决Configuration Change问题。在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。
    ③、UI和实时数据保持一致。因为LiveData采用的是观察者模式，这样一来就可以在数据发生改变时获得通知，更新UI。

2、通过`@BusObserver`注解方法来接收消息。

    ①、此注解中可以设置tag、requestCode、Sticky三个参数。
    ②、当tag相同时，可以用requestCode来区分。
    ③、sticky只是针对`@BusObserver`注解的接收消息的方法。发送消息时不区分粘性或者非粘性消息。sticky为true时表示会收到注册之前发送过的最新一条消息。
    ④、此方法是在主线程中调用的。
    ⑤、此方法只能使用public void修饰（kotlin中只能使用fun修饰），且参数只能是1个。
    ⑥、必须要tag、requestCode、参数类型，这三项与发送的消息完全一致，才能接收到消息。

3、同一个宿主只能注册一次（重复注册只有第一次有效）、同一个宿主中不能有相同的tag+requestCode（重复了就只有第一个有效）。

4、V2.0.0 升级解决的问题如下：
```java
    (1). 消息难以溯源

    有时候我们在阅读代码的过程中，找到一个订阅消息的地方，想要看看是谁发送了这个消息，这个时候往往只能通过查找消息的方式去“溯源”。导致我们在阅读代码，梳理逻辑的过程不太连贯，有种被割裂的感觉。

    (2). 消息发送比较随意，没有强制的约束

    消息总线在发送消息的时候一般没有强制的约束。无论是EventBus、RxBus或是LiveDataBus，在发送消息的时候既没有对消息进行检查，也没有对发送调用进行约束。这种不规范性在特定的时刻，甚至会带来灾难性的后果。比如订阅方订阅了一个名为login_success的消息，编写发送消息的是一个比较随意的程序员，没有把这个消息定义成全局变量，而是定义了一个临时变量String发送这个消息。不幸的是，他把消息名称login_success拼写成了login_seccess。这样的话，订阅方永远接收不到登录成功的消息，而且这个错误也很难被发现。

    (3). 消息由组件自己定义

    以前我们在使用消息总线时，喜欢把所有的消息都定义到一个公共的Java文件里面。但是组件化如果也采用这种方案的话，一旦某个组件的消息发生变动，都会去修改这个Java文件。所以我们希望由组件自己来定义和维护消息定义文件。

    (4). 区分不同组件定义的同名消息

    如果消息由组件定义和维护，那么有可能不同组件定义了重名的消息，消息总线框架需要能够区分这种消息。
```

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        compile 'com.github.like5188.LiveDataBus:livedatabus:版本号'
        annotationProcessor 'com.github.like5188.LiveDataBus:livedatabus_compiler:版本号' // java
        // kapt 'com.github.like5188.LiveDataBus:livedatabus_compiler:版本号' // kotlin
    }
```

2、在需要接收消息的类的初始化方法（通常为构造函数）中调用`register`方法进行注册宿主。当在父类调用`register`方法后，在子类中无需再调用。
```java
    LiveDataBus.register(host: Any, owner: LifecycleOwner? = null)
    // 当注册时参数owner为null时，不会自动关联生命周期，必须显示调用下面的方法取消注册；不为null时会自动关联生命周期，不用调用取消注册的方法。
    LiveDataBus.unregister(tag: String, requestCode: String = "")

    // kotlin
    liveDataBusRegister(owner: LifecycleOwner? = null)
    // 当注册时参数owner为null时，不会自动关联生命周期，必须显示调用下面的方法取消注册；不为null时会自动关联生命周期，不用调用取消注册的方法。
    liveDataBusUnRegister(tag: String, requestCode: String = "")
```

3、发送消息。
```java
    LiveDataBus.post(tag: String)
    LiveDataBus.post(tag: String, t: T)
    LiveDataBus.post(tag: String, requestCode: String, t: T)
```

4、接收消息与发送消息一一对应。
```java
    发送消息：
    LiveDataBus.post(tag: String)
    
    接收消息：
    // java
    @BusObserver("tag")
    public void test() {
    }
    
   // kotlin
   @BusObserver(["tag"])
    fun test() {
    }
```
```java
    发送消息：
    LiveDataBus.post(tag: String, t: T)

    接收消息：
    // java
    @BusObserver("tag")
    public void test(T t) {
    }

   // kotlin
   @BusObserver(["tag"])
    fun test(t: T) {
    }
```
```java
    发送消息：
    LiveDataBus.post(tag: String, requestCode: String, t: T)
    
    接收消息：
    // java
    @BusObserver(value = "tag", requestCode = "requestCode")
    public void test(T t) {
    }

   // kotlin
   @BusObserver(["tag"], requestCode = "requestCode")
    fun test(t: T) {
    }
```