package org.crystal.link2telegram.Utils;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.*;

public class GetSystemStatus {
    public static Object Get(boolean Format){
        OperatingSystemMXBean OSMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // Get CPU Usage
        int MemoryLoad;
        int CPULoad;
        double cpu = OSMXBean.getProcessCpuLoad();
        CPULoad = (int) (cpu * 100);
        // Get Memory Usage
        double totalVirtualMemory = OSMXBean.getTotalMemorySize();
        double freePhysicalMemorySize = OSMXBean.getFreeMemorySize();
        double value = freePhysicalMemorySize / totalVirtualMemory;
        MemoryLoad =  (int) ((1 - value) * 100);
        // Get OS Type
        String OS = System.getProperty("os.name");
        // Get Disk Usage
        File file = new File(Arrays.toString(File.listRoots()));
        String Path = file.getPath();
        long totalSpace = file.getTotalSpace();
        long usableSpace = totalSpace - file.getFreeSpace();
        double spacePercent = (double) usableSpace / (double) totalSpace * 100;
        int usableSpaceG = (int) usableSpace / 1024 / 1024 / 1024;
        int totalSpaceG = (int) totalSpace / 1024 / 1024 / 1024;
        // Format Return
        if(Format){
            return  "OS:" + OS + "\n" +
                    "CPU:" + Formatter.ProgressBar(CPULoad,100) + CPULoad + "%\n" +
                    "Memory:" + Formatter.ProgressBar(MemoryLoad,100) + MemoryLoad + "%\n" +
                    "Disk:\n" +
                    "   " + "Root Path:" + Path + "\n" +
                    "   " + "Used Disk:" + usableSpaceG + "G / " + totalSpaceG + "G" + "\n" +
                    "   " + Formatter.ProgressBar(spacePercent,100) + spacePercent + "%\n";
        }
        else { return new Object[]{OS, CPULoad, MemoryLoad, usableSpaceG, totalSpaceG}; }
    }
}
