package com.onyx.dailydiary;


import static com.onyx.android.sdk.utils.ApplicationUtil.getApplicationContext;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.pen.NeoFountainPen;
import com.onyx.android.sdk.utils.NumberUtils;
import com.onyx.android.sdk.utils.TouchUtils;
import com.onyx.dailydiary.databinding.FragmentTasksBinding;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.onyx.android.sdk.pen.RawInputCallback;
import com.onyx.android.sdk.pen.TouchHelper;
import com.onyx.android.sdk.data.note.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;

public class TasksFragment extends Fragment implements View.OnClickListener {
    public FragmentTasksBinding binding;
    private static TasksFragment instance;
    private static final String TAG = TasksFragment.class.getSimpleName();


    private String filepath = "Bitmaps";
    private String filename =  "tasks.png";
//    private TouchHelper touchHelper;
    private View surfaceBackground;
//    private Path path;
    private final float STROKE_WIDTH = 4.0f;
    public Bitmap bitmap;
    private Paint mPaint = new Paint();
    private Paint erasePaint = new Paint();
//    private int[] colors = new int[]{Color.WHITE, Color.GREEN, Color.MAGENTA, Color.BLUE};
    private int currentSurfaceBackgroundColor = Color.WHITE;

    public List<TouchPoint> points = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    public void onResume() {


        Log.d(TAG, "onResume");
//        initSurfaceView();

//        loadBitmap();
        super.onResume();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//
        Runnable thread = new Runnable()
        {
            public void run()
            {
                safeLoadBitmap();
                redrawSurface();

            }
        };
        Log.d(TAG, "start thread");
        new Thread(thread).start();    //use start() instead of run()
        Log.d(TAG, "returning");
        return;


//        touchHelper.setRawDrawingEnabled(true);
//        touchHelper.setRawDrawingRenderEnabled(true);
//
//        binding.surfaceview.setZOrderOnTop(true);
//        binding.surfaceview.getHolder().setFormat(PixelFormat.TRANSPARENT);

    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        instance = this;
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initSurfaceView();
        initPaint();

        Button clear_all = (Button) view.findViewById(R.id.button);
        clear_all.setOnClickListener(this);


//        ImageButton clear_all2 = (ImageButton) view.findViewById(R.id.clear_tasks);
//        clear_all2.setOnClickListener(this);
        return view;




    }

    @Override
    public void onClick(View v) {
        resetBitmap();
        redrawSurface();
        Log.d(TAG, "onClick");

    }

    public void safeLoadBitmap()
    {
//
//        SurfaceHolder holder;
//
        while (!binding.surfaceview.getHolder().getSurface().isValid()) {

        }
//            holder =
//            if (mHolder != null) {
                loadBitmap();

                if (bitmap == null) {
                    resetBitmap();
                }
                Canvas lockCanvas = binding.surfaceview.getHolder().lockCanvas();

                Rect rect = new Rect(0, 0, binding.surfaceview.getWidth(), binding.surfaceview.getHeight());
                lockCanvas.drawBitmap(bitmap, null, rect, null);
            binding.surfaceview.getHolder().unlockCanvasAndPost(lockCanvas);


    }

    public static TasksFragment GetInstance()
    {
        return instance;
    }

    public void onDestroyView(){
        Log.d(TAG, "onDestroyView");


        redrawSurface();
        super.onDestroyView();
        saveBitmap();

    }

    private void initPaint(){

//        path = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);

        erasePaint = new Paint();
        erasePaint.setAntiAlias(true);
        erasePaint.setDither(true);
        erasePaint.setColor(Color.WHITE);
        erasePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        erasePaint.setStrokeJoin(Paint.Join.ROUND);
        erasePaint.setStrokeCap(Paint.Cap.ROUND);
        erasePaint.setStrokeWidth(10);
//
    }

    private void initSurfaceView() {
        binding.surfaceview.setBackgroundColor(Color.WHITE);
//        touchHelper = TouchHelper.create(binding.surfaceview, tasks_callback);


//        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        binding.surfaceview.setZOrderOnTop(true);
        binding.surfaceview.getHolder().setFormat(PixelFormat.TRANSPARENT);

//        binding.surfaceview.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//                                                               @Override
//                                                               public void onViewAttachedToWindow(@NonNull View view) {
//                                                                   Log.d(TAG, "surfaceView.onViewAttachedToWindow" );
//
//                                                               }
//
//                                                               @Override
//                                                               public void onViewDetachedFromWindow(@NonNull View view) {
//                                                                   Log.d(TAG, "surfaceView.onViewDetachedFromWindow" );
//
//                                                               }
//                                                           }
//
//        );
        binding.surfaceview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int
                    oldRight, int oldBottom) {

                Log.d(TAG, "surfaceView.onLayoutChange" );

//                if (cleanSurfaceView()) {
//                    binding.surfaceview2.removeOnLayoutChangeListener(this);
//                }
//                List<Rect> exclude = new ArrayList<>();
//                exclude.add(getRelativeRect(binding.surfaceview2, binding.clearTasks));
                Rect limit = new Rect();
                binding.surfaceview.getLocalVisibleRect(limit);
//                touchHelper.setStrokeWidth(STROKE_WIDTH)
//                        .setLimitRect(limit, null)
//                        .openRawDrawing();
//                touchHelper.setStrokeStyle(TouchHelper.STROKE_STYLE_MARKER);
                binding.surfaceview.addOnLayoutChangeListener(this);
            }
        });

        binding.surfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "surfaceView.setOnTouchListener - onTouch::action - " + event.getAction());
//                touchHelper.setRawDrawingEnabled(true);
//                Log.d(TAG, "touchHelper - " +  touchHelper.isRawDrawingCreated() +  touchHelper.isRawDrawingInputEnabled() +  touchHelper.isRawDrawingRenderEnabled() );

                ((MainActivity)getActivity()).writeTasks = true;

                return true;
            }
        });

        final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "surfaceCreated");
                safeLoadBitmap();
//                touchHelper.setRawDrawingEnabled(true);
//                touchHelper.setRawDrawingRenderEnabled(true);

                Rect limit = new Rect();
                binding.surfaceview.getGlobalVisibleRect(limit);

                ((MainActivity)getActivity()).addRect(limit);




            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged");

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(this);
            }
        };
        binding.surfaceview.getHolder().addCallback(surfaceCallback);


//        binding.surfaceview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "surfaceView.setOnTouchListener - onTouch::action - " + event.getAction());
//
//                float X = event.getX();
//                float Y = event.getY();
//                switch (event.getActionMasked()) {
//                    case MotionEvent.ACTION_DOWN:
//                        path.reset();
//                        path.moveTo(X, Y);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        path.lineTo(X, Y);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        path.lineTo(event.getX(),event.getY());
//                        Canvas canvas1 = binding.surfaceview.getHolder().lockCanvas();
//                        canvas1.drawPath(path, mPaint);
//                        binding.surfaceview.getHolder().unlockCanvasAndPost(canvas1);
//                        break;
//
//                }
//                if(path != null){
//                    Canvas canvas = binding.surfaceview.getHolder().lockHardwareCanvas(); // .lockCanvas();
//                    canvas.drawPath(path, mPaint);
//                    binding.surfaceview.getHolder().unlockCanvasAndPost(canvas);
//                }
//                return true;
//            }
//        });
//        final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                Log.d(TAG, width + ", " + height);
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                holder.removeCallback(this);
//            }
//        };
//        binding.surfaceview.getHolder().addCallback(surfaceCallback);


    }

    private RawInputCallback tasks_callback = new RawInputCallback() {

        @Override
        public void onBeginRawDrawing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onBeginRawDrawing");
            disableFingerTouch(getApplicationContext());
//            touchHelper.setRawDrawingRenderEnabled(true);
            points.clear();

        }

        @Override
        public void onEndRawDrawing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onEndRawDrawing");
            enableFingerTouch(getApplicationContext());
//            touchHelper.setRawDrawingRenderEnabled(false);


        }

        @Override
        public void onRawDrawingTouchPointMoveReceived(TouchPoint touchPoint) {
//            Log.d(TAG, "onRawDrawingTouchPointMoveReceived");
//            Log.d(TAG, touchPoint.getX() + ", " + touchPoint.getY());
//                countRec++;
//                countRec = countRec % INTERVAL;
//                Log.d(TAG, "countRec = " + countRec);
//            points.add(touchPoint);
//            if (points.size() >= 100) {
//                List<TouchPoint> pointList = new ArrayList<>(points);
//                points.clear();
//
//                drawScribbleToBitmap(pointList);
////                drawBitmap();
//            }
        }

        @Override
        public void onRawDrawingTouchPointListReceived(TouchPointList touchPointList) {
            Log.d(TAG, "onRawDrawingTouchPointListReceived");
            drawScribbleToBitmap(touchPointList.getPoints(),false);

        }

        @Override
        public void onBeginRawErasing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onBeginRawErasing");
//            EpdController.enablePost(binding.surfaceview, 1);
//            touchHelper.setRawDrawingRenderEnabled(false);
            points.clear();
            redrawSurface();

        }

        @Override
        public void onEndRawErasing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onEndRawErasing");
            redrawSurface();
//            touchHelper.setRawDrawingRenderEnabled(true);

        }

        @Override
        public void onRawErasingTouchPointMoveReceived(TouchPoint touchPoint) {
            Log.d(TAG, "onRawErasingTouchPointMoveReceived");

            points.add(touchPoint);
            if (points.size() >= 100) {
                List<TouchPoint> pointList = new ArrayList<>(points);
                points.clear();
                TouchPointList touchPointList = new TouchPointList();
                for (TouchPoint point : pointList) {
                    touchPointList.add(point);
                }
                drawScribbleToBitmap(pointList,true);
//                drawBitmap();
            }
        }

        @Override
        public void onRawErasingTouchPointListReceived(TouchPointList touchPointList) {
            Log.d(TAG, "onRawErasingTouchPointListReceived");
            drawScribbleToBitmap(touchPointList.getPoints(),true);

        }
    };


    public void drawScribbleToBitmap(List<TouchPoint> list, boolean eraser) {
        Log.d(TAG, "drawScribbleToBitmap");
        Canvas canvas = new Canvas(bitmap);

        Rect limit = new Rect();
        Point offset = new Point();
        binding.surfaceview.getGlobalVisibleRect(limit,offset);
//        Log.d(TAG, "drawScribbleToBitmap " + limit + " " + offset);

        Path path = new Path();
        PointF prePoint = new PointF(list.get(0).x, list.get(0).y);
        path.moveTo(prePoint.x-offset.x, prePoint.y-offset.y);
        for (TouchPoint point : list) {
            path.quadTo(prePoint.x-offset.x, prePoint.y-offset.y, point.x-offset.x, point.y-offset.y);
            prePoint.x = point.x;
            prePoint.y = point.y;

//            Log.d(TAG, "drawScribbleToBitmap: " + prePoint.x + " " + prePoint.y);
        }
        if (eraser){
            canvas.drawPath(path, erasePaint);
        }
        else{
            canvas.drawPath(path, mPaint);
        }
//        canvas.drawColor(Color.BLACK);
//        saveBitmap();
//        redrawSurface();
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.lines, null);
        drawable.setBounds(0, 0,binding.surfaceview.getWidth(), binding.surfaceview.getHeight());
        drawable.draw(canvas);

    }

    public void redrawSurface() {
        ((MainActivity)getActivity()).touchHelper.setRawDrawingRenderEnabled(false);


        Log.d(TAG, "redrawSurface");
        Canvas lockCanvas = binding.surfaceview.getHolder().lockCanvas();
        lockCanvas.drawColor(Color.WHITE);
        lockCanvas.drawBitmap(bitmap, 0, 0, null);
        binding.surfaceview.getHolder().unlockCanvasAndPost(lockCanvas);
        ((MainActivity)getActivity()).touchHelper.setRawDrawingRenderEnabled(true);
    }
    public static void disableFingerTouch(Context context) {
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        Rect rect = new Rect(0, 0, width, height);
        Rect[] arrayRect =new Rect[]{rect};
        EpdController.setAppCTPDisableRegion(context, arrayRect);
    }

    public static void enableFingerTouch(Context context) {
        EpdController.appResetCTPDisableRegion(context);
    }

    public void saveBitmap() {

        Log.d(TAG, "saveBitmap");
        File myExternalFile = new File(getActivity().getExternalFilesDir(filepath), filename);
        try {
            FileOutputStream fos =  new FileOutputStream(myExternalFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.d("SAVE_IMAGE", e.getMessage(), e);


        }
    }

    public void loadBitmap() {
        try {
            Log.d(TAG, "loadBitmap");
            File myExternalFile = new File(getActivity().getExternalFilesDir(filepath), filename);
            if (myExternalFile.exists())
            {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inScaled = true;
                opt.inMutable = true;
                bitmap = BitmapFactory.decodeStream(new FileInputStream(myExternalFile),null, opt);
            }
            else
            {
               resetBitmap();
            }
        } catch (Exception e) {
            Log.d("loadBitmap Error: ", e.getMessage(), e);
        }
    }

    public void resetBitmap() {
        Log.d(TAG, "resetBitmap");
        try {


            bitmap = null;
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.lines, null);
            bitmap = Bitmap.createBitmap(binding.surfaceview.getWidth(), binding.surfaceview.getHeight(), Bitmap.Config.ARGB_8888);
            drawable.setBounds(0, 0, binding.surfaceview.getWidth(), binding.surfaceview.getHeight());
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
        }
        catch (Exception e) {
            Log.d("resetBitmap Error: ", e.getMessage(), e);
        }
        return;
    }
}
