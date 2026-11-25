package com.nightcapture.lab;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraExtensionCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.graphics.ImageFormat;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.PowerManager;
import android.util.Range;
import android.util.Size;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Arrays;

/**
 * This inventory contains what the Android HAL advertises. A reported feature
 * is NOT proof that it operates correctly on the user's Redmi 15C.
 */
final class Diagnostic {
    private Diagnostic() { }
    static Object readable(Object o) {
        if (o == null) return JSONObject.NULL;
        if (o instanceof int[]) return ints((int[])o);
        if (o instanceof long[]) { JSONArray a=new JSONArray(); for (long x:(long[])o) a.put(x); return a; }
        if (o instanceof float[]) { JSONArray a=new JSONArray(); for (float x:(float[])o) a.put(Float.valueOf(x)); return a; }
        return o.toString();
    }
    static JSONArray ints(int[] data) {
        JSONArray a=new JSONArray();
        if (data != null) for (int x:data) a.put(x);
        return a;
    }
    static JSONArray sizes(Size[] data) {
        JSONArray a=new JSONArray();
        if (data != null) for (Size x:data) a.put(x.getWidth()+"x"+x.getHeight());
        return a;
    }
    static JSONArray fps(Range<Integer>[] data) {
        JSONArray a=new JSONArray();
        if (data != null) for (Range<Integer> x:data) a.put(x.getLower()+"-"+x.getUpper());
        return a;
    }
    static JSONArray keyNames(Iterable<?> keys) {
        JSONArray a=new JSONArray();
        if (keys != null) for (Object k:keys) a.put(k.toString());
        return a;
    }
    static JSONObject inventory(Context context) throws Exception {
        JSONObject root=new JSONObject();
        root.put("format","Vision Lab camera diagnostic");
        root.put("appVersion",context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName);
        root.put("disclaimer","Android-reported capabilities are not proof of working features; confirm on device.");
        JSONObject device=new JSONObject();
        device.put("manufacturer",Build.MANUFACTURER);
        device.put("model",Build.MODEL);
        device.put("device",Build.DEVICE);
        device.put("product",Build.PRODUCT);
        device.put("board",Build.BOARD);
        device.put("hardware",Build.HARDWARE);
        device.put("sdk",Build.VERSION.SDK_INT);
        device.put("release",Build.VERSION.RELEASE);
        device.put("incremental",Build.VERSION.INCREMENTAL);
        root.put("device",device);
        CameraManager cm=(CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        JSONArray cameras=new JSONArray();
        for (String id:cm.getCameraIdList()) {
            try { cameras.put(camera(cm,id)); }
            catch (Throwable e) {
                JSONObject err=new JSONObject();
                err.put("cameraId",id);
                err.put("error",e.getClass().getSimpleName()+": "+e.getMessage());
                cameras.put(err);
            }
        }
        root.put("cameras",cameras);
        root.put("codecs",codecs());
        JSONObject gpu=new JSONObject();
        ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am!=null) {
            android.content.pm.ConfigurationInfo info=am.getDeviceConfigurationInfo();
            if(info!=null) gpu.put("openGlEsVersion",info.getGlEsVersion());
        }
        JSONArray features=new JSONArray();
        FeatureInfo[] fs=context.getPackageManager().getSystemAvailableFeatures();
        if(fs!=null) for(FeatureInfo f:fs) {
            if(f.name!=null && (f.name.contains("vulkan") || f.name.contains("camera"))) features.put(f.name);
        }
        gpu.put("relatedFeatures",features);
        root.put("graphics",gpu);
        JSONObject motion=new JSONObject();
        SensorManager sm=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        motion.put("gyroscopeAvailable",sm!=null && sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null);
        motion.put("accelerometerAvailable",sm!=null && sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null);
        motion.put("rotationVectorAvailable",sm!=null && sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)!=null);
        root.put("motionSensors",motion);
        PowerManager pm=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if(pm!=null && Build.VERSION.SDK_INT>=29) root.put("thermalStatusAtExport",pm.getCurrentThermalStatus());
        return root;
    }
    static JSONObject camera(CameraManager manager,String id) throws Exception {
        CameraCharacteristics c=manager.getCameraCharacteristics(id);
        JSONObject j=new JSONObject();
        j.put("id",id);
        j.put("lensFacing",readable(c.get(CameraCharacteristics.LENS_FACING)));
        j.put("hardwareLevel",readable(c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)));
        j.put("orientation",readable(c.get(CameraCharacteristics.SENSOR_ORIENTATION)));
        j.put("pixelArray",readable(c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)));
        j.put("activeArray",readable(c.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)));
        j.put("physicalSizeMm",readable(c.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)));
        j.put("sensitivityRangeISO",readable(c.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)));
        j.put("exposureTimeRangeNs",readable(c.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)));
        j.put("maxFrameDurationNs",readable(c.get(CameraCharacteristics.SENSOR_INFO_MAX_FRAME_DURATION)));
        j.put("lensApertures",readable(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)));
        j.put("focalLengths",readable(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)));
        j.put("capabilities",ints(c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)));
        int[] aeModes=c.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        j.put("aeModes",ints(aeModes));
        boolean llb=false;
        if(aeModes!=null) for(int x:aeModes) {
            if(x==android.hardware.camera2.CameraMetadata.CONTROL_AE_MODE_ON_LOW_LIGHT_BOOST_BRIGHTNESS_PRIORITY) llb=true;
        }
        j.put("lowLightBoostAdvertised",llb);
        j.put("availableAeFpsRanges",fps(c.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)));
        j.put("afModes",ints(c.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)));
        j.put("stabilizationModes",ints(c.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)));
        j.put("noiseReductionModes",ints(c.get(CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES)));
        j.put("opticalStabilizationModes",ints(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)));
        j.put("rawRequestKeys",keyNames(c.getAvailableCaptureRequestKeys()));
        j.put("rawResultKeys",keyNames(c.getAvailableCaptureResultKeys()));
        StreamConfigurationMap map=c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if(map!=null) {
            JSONObject output=new JSONObject();
            output.put("video",sizes(map.getOutputSizes(MediaRecorder.class)));
            output.put("jpeg",sizes(map.getOutputSizes(ImageFormat.JPEG)));
            output.put("yuv420",sizes(map.getOutputSizes(ImageFormat.YUV_420_888)));
            output.put("private",sizes(map.getOutputSizes(ImageFormat.PRIVATE)));
            j.put("outputSizes",output);
        }
        try {
            CameraExtensionCharacteristics ext=manager.getCameraExtensionCharacteristics(id);
            j.put("cameraExtensions",ints(ext.getSupportedExtensions().stream().mapToInt(Integer::intValue).toArray()));
            j.put("nightExtensionAdvertised",ext.getSupportedExtensions().contains(CameraExtensionCharacteristics.EXTENSION_NIGHT));
        } catch(Throwable e) {
            j.put("extensionsError",e.getClass().getSimpleName()+": "+e.getMessage());
        }
        return j;
    }
    static JSONArray codecs() {
        JSONArray a=new JSONArray();
        try {
            for(MediaCodecInfo info:new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos()) {
                if(!info.isEncoder()) continue;
                for(String mime:info.getSupportedTypes()) {
                    if(!mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_AVC)
                            && !mime.equalsIgnoreCase(MediaFormat.MIMETYPE_VIDEO_HEVC)) continue;
                    JSONObject row=new JSONObject();
                    row.put("name",info.getName());
                    row.put("mime",mime);
                    if(Build.VERSION.SDK_INT>=29) row.put("hardwareAccelerated",info.isHardwareAccelerated());
                    try {
                        MediaCodecInfo.VideoCapabilities cap=info.getCapabilitiesForType(mime).getVideoCapabilities();
                        if(cap!=null) {
                            row.put("720p15",cap.areSizeAndRateSupported(1280,720,15));
                            row.put("720p30",cap.areSizeAndRateSupported(1280,720,30));
                            row.put("1080p15",cap.areSizeAndRateSupported(1920,1080,15));
                            row.put("1080p30",cap.areSizeAndRateSupported(1920,1080,30));
                            row.put("supportedWidths",cap.getSupportedWidths().toString());
                            row.put("supportedHeights",cap.getSupportedHeights().toString());
                        }
                    } catch(Throwable e) { row.put("detailError",String.valueOf(e)); }
                    a.put(row);
                }
            }
        } catch(Throwable e) { a.put("codecInventoryError="+e); }
        return a;
    }
}
