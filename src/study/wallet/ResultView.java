package study.wallet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class ResultView extends SurfaceView implements Callback, Runnable{
    private SurfaceHolder sfh;
    private Paint paint;
    private Canvas canvas;
    private Bitmap bgImg;
    private int imgX = 0, imgY = 0, imgW, imgH, screenW, screenH;
    private Wallet wallet;

    public ResultView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        wallet = (Wallet) context;
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(20);
        Resources res = this.getResources();
        bgImg = BitmapFactory.decodeResource(res, R.drawable.smile);
        imgW = bgImg.getWidth();
        imgH = bgImg.getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        wallet.showMain();
        return true;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        flag = true;
        th = new Thread(this);
        th.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        screenW = getWidth();
        screenH = getHeight();
        imgX = (screenW - imgW) / 2;
        imgY = (screenH - imgH) / 2;
        flag = false;
    }

    private Thread th;
    private boolean flag;
    private long start, during;
    private final long MILLISPERSECOND = 1000;
    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (flag) {
            start = System.currentTimeMillis();
            canvas = sfh.lockCanvas();
            canvas.drawBitmap(bgImg, imgX, imgY, paint);
            sfh.unlockCanvasAndPost(canvas);
            during = System.currentTimeMillis() - start;
            if (during < MILLISPERSECOND) {
                try {
                    Thread.sleep(MILLISPERSECOND - during);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
