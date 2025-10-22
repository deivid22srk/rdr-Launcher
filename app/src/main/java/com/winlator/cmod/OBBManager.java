package com.winlator.cmod;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OBBManager {
    private static final String TAG = "OBBManager";
    private Context context;
    
    public OBBManager(Context context) {
        this.context = context;
    }
    
    public File getOBBDir() {
        String packageName = context.getPackageName();
        File obbDir = new File(Environment.getExternalStorageDirectory(), 
                              "Android/obb/" + packageName);
        return obbDir;
    }
    
    public File getMainOBBFile() {
        File obbDir = getOBBDir();
        if (!obbDir.exists()) {
            return null;
        }
        
        File[] obbFiles = obbDir.listFiles((dir, name) -> name.endsWith(".obb"));
        if (obbFiles != null && obbFiles.length > 0) {
            return obbFiles[0];
        }
        
        return null;
    }
    
    public boolean isOBBInstalled() {
        File obbFile = getMainOBBFile();
        return obbFile != null && obbFile.exists();
    }
    
    public File getGameDirectory() {
        File obbDir = getOBBDir();
        return new File(obbDir, "Red Dead Redemption");
    }
    
    public File getGameExecutable() {
        File gameDir = getGameDirectory();
        return new File(gameDir, "RDR.exe");
    }
    
    public boolean extractOBB(File obbFile, ProgressCallback callback) {
        if (!obbFile.exists()) {
            Log.e(TAG, "OBB file does not exist: " + obbFile.getAbsolutePath());
            return false;
        }
        
        File obbDir = getOBBDir();
        if (!obbDir.exists()) {
            obbDir.mkdirs();
        }
        
        try {
            FileInputStream fis = new FileInputStream(obbFile);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry entry;
            
            long totalBytes = obbFile.length();
            long extractedBytes = 0;
            
            while ((entry = zis.getNextEntry()) != null) {
                File outputFile = new File(obbDir, entry.getName());
                
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    outputFile.getParentFile().mkdirs();
                    
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[8192];
                    int len;
                    
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                        extractedBytes += len;
                        
                        if (callback != null) {
                            int progress = (int) ((extractedBytes * 100) / totalBytes);
                            callback.onProgress(progress);
                        }
                    }
                    
                    fos.close();
                }
                
                zis.closeEntry();
            }
            
            zis.close();
            fis.close();
            
            if (callback != null) {
                callback.onComplete();
            }
            
            return true;
            
        } catch (IOException e) {
            Log.e(TAG, "Error extracting OBB", e);
            if (callback != null) {
                callback.onError(e.getMessage());
            }
            return false;
        }
    }
    
    public void linkGameToWinePrefix(File winePrefixDir) {
        File gameDir = getGameDirectory();
        if (!gameDir.exists()) {
            Log.e(TAG, "Game directory does not exist: " + gameDir.getAbsolutePath());
            return;
        }
        
        File driveC = new File(winePrefixDir, ".wine/drive_c");
        if (!driveC.exists()) {
            driveC.mkdirs();
        }
        
        File gamesDir = new File(driveC, "Games");
        if (!gamesDir.exists()) {
            gamesDir.mkdirs();
        }
        
        File rdrLink = new File(gamesDir, "Red Dead Redemption");
        
        if (rdrLink.exists()) {
            rdrLink.delete();
        }
        
        try {
            Runtime.getRuntime().exec("ln -s " + gameDir.getAbsolutePath() + " " + rdrLink.getAbsolutePath()).waitFor();
        } catch (Exception e) {
            Log.e(TAG, "Failed to create symlink", e);
            
            copyDirectory(gameDir, rdrLink);
        }
    }
    
    private void copyDirectory(File sourceDir, File destDir) {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    copyDirectory(file, destFile);
                } else {
                    copyFile(file, destFile);
                }
            }
        }
    }
    
    private void copyFile(File source, File dest) {
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(dest);
            
            byte[] buffer = new byte[8192];
            int length;
            
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            
            in.close();
            out.close();
        } catch (IOException e) {
            Log.e(TAG, "Error copying file", e);
        }
    }
    
    public interface ProgressCallback {
        void onProgress(int progress);
        void onComplete();
        void onError(String error);
    }
}
