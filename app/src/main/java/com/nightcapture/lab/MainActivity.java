package com.nightcapture.lab;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Local-only experimental Camera2 lab for one Redmi 15C. No mic/network. */
public final class MainActivity extends Activity implements SensorEventListener {
    private static final int REQ_CAMERA=102, REQ_EXPORT=103;
    private static final String[] MODES={
        "Automático (referência)","Detalhes (menos FPS)","Movimento (mais FPS)",
        "Low Light Boost (se disponível)","Manual ISO/exposição"
    };
    private TextureView preview;
    private TextView status, measurements;
    private EditText scene, isoBox, exposureBox;
    private Spinner modeBox, resolutionBox, codecBox, fpsBox;
    private Button startButton, stopButton, photoButton;
    private HandlerThread thread;
    private Handler background;
    private final Handler main=new Handler(android.os.Looper.getMainLooper());
    private CameraManager manager;
    private CameraDevice camera;
    private CameraCaptureSession session;
    private CameraCharacteristics characteristics;
    private String cameraId;
    private ImageReader photoReader;
    private Surface previewSurface;
    private MediaRecorder recorder;
    private ParcelFileDescriptor recordingFile;
    private Uri recordingUri;
    private Size recordingSize;
    private volatile boolean recording=false, starting=false;
    private long recordingStartedMs=0, recordingStartedElapsedMs=0;
    private volatile long videoFrames=0;
    private SensorManager motionManager;
    private Sensor gyroSensor,accelSensor,rotationSensor;
    private long lastGyroMs=0,lastAccelMs=0,lastRotationMs=0;
    private float[] previousRotationMatrix=null;
    private boolean destroyed=false;
    private int mode=0, selectedResolution=0, selectedCodec=0, selectedIso=1600, selectedVideoFps=30;
    private double selectedExposureMs=50;
    private String sceneTag="quarto", pendingExport, pendingPhotoFileName, currentRecordingName;
    private final Object sampleLock=new Object();
    private long frames=0,lastFrameNs=0,lastHudMs=0;
    private double instantFps=0;
    private final JSONArray samples=new JSONArray();
    private final JSONArray events=new JSONArray();
    private final JSONArray motionSamples=new JSONArray();

    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setStatusBarColor(0xff080d15);
        getWindow().setNavigationBarColor(0xff080d15);
        buildInterface();
        thread=new HandlerThread("vision-camera");
        thread.start();
        background=new Handler(thread.getLooper());
        manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
        motionManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if(motionManager!=null){
            gyroSensor=motionManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            accelSensor=motionManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            rotationSensor=motionManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA},REQ_CAMERA);
        else discoverCamera();
    }
    private int dp(int x) {return (int)(getResources().getDisplayMetrics().density*x+0.5f);}
    private TextView text(String label) {
        TextView v=new TextView(this);
        v.setTextColor(0xffe1e8ef);
        v.setTextSize(14);
        v.setText(label);
        v.setPadding(dp(4),dp(5),dp(4),dp(5));
        return v;
    }
    private Button button(String label) {
        Button v=new Button(this);
        v.setText(label);
        v.setAllCaps(false);
        return v;
    }
    private Spinner spinner(String... labels) {
        Spinner s=new Spinner(this);
        s.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels));
        return s;
    }
    private void buildInterface() {
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(10),dp(6),dp(10),dp(5));
        root.setBackgroundColor(0xff111821);
        TextView title=text("VISION / Lab 0.5");
        title.setTextSize(21);
        root.addView(title);
        status=text("Permissão da câmera necessária.");
        root.addView(status);
        preview=new TextureView(this);
        preview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture s,int w,int h) { openCamera(); }
            public void onSurfaceTextureSizeChanged(SurfaceTexture s,int w,int h) { }
            public boolean onSurfaceTextureDestroyed(SurfaceTexture s) { closeCamera(); return true; }
            public void onSurfaceTextureUpdated(SurfaceTexture s) { }
        });
        root.addView(preview,new LinearLayout.LayoutParams(-1,0,1.1f));
        measurements=text("ISO: -- | exposição: -- | FPS: --");
        measurements.setTextColor(0xffa7dcef);
        root.addView(measurements);
        ScrollView scroll=new ScrollView(this);
        LinearLayout panel=new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(panel);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1.2f));

        panel.addView(text("Nome da cena"));
        scene=new EditText(this);
        scene.setSingleLine(true);
        scene.setText("quarto");
        scene.setTextColor(0xffffffff);
        panel.addView(scene);
        panel.addView(text("Perfil de captura"));
        modeBox=spinner(MODES);
        panel.addView(modeBox);
        panel.addView(text("Resolução"));
        resolutionBox=spinner("1280 x 720 (inicial)","1920 x 1080 (teste)");
        panel.addView(resolutionBox);
        panel.addView(text("Codec do vídeo"));
        codecBox=spinner("H.264 / AVC","H.265 / HEVC");
        panel.addView(codecBox);
        panel.addView(text("FPS solicitado ao sensor/gravador (confirmar no MP4)"));
        fpsBox=spinner("10 FPS","15 FPS","20 FPS","24 FPS","30 FPS");
        fpsBox.setSelection(4);
        panel.addView(fpsBox);
        panel.addView(text("ISO manual (se Camera2 permitir)"));
        isoBox=new EditText(this);
        isoBox.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        isoBox.setText("1600");
        panel.addView(isoBox);
        panel.addView(text("Exposição manual em ms"));
        exposureBox=new EditText(this);
        exposureBox.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        exposureBox.setText("50");
        panel.addView(exposureBox);

        Button apply=button("Aplicar perfil / exposição");
        apply.setOnClickListener(v->{
            if(recording||starting) {toast("Pare o vídeo antes de alterar.");return;}
            readControls();
            configure(false);
        });
        panel.addView(apply);
        photoButton=button("Tirar foto JPEG de referência");
        photoButton.setOnClickListener(v->photo());
        panel.addView(photoButton);
        startButton=button("Iniciar vídeo MP4 (sem áudio)");
        startButton.setOnClickListener(v->startVideo());
        panel.addView(startButton);
        stopButton=button("Parar gravação");
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(v->stopVideo());
        panel.addView(stopButton);
        Button json=button("Exportar diagnóstico + amostras JSON");
        json.setOnClickListener(v->export(false));
        panel.addView(json);
        Button csv=button("Exportar amostras CSV");
        csv.setOnClickListener(v->export(true));
        panel.addView(csv);
        panel.addView(text("Diagnóstico de movimento: giroscópio e acelerômetro, quando disponíveis. Sem GPS, internet, áudio ou gravação oculta."));
        setContentView(root);
    }
    private void readControls() {
        mode=modeBox.getSelectedItemPosition();
        selectedResolution=resolutionBox.getSelectedItemPosition();
        selectedCodec=codecBox.getSelectedItemPosition();
        selectedVideoFps=new int[]{10,15,20,24,30}[fpsBox.getSelectedItemPosition()];
        if(mode==3&&!hasLowLightBoost()&&characteristics!=null){
            mode=0;modeBox.setSelection(0);
            toast("Low Light Boost não anunciado nesta câmera; modo Automático aplicado.");
        }
        try { selectedIso=Integer.parseInt(isoBox.getText().toString()); } catch(Exception ignored) {selectedIso=1600;}
        try { selectedExposureMs=Double.parseDouble(exposureBox.getText().toString().replace(',','.')); }
        catch(Exception ignored) {selectedExposureMs=50;}
        String n=scene.getText().toString().trim().replaceAll("[^a-zA-Z0-9_-]","_");
        if(n.isEmpty()) n="cena";
        sceneTag=n.substring(0,Math.min(n.length(),24));
    }
    private void discoverCamera() {
        try {
            String best=null;
            long pixels=-1;
            for(String id:manager.getCameraIdList()) {
                CameraCharacteristics c=manager.getCameraCharacteristics(id);
                Integer facing=c.get(CameraCharacteristics.LENS_FACING);
                if(facing==null||facing!=CameraCharacteristics.LENS_FACING_BACK) continue;
                Size s=c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
                long p=s==null?0:(long)s.getWidth()*s.getHeight();
                if(best==null||p>pixels) {best=id; pixels=p;}
            }
            if(best==null) {say("Nenhuma câmera traseira disponível.");return;}
            cameraId=best;
            characteristics=manager.getCameraCharacteristics(best);
            readControls();
            log("camera_selected",best+" (candidata pela maior matriz de pixels; validar lente)");
            say("Câmera traseira "+best+" selecionada.");
            openCamera();
        } catch(Exception e) {failure("Seleção da câmera",e);}
    }
    private void openCamera() {
        if(destroyed||cameraId==null||camera!=null||!preview.isAvailable()
                ||checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)return;
        try {
            manager.openCamera(cameraId,new CameraDevice.StateCallback(){
                public void onOpened(CameraDevice c) {
                    if(destroyed){c.close();return;}
                    camera=c; configure(false);
                }
                public void onDisconnected(CameraDevice c) {
                    c.close();camera=null;say("Câmera desconectada.");
                }
                public void onError(CameraDevice c,int error) {
                    c.close();camera=null;say("Erro Camera2: "+error);log("camera_error",""+error);
                }
            },background);
        }catch(Exception e){failure("Abrir câmera",e);}
    }
    private Size chooseVideoSize() {
        StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] sizes=map==null?null:map.getOutputSizes(MediaRecorder.class);
        if(sizes==null||sizes.length==0)return new Size(1280,720);
        int w=selectedResolution==0?1280:1920;
        int h=selectedResolution==0?720:1080;
        for(Size s:sizes)if(s.getWidth()==w&&s.getHeight()==h)return s;
        Size best=null;
        for(Size s:sizes){
            if(s.getWidth()<=w&&s.getHeight()<=h
                    &&(best==null||(long)s.getWidth()*s.getHeight()>(long)best.getWidth()*best.getHeight())) best=s;
        }
        if(best!=null)return best;
        return Arrays.stream(sizes).min(Comparator.comparingLong(s->(long)s.getWidth()*s.getHeight())).orElse(sizes[0]);
    }
    private Size choosePhotoSize() {
        StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] a=map==null?null:map.getOutputSizes(ImageFormat.JPEG);
        if(a==null||a.length==0)return new Size(1280,720);
        Size best=null;
        for(Size s:a){
            long p=(long)s.getWidth()*s.getHeight();
            if(p>3_000_000||s.getWidth()>1920||s.getHeight()>1440)continue;
            if(best==null||p>(long)best.getWidth()*best.getHeight())best=s;
        }
        return best==null?a[0]:best;
    }
    private void configure(boolean forVideo) {
        if(camera==null||!preview.isAvailable())return;
        try{
            if(session!=null){session.close();session=null;}
            if(photoReader!=null){photoReader.close();photoReader=null;}
            if(previewSurface!=null){previewSurface.release();previewSurface=null;}
            recordingSize=chooseVideoSize();
            SurfaceTexture texture=preview.getSurfaceTexture();
            if(texture==null)return;
            texture.setDefaultBufferSize(recordingSize.getWidth(),recordingSize.getHeight());
            previewSurface=new Surface(texture);
            List<Surface> targets=new ArrayList<>();
            targets.add(previewSurface);
            if(forVideo){
                if(recorder==null)throw new IllegalStateException("Gravador não preparado");
                targets.add(recorder.getSurface());
            }else{
                Size p=choosePhotoSize();
                log("photo_stream_selected",p.getWidth()+"x"+p.getHeight());
                photoReader=ImageReader.newInstance(p.getWidth(),p.getHeight(),ImageFormat.JPEG,2);
                photoReader.setOnImageAvailableListener(r->savePhoto(r),background);
                targets.add(photoReader.getSurface());
            }
            camera.createCaptureSession(targets,new CameraCaptureSession.StateCallback(){
                public void onConfigured(CameraCaptureSession s) {
                    if(destroyed||camera==null){s.close();return;}
                    session=s;
                    try {
                        s.setRepeatingRequest(request(forVideo,false),callback,background);
                        if(forVideo&&starting) {
                            recorder.start();
                            videoFrames=0;lastFrameNs=0;
                            previousRotationMatrix=null;lastRotationMs=0;
                            recordingStartedElapsedMs=android.os.SystemClock.elapsedRealtime();
                            starting=false;recording=true;recordingStartedMs=System.currentTimeMillis();
                            log("video_started",currentRecordingName+" size="+recordingSize+" mode="+MODES[mode]+" requestedFps="+selectedVideoFps+" codec="+(selectedCodec==1?"HEVC":"H264")+" isoRequested="+selectedIso+" exposureMsRequested="+selectedExposureMs+" thermal="+thermalStatus());
                            runOnUiThread(()->{
                                startButton.setEnabled(false);stopButton.setEnabled(true);
                                photoButton.setEnabled(false);
                                preview.setKeepScreenOn(true);
                                status.setText("GRAVANDO MP4");
                            });
                        } else if(!forVideo) say("Prévia pronta: "+MODES[mode]);
                    }catch(Exception e) {
                        failure("Iniciar sessão",e);
                        if(forVideo)main.post(()->stopVideo());
                    }
                }
                public void onConfigureFailed(CameraCaptureSession s) {
                    log("session_failed","video="+forVideo);
                    say("O sistema rejeitou essa combinação de saídas.");
                    if(forVideo)main.post(()->stopVideo());
                }
            },background);
        }catch(Exception e){failure("Configurar câmera",e);if(forVideo)main.post(()->stopVideo());}
    }
    private boolean supports(int capability) {
        int[] capabilities=characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        if(capabilities!=null)for(int c:capabilities)if(c==capability)return true;
        return false;
    }
    private boolean hasLowLightBoost() {
        int[] modes=characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        if(modes!=null)for(int x:modes)
            if(x==CameraMetadata.CONTROL_AE_MODE_ON_LOW_LIGHT_BOOST_BRIGHTNESS_PRIORITY)return true;
        return false;
    }
    private Range<Integer> preferredFps() {
        Range<Integer>[] a=characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        if(a!=null)for(Range<Integer> r:a)
            if(r.getLower()==selectedVideoFps&&r.getUpper()==selectedVideoFps)return r;
        return null;
    }
    private CaptureRequest request(boolean video,boolean still) throws Exception {
        CaptureRequest.Builder b=camera.createCaptureRequest(still?CameraDevice.TEMPLATE_STILL_CAPTURE:
                video?CameraDevice.TEMPLATE_RECORD:CameraDevice.TEMPLATE_PREVIEW);
        if(still){b.addTarget(photoReader.getSurface());b.setTag(pendingPhotoFileName);}
        else {
            b.addTarget(previewSurface);
            if(video)b.addTarget(recorder.getSurface());
        }
        b.set(CaptureRequest.FLASH_MODE,CaptureRequest.FLASH_MODE_OFF);
        int[] af=characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        if(af!=null)for(int v:af)if(v==CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO){
            b.set(CaptureRequest.CONTROL_AF_MODE,v);break;
        }
        if(mode==3&&hasLowLightBoost()) {
            b.set(CaptureRequest.CONTROL_AE_MODE,
                    CameraMetadata.CONTROL_AE_MODE_ON_LOW_LIGHT_BOOST_BRIGHTNESS_PRIORITY);
        }else if(mode==4&&supports(CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)){
            b.set(CaptureRequest.CONTROL_AE_MODE,CameraMetadata.CONTROL_AE_MODE_OFF);
            Range<Integer> isoRange=characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
            Range<Long> expRange=characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
            int iso=Math.max(50,selectedIso);
            long exp=(long)(Math.max(0.1,selectedExposureMs)*1_000_000.0);
            if(isoRange!=null)iso=Math.max(isoRange.getLower(),Math.min(iso,isoRange.getUpper()));
            if(expRange!=null)exp=Math.max(expRange.getLower(),Math.min(exp,expRange.getUpper()));
            b.set(CaptureRequest.SENSOR_SENSITIVITY,iso);
            b.set(CaptureRequest.SENSOR_EXPOSURE_TIME,exp);
            long frameNs=1_000_000_000L/selectedVideoFps;
            if(video&&exp>frameNs){
                log("manual_exposure_clamped",exp+" ns -> "+frameNs+" ns para "+selectedVideoFps+" FPS");
                exp=frameNs;
                b.set(CaptureRequest.SENSOR_EXPOSURE_TIME,exp);
            }
            b.set(CaptureRequest.SENSOR_FRAME_DURATION,Math.max(exp,frameNs));
        }else{
            b.set(CaptureRequest.CONTROL_AE_MODE,CameraMetadata.CONTROL_AE_MODE_ON);
            Range<Integer> fps=preferredFps();
            if(fps!=null)b.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,fps);
            else log("fps_range_fallback","Sem intervalo exato para "+selectedVideoFps+" FPS");
        }
        if(still){
            Integer orientation=characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            b.set(CaptureRequest.JPEG_ORIENTATION,orientation==null?90:orientation);
        }
        return b.build();
    }
    private final CameraCaptureSession.CaptureCallback callback=new CameraCaptureSession.CaptureCallback(){
        @Override public void onCaptureCompleted(CameraCaptureSession s,CaptureRequest r,TotalCaptureResult x){
            frames++;
            if(recording)videoFrames++;
            Long ts=x.get(CaptureResult.SENSOR_TIMESTAMP);
            Integer iso=x.get(CaptureResult.SENSOR_SENSITIVITY);
            Long exp=x.get(CaptureResult.SENSOR_EXPOSURE_TIME);
            Long duration=x.get(CaptureResult.SENSOR_FRAME_DURATION);
            Integer llb=x.get(CaptureResult.CONTROL_LOW_LIGHT_BOOST_STATE);
            if(ts!=null&&lastFrameNs>0&&ts>lastFrameNs)instantFps=1_000_000_000.0/(ts-lastFrameNs);
            if(ts!=null)lastFrameNs=ts;
            if(frames%5==0)try{
                JSONObject row=new JSONObject();
                row.put("time",System.currentTimeMillis());
                row.put("sensorTimestampNs",ts);
                row.put("iso",iso);
                row.put("exposureNs",exp);
                row.put("frameDurationNs",duration);
                row.put("measuredInstantFps",instantFps);
                row.put("llbState",llb);
                row.put("recording",recording);
                row.put("mode",MODES[mode]);
                row.put("clip",recording?currentRecordingName:"PREVIEW");
                row.put("aeState",x.get(CaptureResult.CONTROL_AE_STATE));
                row.put("afState",x.get(CaptureResult.CONTROL_AF_STATE));
                row.put("awbState",x.get(CaptureResult.CONTROL_AWB_STATE));
                row.put("focusDistanceDiopters",x.get(CaptureResult.LENS_FOCUS_DISTANCE));
                synchronized(sampleLock){if(samples.length()<10000)samples.put(row);}
            }catch(Exception ignored){ }
            long now=System.currentTimeMillis();
            if(photoReader!=null&&!recording&&Integer.valueOf(CameraMetadata.CONTROL_CAPTURE_INTENT_STILL_CAPTURE).equals(r.get(CaptureRequest.CONTROL_CAPTURE_INTENT))){
                log("still_metadata","file="+String.valueOf(r.getTag())+" ISO="+iso+" exposureNs="+exp);
            }
            if(now-lastHudMs>=650){
                lastHudMs=now;
                String hud=String.format(Locale.US,"ISO %s | %.1f ms | %.1f fps | LLB %s",
                    iso==null?"?":iso.toString(),exp==null?0:exp/1e6,instantFps,
                    llb==null?"?":llb.toString());
                runOnUiThread(()->measurements.setText(hud));
            }
        }
    };
    private void photo(){
        if(recording||starting||session==null||photoReader==null){toast("Aguarde a prévia.");return;}
        readControls();
        pendingPhotoFileName=fileName("jpg");
        photoButton.setEnabled(false);
        try{session.capture(request(false,true),callback,background);log("photo_request",pendingPhotoFileName+" "+MODES[mode]);}
        catch(Exception e){photoButton.setEnabled(true);failure("Fotografia",e);}
    }
    private void savePhoto(ImageReader r){
        Image image=null;
        Uri uri=null;
        try{
            image=r.acquireNextImage();
            if(image==null)return;
            ByteBuffer data=image.getPlanes()[0].getBuffer();
            byte[] bytes=new byte[data.remaining()];
            data.get(bytes);
            ContentValues cv=new ContentValues();
            cv.put(MediaStore.Images.Media.DISPLAY_NAME,pendingPhotoFileName==null?fileName("jpg"):pendingPhotoFileName);
            cv.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
            cv.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+"/VisionLab");
            cv.put(MediaStore.Images.Media.IS_PENDING,1);
            uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
            if(uri==null)throw new IllegalStateException("MediaStore não criou JPEG");
            try(OutputStream os=getContentResolver().openOutputStream(uri)){
                if(os==null)throw new IllegalStateException("Sem stream de saída");
                os.write(bytes);
            }
            ContentValues ready=new ContentValues();
            ready.put(MediaStore.Images.Media.IS_PENDING,0);
            getContentResolver().update(uri,ready,null,null);
            log("photo_saved",String.valueOf(pendingPhotoFileName)+" "+bytes.length+" bytes");
            toast("Foto salva na Galeria.");
        }catch(Exception e){if(uri!=null)getContentResolver().delete(uri,null,null);failure("Salvar foto",e);}
        finally{if(image!=null)image.close();pendingPhotoFileName=null;
            runOnUiThread(()->photoButton.setEnabled(!recording&&!starting));}
    }
    private void startVideo(){
        if(camera==null||recording||starting){toast("Câmera não está pronta.");return;}
        readControls();
        try{
            recordingSize=chooseVideoSize();
            ContentValues cv=new ContentValues();
            currentRecordingName=fileName("mp4");
            cv.put(MediaStore.Video.Media.DISPLAY_NAME,currentRecordingName);
            cv.put(MediaStore.Video.Media.MIME_TYPE,"video/mp4");
            cv.put(MediaStore.Video.Media.RELATIVE_PATH,Environment.DIRECTORY_MOVIES+"/VisionLab");
            cv.put(MediaStore.Video.Media.IS_PENDING,1);
            recordingUri=getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,cv);
            if(recordingUri==null)throw new IllegalStateException("Não criou MP4");
            recordingFile=getContentResolver().openFileDescriptor(recordingUri,"rw");
            if(recordingFile==null)throw new IllegalStateException("Sem arquivo de gravação");
            recorder=new MediaRecorder(this);
            recorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setVideoEncoder(selectedCodec==1?MediaRecorder.VideoEncoder.HEVC:MediaRecorder.VideoEncoder.H264);
            recorder.setVideoSize(recordingSize.getWidth(),recordingSize.getHeight());
            int requestedFps=selectedVideoFps;
            recorder.setVideoFrameRate(requestedFps);
            log("recorder_fps_requested",String.valueOf(requestedFps));
            recorder.setVideoEncodingBitRate(selectedResolution==1?12_000_000:6_000_000);
            recorder.setOrientationHint(90);
            recorder.setOutputFile(recordingFile.getFileDescriptor());
            recorder.prepare();
            starting=true;
            configure(true);
        }catch(Exception e){failure("Preparar gravação",e);stopVideo();}
    }
    private void stopVideo(){
        if(!recording&&!starting&&recorder==null)return;
        boolean valid=recording;
        try{if(session!=null){session.stopRepeating();session.close();session=null;}}
        catch(Exception ignored){ }
        try{if(recording&&recorder!=null)recorder.stop();}
        catch(Exception e){valid=false;log("recorder_stop_error",e.toString());}
        try{if(recorder!=null)recorder.release();}catch(Exception ignored){ }
        recorder=null;
        try{if(recordingFile!=null)recordingFile.close();}catch(Exception ignored){ }
        recordingFile=null;
        if(recordingUri!=null){
            if(valid){
                ContentValues cv=new ContentValues();
                cv.put(MediaStore.Video.Media.IS_PENDING,0);
                getContentResolver().update(recordingUri,cv,null,null);
                log("video_saved",currentRecordingName+" "+String.valueOf(recordingUri)+" durationMs="+(System.currentTimeMillis()-recordingStartedMs)+" sensorFrames="+videoFrames+" thermal="+thermalStatus());
                toast("Vídeo salvo na Galeria.");
            }else{
                getContentResolver().delete(recordingUri,null,null);
                log("video_discarded","Gravação não terminou corretamente.");
            }
            recordingUri=null;
        }
        recording=false;starting=false;currentRecordingName=null;recordingStartedMs=0;recordingStartedElapsedMs=0;
        previousRotationMatrix=null;
        startButton.setEnabled(true);stopButton.setEnabled(false);photoButton.setEnabled(true);
        preview.setKeepScreenOn(false);
        if(!destroyed&&camera!=null&&preview.isAvailable())configure(false);
    }
    private String fileName(String ext){
        return "Vision_"+sceneTag+"_M"+mode+"_F"+selectedVideoFps+"_"+new SimpleDateFormat("yyyyMMdd_HHmmss_SSS",Locale.US).format(new Date())+"."+ext;
    }
    private String thermalStatus(){
        try{PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);return pm==null?"unknown":String.valueOf(pm.getCurrentThermalStatus());}
        catch(Exception e){return "unknown";}
    }
    private void export(boolean csv){
        if(recording||starting){toast("Pare a gravação antes de exportar relatórios.");return;}
        try{
            JSONArray snapshot;
            synchronized(sampleLock){snapshot=new JSONArray(samples.toString());}
            if(csv){
                StringBuilder sb=new StringBuilder("time,sensorTimestampNs,iso,exposureNs,frameDurationNs,measuredInstantFps,llbState,recording,mode,clip,aeState,afState,awbState,focusDistanceDiopters\n");
                for(int i=0;i<snapshot.length();i++){
                    JSONObject row=snapshot.getJSONObject(i);
                    String[] keys={"time","sensorTimestampNs","iso","exposureNs","frameDurationNs",
                            "measuredInstantFps","llbState","recording","mode","clip","aeState","afState","awbState","focusDistanceDiopters"};
                    for(int k=0;k<keys.length;k++){
                        if(k>0)sb.append(',');
                        sb.append('"').append(row.optString(keys[k],"").replace("\"","\"\"")).append('"');
                    }
                    sb.append('\n');
                }
                pendingExport=sb.toString();
            }else{
                JSONObject data=Diagnostic.inventory(this);
                data.put("selectedCameraId",cameraId);
                synchronized(sampleLock){data.put("events",new JSONArray(events.toString()));}
                data.put("captureSamples",snapshot);
                synchronized(sampleLock){data.put("motionSamples",new JSONArray(motionSamples.toString()));}
                data.put("motionNote","motionSamples are sampled sensor magnitudes, not stabilization or video frames");
                data.put("framesObserved",frames);
                data.put("sampleNote","Sensor frame timestamps are not proof of encoded MP4 framerate.");
                PowerManager p=(PowerManager)getSystemService(Context.POWER_SERVICE);
                if(p!=null)data.put("thermalStatus",p.getCurrentThermalStatus());
                pendingExport=data.toString(2);
            }
            Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType(csv?"text/csv":"application/json");
            i.putExtra(Intent.EXTRA_TITLE,"Vision_diagnostico_"+System.currentTimeMillis()+(csv?".csv":".json"));
            startActivityForResult(i,REQ_EXPORT);
        }catch(Exception e){failure("Exportar diagnóstico",e);}
    }
    @Override protected void onActivityResult(int request,int result,Intent intent){
        super.onActivityResult(request,result,intent);
        if(request==REQ_EXPORT&&result==RESULT_OK&&intent!=null&&intent.getData()!=null){
            try(OutputStream stream=getContentResolver().openOutputStream(intent.getData())){
                if(stream==null)throw new IllegalStateException("Arquivo não disponível");
                stream.write(pendingExport.getBytes(StandardCharsets.UTF_8));
                toast("Relatório exportado com sucesso.");
            }catch(Exception e){failure("Gravar relatório",e);}
        }
    }
    private void log(String type,String detail){
        try{
            JSONObject e=new JSONObject();
            e.put("ms",System.currentTimeMillis());e.put("type",type);e.put("detail",detail);
            synchronized(sampleLock){events.put(e);}
        }catch(Exception ignored){}
    }
    private void say(String msg){runOnUiThread(()->{if(!destroyed)status.setText(msg);});}
    private void toast(String msg){runOnUiThread(()->Toast.makeText(this,msg,Toast.LENGTH_LONG).show());}
    private void failure(String step,Exception e){
        log("error_"+step,e.getClass().getSimpleName()+": "+e.getMessage());
        say(step+": "+e.getMessage());
    }
    @Override public void onRequestPermissionsResult(int request,String[] names,int[] grants){
        super.onRequestPermissionsResult(request,names,grants);
        if(request==REQ_CAMERA&&grants.length>0&&grants[0]==PackageManager.PERMISSION_GRANTED)discoverCamera();
        else if(request==REQ_CAMERA)say("Permissão de câmera negada.");
    }
    private void closeCamera(){
        try{if(session!=null)session.close();}catch(Exception ignored){}
        session=null;
        try{if(camera!=null)camera.close();}catch(Exception ignored){}
        camera=null;
        try{if(photoReader!=null)photoReader.close();}catch(Exception ignored){}
        photoReader=null;
        try{if(previewSurface!=null)previewSurface.release();}catch(Exception ignored){}
        previewSurface=null;
    }
    @Override public void onSensorChanged(SensorEvent event) {
        if(!recording||event==null||event.values.length<3||recordingStartedElapsedMs<=0)return;
        int type=event.sensor.getType();
        if(type!=Sensor.TYPE_GYROSCOPE&&type!=Sensor.TYPE_ACCELEROMETER&&type!=Sensor.TYPE_ROTATION_VECTOR)return;
        long now=android.os.SystemClock.elapsedRealtime();
        if(type==Sensor.TYPE_GYROSCOPE){
            if(now-lastGyroMs<100)return;
            lastGyroMs=now;
        }else if(type==Sensor.TYPE_ACCELEROMETER){
            if(now-lastAccelMs<100)return;
            lastAccelMs=now;
        }else{
            if(now-lastRotationMs<100)return;
            lastRotationMs=now;
        }
        try{
            double magnitude;
            if(type==Sensor.TYPE_ROTATION_VECTOR){
                float[] currentRotation=new float[9];
                SensorManager.getRotationMatrixFromVector(currentRotation,event.values);
                if(previousRotationMatrix==null){previousRotationMatrix=currentRotation;return;}
                double trace=0;
                for(int i=0;i<9;i++)trace+=previousRotationMatrix[i]*currentRotation[i];
                previousRotationMatrix=currentRotation;
                double cos=(trace-1.0)/2.0;
                magnitude=Math.toDegrees(Math.acos(Math.max(-1.0,Math.min(1.0,cos))));
            }else{
                magnitude=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
            }
            JSONObject sample=new JSONObject();
            sample.put("clip",currentRecordingName);
            sample.put("relativeMs",Math.max(0,now-recordingStartedElapsedMs));
            sample.put("type",type==Sensor.TYPE_GYROSCOPE?"gyro_rad_s":type==Sensor.TYPE_ROTATION_VECTOR?"rotation_step_deg":"accel_m_s2");
            sample.put("accuracy",event.accuracy);
            sample.put("magnitude",magnitude);
            synchronized(sampleLock){if(motionSamples.length()<12000)motionSamples.put(sample);}
        }catch(Exception ignored){}
    }
    @Override public void onAccuracyChanged(Sensor sensor,int accuracy) {}
    @Override protected void onPause(){
        super.onPause();
        if(motionManager!=null)motionManager.unregisterListener(this);
        if(recording||starting)stopVideo();
        closeCamera();
    }
    @Override protected void onResume(){
        super.onResume();
        if(motionManager!=null){
            if(gyroSensor!=null)motionManager.registerListener(this,gyroSensor,SensorManager.SENSOR_DELAY_GAME,background);
            if(accelSensor!=null)motionManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_GAME,background);
            if(rotationSensor!=null)motionManager.registerListener(this,rotationSensor,SensorManager.SENSOR_DELAY_GAME,background);
        }
        if(manager!=null&&cameraId!=null&&preview!=null&&preview.isAvailable())openCamera();
    }
    @Override protected void onDestroy(){
        destroyed=true;
        if(recorder!=null)stopVideo();
        closeCamera();
        if(thread!=null)thread.quitSafely();
        super.onDestroy();
    }
}
