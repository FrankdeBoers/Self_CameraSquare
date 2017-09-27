package ghc.filterghc.CameraV1GLSurfaceView;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by GHC on 2017/6/12.
 */

public class CameraV1GLSurfaceView extends GLSurfaceView {

    private static final String TAG = "CameraV1GLSurfaceView";

    private CameraV1Renderer mRenderer;
    private int textureId = -1;

    public CameraV1GLSurfaceView(Context context) {
        super(context);
    }

    public void init(CameraV1 camera, boolean isPreviewStarted) {

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        Log.d(TAG, "[init] >> width:" + width);

        setEGLContextClientVersion(2);
        mRenderer = new CameraV1Renderer();
        mRenderer.init(this, camera, isPreviewStarted, width);
        setRenderer(mRenderer);
    }

    public void deinit() {
        if (mRenderer != null) {
            mRenderer.deinit();
            mRenderer = null;
            textureId = -1;
        }
    }
}
