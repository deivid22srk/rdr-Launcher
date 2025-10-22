package com.winlator.cmod;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.winlator.cmod.core.KeyValueSet;
import com.winlator.cmod.fexcore.FEXCoreManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RDRConfigHelper {
    
    public static void applyRDROptimizedSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        
        editor.putBoolean("enable_wine_debug", false);
        editor.putBoolean("enable_box86_64_logs", false);
        editor.putBoolean("use_dri3", true);
        editor.putBoolean("gyro_enabled", false);
        editor.putBoolean("touchscreen_timeout_enabled", true);
        editor.putString("trigger_type", "0");
        
        editor.apply();
    }
    
    public static KeyValueSet getGraphicsDriverConfig() {
        KeyValueSet config = new KeyValueSet();
        config.put("version", "turnip-25.1.0");
        config.put("adrenotoolsTurnip", "1");
        config.put("frameSync", "Normal");
        config.put("blacklistedExtensions", "");
        config.put("maxDeviceMemory", "0");
        return config;
    }
    
    public static KeyValueSet getDXVKConfig() {
        KeyValueSet config = new KeyValueSet();
        config.put("version", "2.3.1-arm64ec-gplasync");
        config.put("async", "1");
        config.put("asyncCache", "1");
        config.put("framerate", "0");
        config.put("maxDeviceMemory", "0");
        return config;
    }
    
    public static KeyValueSet getFEXCoreConfig() {
        KeyValueSet config = new KeyValueSet();
        config.put("fexcoreVersion", "2508");
        config.put("tsoMode", "Fastest");
        config.put("x87Mode", "Fast");
        config.put("multiBlock", "Enabled");
        return config;
    }
    
    public static void createFEXCoreConfigFile(File containerDir) {
        try {
            File fexConfigFile = new File(containerDir, ".fex-emu/Config.json");
            fexConfigFile.getParentFile().mkdirs();
            
            JSONObject config = new JSONObject();
            JSONObject settings = new JSONObject();
            
            settings.put("Multiblock", "1");
            settings.put("TSOEnabled", "0");
            settings.put("VectorTSOEnabled", "0");
            settings.put("MemcpySetTSOEnabled", "0");
            settings.put("HalfBarrierTSOEnabled", "0");
            settings.put("X87ReducedPrecision", "1");
            
            config.put("Config", settings);
            
            FileWriter writer = new FileWriter(fexConfigFile);
            writer.write(config.toString(2));
            writer.close();
            
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    
    public static String getEnvVars() {
        return "ZINK_DESCRIPTORS=lazy " +
               "ZINK_DEBUG=compact " +
               "MESA_SHADER_CACHE_DISABLE=false " +
               "MESA_SHADER_CACHE_MAX_SIZE=2G " +
               "mesa_glthread=true " +
               "GALLIUM_DRIVER=zink " +
               "MESA_NO_ERROR=1 " +
               "vblank_mode=0 " +
               "DXVK_ASYNC=1 " +
               "DXVK_GPLASYNCCACHE=1 " +
               "__GL_THREADED_OPTIMIZATIONS=1";
    }
    
    public static String getWineComponents() {
        return "d3dx9:vkd3d:arial:times:cjkfonts:liberation:vcrun2019";
    }
    
    public static String getDrives() {
        return "D:,/storage/emulated/0";
    }
}
