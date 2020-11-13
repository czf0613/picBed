# picBed

A cheap self-made pic bed, including user interfaces and a small script which can help software like Typora to upload images automatically.
[网站戳这里](https://pic-bed.xyz)。API文档可以从上方的“API文档”的markdown文件中获取。

如果不出意外的话，这篇readme文档里面的图片服务，就是我自己提供的啦！

首先来说一下开发这个软件的初衷吧。本质上来说，就是一个图床的功能。何为图床呢？不知大家有没有遇到过这样一个问题，在编写markdown文件的时候，我们并不能直接把图片的二进制文件插入，我们需要引用一个文件地址，然后再由markdown的编辑器自动将这个图片显示出来。这时我们难免遇到一个问题，如果我们需要把markdown文件拷给别人或者发布到GitHub上去的时候，这些图片都会失效，或者……你不得不把图片和markdown文件放到一起去然后用相对地址来引用它，这当然是相当不方便的。

当然，图床这个概念早就诞生了，但早期一直是由一些外国公司运营的，比如说SM.MS之类的服务，或者还有一个比较流行的叫做picGo的图床配置工具，但是……这些服务都不便宜（因为要用到对象存储服务），而且因为那些服务器都在国外，国内使用起来很慢。所以，咱们干脆自己动手，丰衣足食吧。我们就以SM.MS为模板，来造一个图片服务器吧。

[TOC]



## 1，技术路线

当然，这一回肯定还是用Springboot开发比较稳定，毕竟业内成熟的技术，要处理起来还是比较容易的，至少……坑可以少踩一点（其实坑一点也没少踩……咳咳咳）不过，当大家看到GitHub显示的代码语言组成占比的时候，应该已经发现有点不对劲了吧，是的你没看错，这是一个纯Kotlin开发的项目。

用纯Kotlin来开发Springboot项目，确实是疯掉了，而且……基本上可能很多东西还是在网上都找不到解决方案的，但u1s1，用Kotlin开发服务端项目的时候，似乎远没有在安卓端那么香，至少我是这么觉得的。

为什么这么说呢，首先就是可怜的生态问题，服务端项目绝大多数都是用Java构建的，然而Kotlin和Java的互操作性其实根本达不到100%，所以其实，我们会被各种各样的臭bug困扰。

再者就是，Kotlin最牛逼的两个功能：协程和空安全，在Springboot里面没有发挥的余地。首先就是，web项目最终的承载者是一个容器，比如说Tomcat或者Undertow之类的，那么，这个时候，协程的上下文就变成了一个很玄学的问题，所以，协程的发起就不得不使用GlobalScope.launch来处理，然而，这个东西所导致的泄漏问题和输出流意外关闭的问题，就非常尴尬了。再者，因为Java对象并不区分nullable和not null的问题，除非用装饰器来标记一下，然而……很多Spring源代码里面就没有考虑这个问题，这个就导致我们有的时候用Kotlin中的Obj，或者Obj?起不到空安全的作用，所以……空安全这个作用也没了。

最后的一个问题就是……编译是真滴慢啊，Kotlin的编译确实要比Java慢不少啊，这个项目也就几千行代码，编译就要7-8秒。

所以，一言以蔽之，这回用Kotlin写服务端确实是一次疯狂的尝试，算是长长见识了，不过，我还是更建议用Kotlin和Java的混合项目会更好。



## 2，前期准备

首先你需要一台有比较大硬盘空间的服务器，如果没有服务器的话也不要紧，用花生壳给自己的电脑动态分配一个域名或者公网IP就够了，然后就可以利用自己的电脑搭建这个服务。

接着就是一个数据库，数据库要尽可能保证安全，因为上传后文件的地址就存放在数据库里（请注意，不要把文件用BLOB的方式直接写入数据库，这是一个不太聪明的做法）。推荐购买云数据库服务，而且最好和服务器在同一个片区，这样就可以通过云服务提供商的内网直接访问，速度就快多了。

有条件的话，准备一下SSL证书，避免你的数据在网络上裸奔，免费的证书很容易就可以申请的。如果有条件的话，甚至还可以给服务器里保存的文件也加密一下，比如AES之类的，不过本次产品里就没有使用这个了，因为服务器性能比较差，过多的加密会导致性能下降。

最后一个就是带宽问题了，如果服务器是1M的小水管的话，就不建议部署这样的项目了，太慢了……但其实也并不是不行，有一个技术叫做CDN加速，用了CDN之后就可以解决加载慢的问题了。至于CDN的提供商是谁，这个就不方便说了，因为国内这个东西管的严，如果不备案的网站弄了是会被ban的。



## 3，要做什么

首先我们先进行一下分析，整个图床服务分为几个部分，一个就是文件管理，一个是向前端提供数据的接口，还有一个是管理文件索引的数据库。那么我们一步一步来说我们要干嘛。

### 1，文件管理

文件管理其实就是文件的读，写操作。对于Java而言，文件的读写往往是依靠流来实现的，这个其实并没有什么问题，但是如果访问量大起来的时候，传统的IO模型很快就会遇到性能瓶颈，因为在进行IO操作的时候，这个线程实际上是被阻塞的，那将会导致资源的浪费，所以我们需要使用到NIO模型。

如果仅仅只是使用NIO模型的话，其实这个问题也解决了，但……我们这回用的是Kotlin啊，这哪来的NIO？（其实也有，就是Java的NIO而已）不如……我们来用用协程吧，似乎也可以很轻易地干到这件事情呢

```kotlin
suspend fun writeFileTo(place: String, multipartFile: MultipartFile) {
    withContext(Dispatchers.IO) {
        val directory = place.substringBeforeLast("/")
        val dir=File(directory)
        if(!dir.exists())
            dir.mkdirs()

        val file = File(place)
        multipartFile.transferTo(file)
    }
}
```

解决完读写的问题，别忘了我们还有一个守护进程，专门回收没用的文件，长期不用的token等等。

### 2，向前端提供数据

这个请前往阅读我们的技术文档（目前还在更新中，日后会放出来）

### 3，文件索引库

一般而言，我们是不可以把文件直接写入数据库的，这是一个非常不好的操作，因为这个会极大加重数据库的负担，所以我们还是使用分离的办法，数据库只记录文件的绝对地址（或者是URL也行，总之能定位一个文件即可），然后剩下的交给专门的文件管理器来做。

同时，这样还有一个好处，文件的存储和文件的记录是解耦的，那么就意味着，我可以随便更改文件存储的手段，比如说我现在是直接存储在服务器上的，以后也可以轻易地改成OSS储存，或者是换成分布式存储等等等等，你只需要更改一个模块即可，因为它们彼此没有耦合。



## 4，如何使用

需要安装的东西有一点复杂，不过对于大家来讲应该不会太难，日后关于如何使用的问题可能还会更改

### 1，下载Typora

这个是很好用的markdown编辑器，地址在这里：

```
https://www.typora.io
```

### 2，下载python

如果已经安装了python的话，请忽略这一步，直接看requests等模块的安装即可。

由于Typora并不可以直接兼容我做的这个图床，但是Typora给出了一个命令行工具上传图片的选项，所以我们只需要弄出来一个命令行工具就OK了。鉴于各个平台的通用性，最终还是选择了python来构建命令行工具，毕竟嘛……shell脚本不方便在Windows运行，Jar文件又需要太多运行环境，ruby脚本也在Windows端不方便使用，所以就还是使用python吧……

建议选择python3，版本选择64bit即可（主要我担心会有玄学错误，32位就还是算了吧），下载地址如下：（分别是Windows，macOS，Linux平台的）

```
https://www.python.org/ftp/python/3.8.6/python-3.8.6-amd64.exe
https://www.python.org/ftp/python/3.8.6/python-3.8.6-macosx10.9.pkg
https://www.python.org/ftp/python/3.8.6/Python-3.8.6.tgz
```

注意，Windows版安装时请务必选择add to path的选项

接下来，安装几个必须的模块，否则不能正确地运行脚本。第一个是requests模块，第二个是构造请求体的一个模块，在cmd窗口中依次输入以下命令即可：

```
pip install requests
pip install requests_toolbelt
```

注意，如果在macOS或者Linux系统中，可能需要使用pip3命令而不是pip命令

### 3，下载脚本

无需克隆整个版本库，因为整个版本库还包含了服务端的源代码，非常大，可以在release中找到最新的python脚本，名字叫main.py

我们只需要修改这几个部分即可：

```python
url = 'https://pic-bed.xyz/api/upload'

part = MultipartEncoder(fields = {'userId': '1', 'file': ('xxx.png', open(i, 'rb'), 'application/octet-stream')})
head = {'token': 'c1c5719e761241c3ab3e1627286b9647', 'content-type': part.content_type}
```

如果在您自己的服务器上部署了文件服务器后台，那么这个域名需要随之改动，注意是否配置SSL。

首先将userId换成您自己的用户id，注意这个是字符串类型，不能填int进来

第二个，把其中的token换成服务器颁发的token即可正常使用

用户的注册和token的管理功能正在逐步上线中，目前可以暂时使用userId=1，token=c1c5719e761241c3ab3e1627286b9647的配置项进行试用，日后会有管理后台供大家使用。

### 4，配置Typora

#### 1，打开Typora的偏好设置

点击左上角，选择偏好设置即可打开，打开后应该会看到如下页面

![image-20201004204334176](https://pic-bed.xyz/res/userFiles/czf/11.png)

#### 2，左侧边栏点击图像

![image-20201004204453865](https://pic-bed.xyz/res/userFiles/czf/12.png)

然后，将插入图片时这个部分的选项改成如图所示。接下来，更改上传服务设定，选择custom command选项，然后输入自定义的命令，格式如下：

```shell
python $python脚本的绝对地址，包含后缀名
```

注意，在某些系统上，可能需要使用python3命令而不是python命令。

修改完成后，点击“验证图片上传选项”，如果您成功看到以下提示信息，那就说明大功告成了

![image-20201004204853870](https://pic-bed.xyz/res/userFiles/czf/15.png)

#### 3，部分疑难解答

##### 1，python模块和Typora下载很慢

这个是国内特色，请各位自行解决……

##### 2，python模块冲突

如果您的电脑已经安装了python环境，请不要重复安装，保留一个即可

##### 3，Typora执行结果为Fail

首先检查您的网络连接或者防火墙，可能python程序被禁止联网了。或者，还有极罕见的可能为触发了跨域的拦截，因为服务器部署的位置在上海，如果国外的用户访问有概率会遇到此问题。

另外，也请检查是否误修改了脚本中的某些代码。
