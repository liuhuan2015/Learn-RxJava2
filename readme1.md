>学习目标文章[RxJava2.0——从放弃到入门](https://www.jianshu.com/p/cd3557b1a474)

### 1 . RxJava 2.x和RxJava 1.x的关系
RxJava 2.x和RxJava 1.x的核心思想都是观察者模式，只不过RxJava 2.x在RxJava 1.x的基础上对一些方法进行了优化，方便开发者更好地的理解其编程思想；同时RxJava 2.x增加了一部分新的方法来解决1.x中存在的问题，比如背压等。
#### 1.1 观察者模式
举例：读者订阅了连载小说，当小说有了新的连载更新后会主动推送给读者，读者不用时刻盯着小说连载。这就是观察者模式。

### 2 . RxJava 2.x的异步和链式编程

**RxJava最常用的写法：异步 + 链式编程**

RxJava是支持异步的，通过Scheduler（英文名为调度器）来控制线程，当没有设置Scheduler时，RxJava遵循哪个线程产生就在哪个线程消费的原则，也就是说线程不会发生变化，始终在同一个。

一般使用RxJava的场景都是后台执行，前台调用，这就需要使用Scheduler来进行线程的调度了。

observeOn(AndroidSchedulers.mainThread())：事件回调的线程在主线程。

subscribeOn(Schedulers.io())：事件执行的线程在io线程（子线程）；这里也可以使用Schedulers.newThread()，但是io线程可以重用空闲的线程，因此多数情况下io()比newThread()更有效率。

### 3 . RxJava的应用场景

**所有用到异步的地方，都可以使用RxJava来完成**

RxJava的好处：“随着程序逻辑变得越来越复杂，RxJava依然能够保持简洁”。

比如：依次加载十张图片（加载图片是耗时过程），其中第六张延时3秒加载，第七张复制到sd卡中，第八张要上传至网络。如果是使用Handler，必然是各种嵌套，逻辑复杂得再看一眼都难受，但是如果是使用RxJava呢——不会有任何嵌套，逻辑依然简洁。







