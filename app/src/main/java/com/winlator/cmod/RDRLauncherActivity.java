package com.winlator.cmod;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.winlator.cmod.container.Container;
import com.winlator.cmod.container.ContainerManager;
import com.winlator.cmod.container.Shortcut;
import com.winlator.cmod.core.KeyValueSet;
import com.winlator.cmod.inputcontrols.ControllerManager;
import com.winlator.cmod.xenvironment.ImageFsInstaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class RDRLauncherActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private boolean permissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ControllerManager.getInstance().init(getApplicationContext());
        
        if (checkPermissions()) {
            permissionsGranted = true;
            initializeAndLaunch();
        } else {
            requestAppPermissions();
        }
    }

    private boolean checkPermissions() {
        boolean hasWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean hasReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return hasWritePermission && hasReadPermission;
    }

    private void requestAppPermissions() {
        String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
                initializeAndLaunch();
            } else {
                Toast.makeText(this, "Permissões necessárias não foram concedidas", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initializeAndLaunch() {
        RDRConfigHelper.applyRDROptimizedSettings(this);
        ImageFsInstaller.installIfNeeded(this, () -> {
            setupRDRContainerAndLaunch();
        });
    }

    private void setupRDRContainerAndLaunch() {
        new Thread(() -> {
            try {
                OBBManager obbManager = new OBBManager(this);
                
                if (!obbManager.isOBBInstalled()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "OBB do jogo não encontrado. Por favor, instale o arquivo OBB em: " + 
                                      obbManager.getOBBDir().getAbsolutePath(), Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }
                
                File rdrExe = obbManager.getGameExecutable();
                if (!rdrExe.exists()) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Jogo não encontrado em " + rdrExe.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        finish();
                    });
                    return;
                }
                
                ContainerManager containerManager = new ContainerManager(this);
                Container rdrContainer = null;
                
                for (Container container : containerManager.getContainers()) {
                    if ("RDR1".equals(container.getName())) {
                        rdrContainer = container;
                        break;
                    }
                }
                
                if (rdrContainer == null) {
                    rdrContainer = createRDRContainer(containerManager);
                }
                
                obbManager.linkGameToWinePrefix(rdrContainer.getRootDir());
                
                Shortcut rdrShortcut = createRDRShortcut(rdrContainer);
                
                final Container finalContainer = rdrContainer;
                final Shortcut finalShortcut = rdrShortcut;
                
                runOnUiThread(() -> {
                    Intent intent = new Intent(RDRLauncherActivity.this, XServerDisplayActivity.class);
                    intent.putExtra("container_id", finalContainer.getId());
                    intent.putExtra("shortcut_path", finalShortcut.file.getAbsolutePath());
                    startActivity(intent);
                    finish();
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro ao inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }

    private Container createRDRContainer(ContainerManager containerManager) throws JSONException {
        JSONObject containerData = new JSONObject();
        containerData.put("id", 1);
        containerData.put("name", "RDR1");
        containerData.put("screenSize", "1280x720");
        containerData.put("envVars", RDRConfigHelper.getEnvVars());
        
        containerData.put("graphicsDriverConfig", RDRConfigHelper.getGraphicsDriverConfig().toString());
        containerData.put("graphicsDriver", "turnip");
        containerData.put("audioDriver", "alsa-reflector");
        
        containerData.put("dxwrapperConfig", RDRConfigHelper.getDXVKConfig().toString());
        containerData.put("dxwrapper", "dxvk");
        containerData.put("emulator", "FEXCore");
        
        containerData.put("extraData", RDRConfigHelper.getFEXCoreConfig().toString());
        
        containerData.put("startupSelection", "Aggressive");
        containerData.put("showFPS", false);
        containerData.put("fullscreenStretched", true);
        containerData.put("box64Preset", "Performance");
        containerData.put("wincomponents", RDRConfigHelper.getWineComponents());
        containerData.put("drives", RDRConfigHelper.getDrives());
        
        Container container = new Container(this, containerData);
        containerManager.addContainer(container);
        
        RDRConfigHelper.createFEXCoreConfigFile(container.getRootDir());
        
        return container;
    }

    private Shortcut createRDRShortcut(Container container) {
        File desktopDir = new File(container.getRootDir(), ".wine/drive_c/users/Public/Desktop");
        if (!desktopDir.exists()) {
            desktopDir.mkdirs();
        }
        
        File shortcutFile = new File(desktopDir, "RDR1.desktop");
        
        String windowsPath = "C:\\Games\\Red Dead Redemption\\RDR.exe";
        
        String desktopContent = "[Desktop Entry]\n" +
                "Encoding=UTF-8\n" +
                "Type=Application\n" +
                "Exec=env wine " + windowsPath + "\n" +
                "Icon=\n" +
                "Name=Red Dead Redemption\n" +
                "StartupWMClass=rdr.exe\n" +
                "\n" +
                "[Extra Data]\n" +
                "graphicsDriver=turnip\n" +
                "audioDriver=alsa-reflector\n" +
                "dxwrapper=dxvk\n" +
                "emulator=FEXCore\n" +
                "box64Preset=Performance\n";
        
        try {
            java.io.FileWriter writer = new java.io.FileWriter(shortcutFile);
            writer.write(desktopContent);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new Shortcut(container, shortcutFile);
    }
}
