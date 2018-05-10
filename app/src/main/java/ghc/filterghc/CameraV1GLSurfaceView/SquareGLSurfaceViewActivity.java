package ghc.filterghc.CameraV1GLSurfaceView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class SquareGLSurfaceViewActivity extends Activity {
    private SquareGLSurfaceView mGLSurfaceView;
    private int mCameraId;
    private SquareCamera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mGLSurfaceView = new SquareGLSurfaceView(this);
        mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        DisplayMetrics dm = new DisplayMetrics();
        mCamera = new SquareCamera(this);
        if (!mCamera.openCamera(dm.widthPixels, dm.heightPixels, mCameraId)) {
            return;
        }
        mGLSurfaceView.initGLSView(mCamera, false);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGLSurfaceView != null) {
            mGLSurfaceView.onPause();
            mGLSurfaceView.unInitGLSView();
            mGLSurfaceView = null;
        }

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.releaseCamera();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
