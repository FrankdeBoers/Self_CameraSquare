package ghc.filterghc.CameraV1GLSurfaceView;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

/**
 * Created by GHC on 2017/6/27.
 */

public class SquareCamera {
    private Activity mActivity;
    private int mCameraId;
    private Camera mCamera;

    public SquareCamera(Activity activity) {
        mActivity = activity;
    }

    public boolean openCamera(int screenWidth, int screenHeight, int cameraId) {
        try {
            mCameraId = cameraId;
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.set("orientation", "portrait");
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setPreviewSize(Constant.SYSTEM_PREVIEW_WIDTH, Constant.SYSTEM_PREVIEW_HEIGHT);
            setCameraDisplayOrientation(mActivity, mCameraId, mCamera);
            mCamera.setParameters(parameters);
            Log.i("GHC", "open camera");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void setPreviewTexture(SurfaceTexture surfaceTexture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
