package com.samsung.portalserver.schedule;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

public class ResourceManager {
    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    /**
     *
     * @param path: directory path
     * @return usable storage size (Gigabytes)
     */
    public int getUsableStorageSize(String path) {
        File file = new File(path);
        try {
            return convertByteToGB(file.getUsableSpace());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     *
     * @param path: directory path
     * @return usable storage ratio
     */
    public double getUsableStorageRatio(String path) {
        File file = new File(path);
        return Math.round((1 - ((double) file.getUsableSpace() / file.getTotalSpace())) * 100) / 100.0;
    }

    private int convertByteToGB(long b) {
        return (int) (b / (1024 * 1024 * 1024));
    }

    /**
     *
     * @return System cpu usage (Percentage)
     */
    public int getCpuUsage() {
        return (int) Math.round(osBean.getSystemCpuLoad() * 100);
    }

    /**
     *
     * @return System memory usage (Percentage)
     */
    public int getMemoryUsage() {
        return (int) ((1 - ((double) osBean.getFreePhysicalMemorySize() / osBean.getTotalPhysicalMemorySize()))*100);
    }
}
