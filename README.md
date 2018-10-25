# LiveDataBus

1、该项目基于LiveData开发的。

2、通过`@BusObserver`注解方法来接收消息，此方法是在主线程中，可以设置tag组、requestCode（当tag相同时，可以用这个来区分）、Sticky标记。并且注解的方法中的参数类型必须和发送的消息类型一致，否则接收不到。

3、可以发送普通消息和Sticky消息，Sticky消息在接收到后就会和普通消息一样了。

4、同一个宿主只能注册一次（重复注册只有第一次有效）、同一个宿主中不能有相同的tag+requestCode（重复了就只有第一个有效）。

5、其它特点请参照LiveData相关介绍。

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

2、在需要接收消息的类的初始化方法（通常为构造函数）中调用`register(this)`方法进行注册宿主（通常在Activity的onCreate()方法中调用，也可以是其它任何类）。当在父类调用`register(this)`方法后，在子类无需再调用了，调用了也行，会自动防重复注册宿主。
```java
    RxBus.register(this);
```

3、在销毁宿主的实例时调用`unregister(this)`方法进行取消注册宿主（通常在Activity的onDestroy()方法中调用）。
```java
    RxBus.unregister(this);
```

4、发送普通消息可以使用`post()`方法。
```java
    RxBus.post(tag);
    RxBus.post(tag, content);
    RxBus.post(tag, code, content);
```

5、发送Sticky消息使用`postSticky()`方法，注意Sticky消息在第一次接收后，就会销毁。和发送普通消息相比，实际上就是延迟了第一次接收消息的时间（用来替代Intent传递数据）。
```java
    RxBus.postSticky(tag, content);
    RxBus.postSticky(tag, code, content);
```

6、接收消息和发送消息是一一对应的。使用`@RxBusSubscribe`注解一个方法，被注解的方法的参数最多只能是1个。只能被public修饰，且不能被static修饰(即只能使用public void修饰)。其中可以设置标签组、请求码、线程(`RxBusThread`)、Sticky标记。
```java
    发送消息：
    RxBus.post("tag");
    
    接收消息：
    @RxBusSubscribe("tag")
    public void test() {
    }
    
   // kotlin
   @RxBusSubscribe("tag")
    fun test() {
    }
```
```java
    发送消息：
    RxBus.post("tag", 123);
    
    接收消息：
    @RxBusSubscribe("tag")
    public void test(int data) {
    }
```
```java
    发送消息：
    RxBus.post("tag", "code", 2.3);
    
    接收消息：
    @RxBusSubscribe(value = "tag", code = "code")
    public void test(double data) {
    }
```
```java
    发送消息：
    RxBus.post("tag1", "1");
    RxBus.post("tag2", "2");
    
    接收消息：
    @RxBusSubscribe(value = {"tag1", "tag2"})
    public void test(String data) {
    }
```
```java
    发送Sticky消息：
    RxBus.postSticky("tag", "1");
    
    接收Sticky消息：
    @RxBusSubscribe(value = "tag", isSticky = true)
    public void test(String data) {
    }
```
```java
    发送Sticky消息：
    RxBus.postSticky("tag", "code", 1);
    
    接收Sticky消息：
    @RxBusSubscribe(value = "tag", code = "code", isSticky = true)
    public void test(int data) {
    }
```

7、Proguard
```java
    -keep class * extends com.like.rxbus.RxBusProxy
    -keep class com.like.rxbus.annotations.**{*;}
    -keepclasseswithmembers class * {
        @com.like.rxbus.annotations.RxBusSubscribe <methods>;
    }
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
