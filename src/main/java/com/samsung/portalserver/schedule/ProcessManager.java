package com.samsung.portalserver.schedule;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessManager {
    public static long findProcessIdByInstance(Process p) {
        long pid = -1;

        try {
            if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            } else if (p.getClass().getName().equals("java.lang.Win32Process") ||
                    p.getClass().getName().equals("java.lang.ProcessImpl")) {
                Field f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                long handl = f.getLong(p);
                Kernel32 kernel = Kernel32.INSTANCE;
                WinNT.HANDLE hand = new WinNT.HANDLE();
                hand.setPointer(Pointer.createConstant(handl));
                pid = kernel.GetProcessId(hand);
                f.setAccessible(false);
            }
        } catch (Exception e) {
            pid = -1;
        }
        return pid;
    }

    public static Map<Integer, Boolean> findProcessIdByName(String name) {
        Map<Integer, Boolean> processIds = new ConcurrentHashMap<>();

        Runtime rt = Runtime.getRuntime();
        try {
            Process p = rt.exec("jps -l");

            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while (true) {
                String processInfo = br.readLine();
                if (processInfo==null)
                    break;

                String[] splited = processInfo.split(" ");
                Integer pid = Integer.parseInt(splited[0]);

                String processName = "";
                for (int i = 1; i < splited.length; i++) {
                    processName += splited[i];
                }

                if (processName.equals(name)) {
                    processIds.put(pid, true);
                }

                System.out.println(processInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return processIds;
    }
}
