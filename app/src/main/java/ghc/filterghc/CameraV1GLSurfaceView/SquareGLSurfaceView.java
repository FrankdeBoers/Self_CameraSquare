package ghc.filterghc.CameraV1GLSurfaceView;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by GHC on 2017/6/12.
 */

public class SquareGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "CameraV1GLSurfaceView";

    private SquareRenderer mRenderer;
    private int textureId = -1;

    public SquareGLSurfaceView(Context context) {
        super(context);
    }

    public void initGLSView(SquareCamera camera, boolean isPreviewStarted) {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        Log.d(TAG, "[initGLSView] >> width:" + width);

        setEGLContextClientVersion(2);
        mRenderer = new SquareRenderer();
        mRenderer.init(this, camera, isPreviewStarted, width);
        setRenderer(mRenderer);
    }

    public void unInitGLSView() {
        if (mRenderer != null) {
            mRenderer.deinit();
            mRenderer = null;
            textureId = -1;
        }
    }
}
