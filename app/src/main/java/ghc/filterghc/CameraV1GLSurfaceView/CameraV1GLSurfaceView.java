package ghc.filterghc.CameraV1GLSurfaceView;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by GHC on 2017/6/12.
 */

public class CameraV1GLSurfaceView extends GLSurfaceView {

    private CameraV1Renderer mRenderer;
    private int textureId = -1;

    public CameraV1GLSurfaceView(Context context) {
        super(context);
    }

    public void init(CameraV1 camera, boolean isPreviewStarted) {
        setEGLContextClientVersion(2);
        mRenderer = new CameraV1Renderer();
        mRenderer.init(this, camera, isPreviewStarted);
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
