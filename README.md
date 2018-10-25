# LiveDataBus

1、该项目基于LiveData开发的。
    ①、LiveData可以感知被绑定的组件的生命周期，会在组件销毁时，自动取消注册。
    ②、组件在不活跃状态时，不会收到数据。
    ③、当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。

2、通过`@BusObserver`注解方法来接收消息。
    ①、此注解中可以设置tag、requestCode、Sticky三个参数。
    ②、当tag相同时，可以用requestCode来区分。requestCode相当于常用的请求码。
    ③、sticky只是针对`@BusObserver`注解的接收消息的方法。发送消息时不区分粘性或者非粘性消息。sticky为true时表示会收到注册之前发送过的最新一条消息。
    ④、此方法是在主线程中调用的。
    ⑤、此方法只能使用public void修饰（kotlin中只能使用fun修饰），且参数只能是1个。
    ⑥、必须要tag、requestCode、参数类型，这三项与发送消息完全一致，才能接收到消息。

3、同一个宿主只能注册一次（重复注册只有第一次有效）、同一个宿主中不能有相同的tag+requestCode（重复了就只有第一个有效）。

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
        compile 'com.github.like5188:RxBus:版本号'
        annotationProcessor 'com.github.like5188.RxBus:rxbus-compiler:版本号' // java
        // kapt 'com.github.like5188.RxBus:rxbus-compiler:版本号' // kotlin
    }
```

2、在需要接收消息的类的初始化方法（通常为构造函数）中调用`register`方法进行注册宿主。当在父类调用`register`方法后，在子类中无需再调用。
```java
    LiveDataBus.register(host: Any)
    LiveDataBus.register(host: Any, owner: LifecycleOwner)

    // kotlin中还可使用下面的扩展方法注册
    liveDataBusRegister()
    liveDataBusRegister(owner: LifecycleOwner)
```

3、发送消息。
```java
    LiveDataBus.post(tag: String, t: T)
    LiveDataBus.post(tag: String, requestCode: String, t: T)
```

4、接收消息。
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

5、Proguard
```java
    -keep class * extends com.like.livedatabus.Bridge
    -keep class com.like.livedatabus_annotations.**{*;}
```

# License
```xml
    Copyright 2017 like5188
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
