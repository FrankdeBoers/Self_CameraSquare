**如何自定义Camera分辨率？**

开发中遇到需求，改变相机预览分辨率以适配屏幕。当前手机屏幕是720*720（1：1），非正常的16：9屏幕，这就要求Camera需要更改预览分辨率和图片保存分辨率，以适配屏幕。  这也是当前很火的全面屏（18：9）的相机开发会遇到的问题。

针对这个需求，通过查阅资料发现，可以有两种实现方式：

>  1. 使用遮罩，盖住一部分预览界面。
>  2. 使用GLSurfaceView实现预览，在onDrawFrame()中对SurfaceView的尺寸做裁剪。
>  3. 更改hal层config.ftbl.common_raw.h文件，由驱动层处理。

以MIUI9 拥有的方形预览为例，接下来分析各种方法的实现：

![MIUI9的方形预览](https://img-blog.csdn.net/20180509140844332?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0ZyYWtpZV9Ld29r/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

##1.使用遮罩，盖住一部分预览界面。
这是网上看到的一种方法，就是使用黑色的控件或其他颜色控件，遮盖住一部分预览界面，实现加的方形预览。
我没有去实践，这种方法灵活性不高，不能满足我的需求，因为我是要求全屏铺满720*720的，不能再放下其他控件了。

> 优点：最容易实现，代码量最小。
> 缺点：灵活性差；拿到onPictureTaken()的byte[] data后，还要对data进行软裁剪，这个裁剪是在CPU里面做的，资源消耗较大。

##2.使用GLSurfaceView实现预览，在onDrawFrame()中对SurfaceView的尺寸做裁剪。
这种方法是在学习OpenGL的时候发现的，主要步骤如下：
实例化GLSurfaceView，并绑定到Camera上面。 
实现Render渲染，在onDrawFrame中对画面进行裁剪。
最终效果：
<img src="https://github.com/FrankdeBoers/Self_CameraSquare/blob/master/screenshot/squarecamera.png" width="50%" height="50%" />

