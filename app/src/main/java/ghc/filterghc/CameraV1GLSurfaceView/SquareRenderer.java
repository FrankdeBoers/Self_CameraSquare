package ghc.filterghc.CameraV1GLSurfaceView;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL11.GL_FLOAT;

/**
 * Created by GHC on 2017/6/12.
 */

public class SquareRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "Filter_MyRenderer";
    private int mOESTextureId = -1;
    private SurfaceTexture mSurfaceTexture;
    private float[] transformMatrix = new float[16];
    private SquareGLSurfaceView mGLSurfaceView;
    private SquareCamera mCamera;
    private boolean bIsPreviewStarted;
    private RenderEngine mRenderEngine;
    private FloatBuffer mDataBuffer;
    private int mShaderProgram = -1;
    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;
    private int[] mFBOIds = new int[1];

    private int mScreenWidth = 800;

    public void init(SquareGLSurfaceView glSurfaceView, SquareCamera camera, boolean isPreviewStarted, int width) {
        mGLSurfaceView = glSurfaceView;
        mCamera = camera;
        bIsPreviewStarted = isPreviewStarted;
        mScreenWidth = width;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mOESTextureId = createOESTextureObject();
        mRenderEngine = new RenderEngine(mOESTextureId);
        mDataBuffer = mRenderEngine.getBuffer();
        mShaderProgram = mRenderEngine.getShaderProgram();
        glGenFramebuffers(1, mFBOIds, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, mFBOIds[0]);
        Log.i(TAG, "onSurfaceCreated: mFBOId: " + mFBOIds[0]);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "screen size width: " + width + " ; height: " + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        gl.glEnable(GL10.GL_SCISSOR_TEST);
        gl.glScissor(20, 20, Constant.CUSTOM_PREVIEW_WIDTH, Constant.CUSTOM_PREVIEW_HEIGHT);

        Long t1 = System.currentTimeMillis();
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(transformMatrix);
        }

        if (!bIsPreviewStarted) {
            bIsPreviewStarted = initSurfaceTexture();
            bIsPreviewStarted = true;
            return;
        }

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        aPositionLocation = glGetAttribLocation(mShaderProgram, ghc.filterghc.CameraV1GLSurfaceView.RenderEngine.POSITION_ATTRIBUTE);
        aTextureCoordLocation = glGetAttribLocation(mShaderProgram, ghc.filterghc.CameraV1GLSurfaceView.RenderEngine.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = glGetUniformLocation(mShaderProgram, ghc.filterghc.CameraV1GLSurfaceView.RenderEngine.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = glGetUniformLocation(mShaderProgram, ghc.filterghc.CameraV1GLSurfaceView.RenderEngine.TEXTURE_SAMPLER_UNIFORM);

        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        glUniform1i(uTextureSamplerLocation, 0);
        glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        if (mDataBuffer != null) {
            mDataBuffer.position(0);
            glEnableVertexAttribArray(aPositionLocation);
            glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 16, mDataBuffer);

            mDataBuffer.position(2);
            glEnableVertexAttribArray(aTextureCoordLocation);
            glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 16, mDataBuffer);
        }

        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        long t2 = System.currentTimeMillis();
        long t = t2 - t1;
        Log.i(TAG, "onDrawFrame: time: " + t);

    }

    public boolean initSurfaceTexture() {
        if (mCamera == null || mGLSurfaceView == null) {
            Log.i(TAG, "mCamera or mGLSurfaceView is null!");
            return false;
        }
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGLSurfaceView.requestRender();
            }
        });
        mCamera.setPreviewTexture(mSurfaceTexture);
        mCamera.startPreview();
        return true;
    }

    public void deinit() {
        if (mRenderEngine != null) {
            mRenderEngine = null;
        }
        mDataBuffer = null;
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCamera = null;
        mOESTextureId = -1;
        bIsPreviewStarted = false;
    }

    public static int createOESTextureObject() {
        int[] tex = new int[1];
        GLES20.glGenTextures(1, tex, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }

}
