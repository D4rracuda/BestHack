package io.card.development;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import io.card.development.ocrreader.ui.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for the Ocr Detecting app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public class OcrCaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {

    private Context context;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Button flipCamera;
    private Button flashCameraButton;
    private Button captureImage;
    private Button BtnCapturarFoto;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    private ActionBar actionBar;
    private CameraSource cameraSource;
    private StringBuilder builder;
    private TextView txTextoCapturado;

    private TextView textView;

    private List<String> allText = new ArrayList<>();


    private static OcrCaptureActivity instance;
    private Activity activity;
    private String nombreProyecto;
    public static final int REQUEST_IMAGE_CAPTURE = 0020;
    private File file;
    private String ubicacion;
    private static final String TAG = "FOTOGRAFIA";
    private int Height = 620, Width = 480;
    private TextRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_capture);
        context = this;
        // camera surface view created
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        flipCamera = (Button) findViewById(R.id.flipCamera);
        flashCameraButton = (Button) findViewById(R.id.flash);
        captureImage = (Button) findViewById(R.id.captureImage);
        BtnCapturarFoto = (Button) findViewById(R.id.btnCapturarFoto);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        txTextoCapturado = (TextView) findViewById(R.id.tvTextoCapturado);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        flipCamera.setOnClickListener(this);
        captureImage.setOnClickListener(this);
        flashCameraButton.setOnClickListener(this);
        BtnCapturarFoto.setOnClickListener(this);
        textView = findViewById(R.id.textView);
        boolean autoFocus = true;
        boolean useFlash = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    /*if (Camera.getNumberOfCameras() > 1) {
        flipCamera.setVisibility(View.VISIBLE);
    }*/
        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            flashCameraButton.setVisibility(View.GONE);
            flipCamera.setVisibility(View.GONE);
        }


        recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (recognizer.isOperational()) {

            cameraSource = new CameraSource.Builder(getApplicationContext(), recognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                    .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO : null)
                    .build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(OcrCaptureActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                        return;
                    }
                    try {
                        releaseCamera();
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (cameraSource != null) {
                        cameraSource.stop();
                    }
                }
            });
            recognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                    Toast.makeText(getApplicationContext(),"33", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        builder = new StringBuilder();
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock it = items.valueAt(i);
                            builder.append(it.getValue());
                        }
                        final String read = builder.toString().trim();
                        //String read = builder.toString().trim().replace(" ", "").replace("\n", "");
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txTextoCapturado.setText(read);
                                    allText.add(read);
                                }
                            });
                        } catch (Exception ex) {
                            Log.e("error", "Error al actualizar texto OCR");
                        }

                        //It continues doing other things here
                    }
                }
            });


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.setErrorCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.setErrorCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }

    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        //releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new Camera.ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
    }

    private void showFlashButton(Camera.Parameters params) {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        flashCameraButton.setVisibility(showFlash ? View.VISIBLE
                : View.INVISIBLE);

    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    private void releaseCameraSource() {
        try {
            if (cameraSource != null) {
                cameraSource.stop();
                cameraSource.release();
                cameraSource = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            cameraSource = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash:
                flashOnButton();
                break;
            case R.id.flipCamera:
                flipCamera();
                break;
            case R.id.captureImage:
                takeImage();
                break;
            case R.id.btnCapturarFoto:
                cameraSource.stop();
                String res = "";
                for(int i = 0; i < allText.size(); i++){
                    res += allText.get(i);
                    res += "\n";
                }
                textView.setText(res);
                takeImage();
                break;

            default:
                break;
        }
    }

    private void takeImage() {
        try {
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //releaseCameraSource();
            //releaseCamera();
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //setUpCamera(camera);
            //Thread.sleep(1000);
            cameraSource.takePicture(null, new CameraSource.PictureCallback() {

                private File imageFile;

                @Override
                public void onPictureTaken(byte[] bytes) {
                    try {
                        // convert byte array into bitmap
                        Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);

                        // rotate Image
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(rotation);
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                                loadedImage.getWidth(), loadedImage.getHeight(),
                                rotateMatrix, false);
                        String state = Environment.getExternalStorageState();
                        File folder = null;
                        if (state.contains(Environment.MEDIA_MOUNTED)) {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/Demo");
                        } else {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/Demo");
                        }

                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            java.util.Date date = new java.util.Date();
                            imageFile = new File(folder.getAbsolutePath()
                                    + File.separator
                                    //+ new Timestamp(date.getTime()).toString()
                                    + "Image.jpg");

                            imageFile.createNewFile();
                        } else {
                            Toast.makeText(getBaseContext(), "Image Not saved",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                        // save image into gallery
                        rotatedBitmap = resize(rotatedBitmap, 800, 600);
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                        FileOutputStream fout = new FileOutputStream(imageFile);
                        fout.write(ostream.toByteArray());
                        fout.close();
                        ContentValues values = new ContentValues();

                        values.put(MediaStore.Images.Media.DATE_TAKEN,
                                System.currentTimeMillis());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.MediaColumns.DATA,
                                imageFile.getAbsolutePath());

                        OcrCaptureActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        setResult(Activity.RESULT_OK); //add this
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            txTextoCapturado.setText("Error al capturar fotografia!");
        }

    }

    private void flipCamera() {
        int id = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK);
        if (!openCamera(id)) {
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(OcrCaptureActivity.this,
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void flashOnButton() {
        if (camera != null) {
            try {
                Camera.Parameters param = camera.getParameters();
                if (flashmode) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

                }
                //param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_TORCH : Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
            } catch (Exception e) {
                e.printStackTrace();
                // TODO: handle exception
            }

        }
    }

    /**
     * Metodo para cambiar el tamaño de la fotografia una resolucion predeterminada.
     *
     * @param image
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
}
