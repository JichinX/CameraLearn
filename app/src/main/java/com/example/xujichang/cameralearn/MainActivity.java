package com.example.xujichang.cameralearn;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int STATE_STOP = 1;
    private static final int STATE_PAUSE = 2;
    private static final int STATE_START = 3;
    private static final int STATE_RECORDING = 4;


    private TextView start;
    private SurfaceView surfaceView;
    private static int status = STATE_STOP;
    private TextView stop;

    private Camera camera;
    private MediaRecorder recorder;
    private SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        start = (TextView) findViewById(R.id.start);
        stop = (TextView) findViewById(R.id.stop);

        holder = surfaceView.getHolder();

        holder.addCallback(this);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecord();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecord();
            }
        });
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void stopRecord() {
        if (status == STATE_RECORDING) {
            recorder.stop();
            recorder.release();
            recorder = null;
            camera.lock();
            camera.release();
        }
    }

    private void startRecord() {
        if (status == STATE_STOP) {
            try {
                prepareRecord();
                recorder.start();
                status = STATE_RECORDING;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (status == STATE_PAUSE) {
            resumeRecord();
        }

    }

    private void resumeRecord() {

    }

    /**
     * 录制前的准备
     */
    private void prepareRecord() throws IOException {
        if (!initCamera()) {
            return;
        }
        recorder = new MediaRecorder();

        recorder.setCamera(camera);

        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        recorder.setVideoSize(176, 144);

        recorder.setVideoFrameRate(20);

        recorder.setPreviewDisplay(holder.getSurface());

        recorder.setOutputFile("/sdcard/love.mp4");

        recorder.prepare();
    }

    private boolean initCamera() {
        camera = Camera.open();

        if (camera == null) {
            return false;
        }
        setCameraParams();
        return true;
    }

    private void setCameraParams() {
        Camera.Parameters params = camera.getParameters();
        params.set("orientation", "portrait");
        camera.setDisplayOrientation(90);
        camera.setParameters(params);
        camera.unlock();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        holder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceView = null;
        holder = null;
        recorder = null;
        status = STATE_STOP;
    }
}
