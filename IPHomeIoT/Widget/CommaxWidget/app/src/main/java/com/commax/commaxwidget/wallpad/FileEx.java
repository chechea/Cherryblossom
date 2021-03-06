package com.commax.commaxwidget.wallpad;

import android.os.Environment;
import android.util.Log;

import com.commax.commaxwidget.wallpad.constans.NameSpace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileEx {
    private final String DEBUG_TAG = "FileEx";

    public String[] readFile(String fileName) throws FileNotFoundException,
            IOException {
        String[] returnValue = new String[]{};

        File f = new File(fileName);
        if (f.exists() == false) {
            return returnValue;
        }
        if (f.canRead() == false) {
            return returnValue;
        }

        FileInputStream fis = null;
        BufferedReader br = null;

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis));
        String temp;
        ArrayList<String> list = new ArrayList<String>();
        while ((temp = br.readLine()) != null) {

            list.add(temp.trim());
        }
        returnValue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            returnValue[i] = list.get(i);
        }

        if (br != null) {
            try {
                br.close();
            } catch (Exception e) {
                // Log.e(TAG, e.toString());
            }
        }
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                // Log.e(TAG, e.toString());
            }
        }
        return returnValue;
    }

    public String[] readFolder(String path) {
        String[] returnValue = new String[]{};

        File f = new File(path);
        File[] files = f.listFiles();
        if (files == null) {
            return returnValue;
        }

        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                continue;
            }
            list.add(file.getName());
        }
        returnValue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            returnValue[i] = list.get(i);
        }
        return returnValue;
    }

    public String getNetworkState() {

        String network_state = "";
        try {
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = dirPath + NameSpace.NETWORK_CHECK_PATH;
            String[] files = null;

            try {
                files = readFile(fileName);
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
            } catch (IOException e) {
                // e.printStackTrace();
            }

            if (files == null) {
                return network_state;
            }

            if (files.length > 0) {
                // ���� üũ
                if (files == null) {
                    return network_state;
                }
                if ("".equals(files[0])) {
                    return network_state;
                }
                if ("-1".equals(files[0])) {
                    return network_state;
                }
            }

            for (int i = 0; i < files.length; i++) {
                String line = files[i];

                try {
                    if (line.contains(NameSpace.NETWORK_STATE_KEY)) {
                        network_state = line.replace(NameSpace.NETWORK_STATE_KEY + "=", "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(DEBUG_TAG, "getNetworkState " + network_state);
        return network_state;
    }
}
