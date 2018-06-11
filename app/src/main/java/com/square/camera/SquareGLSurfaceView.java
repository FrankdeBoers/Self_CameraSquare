package com.square.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by GHC on 2017/6/12.
 */

public class SquareGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "SquareGLSurfaceView";
    // 最重要的是这个Render渲染线程，@2.2会分析这个线程
    private SquareRenderer mRenderer;

    public SquareGLSurfaceView(Context context) {
        super(context);
    }

    public void initGLSView(SquareCameraManager camera, boolean isPreviewStarted) {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        Log.d(TAG, "initGLSView >> width:" + width);

        // 初始化GLSView最重要的工作：
        // @Step1 设置使用的OpenGL ES的版本，一般都是设置为2
        setEGLContextClientVersion(2);
        // @Step2 设置当前GLSurfaceView的Render渲染线程.===>看 @2.2
        mRenderer = new SquareRenderer();
        // 传入Camera对象，主要是为了调用预览等函数
        // camera对象看 @2.1.2
        mRenderer.init(this, camera, isPreviewStarted);
        setRenderer(mRenderer);
    }

    /*
    * 退出的时候会注销Render线程，避免资源浪费
    * */
    public void unInitGLSView() {
        if (mRenderer != null) {
            mRenderer.deinit();
            mRenderer = null;
        }
    }
}
