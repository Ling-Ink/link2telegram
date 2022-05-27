package org.crystal.link2telegram.Utils;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;

public class GetSystemStatus {
    static OperatingSystemMXBean OSMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    static String OS;
    static int CPULoad;
    static int MemoryLoad;
    static long usedSpaceG;
    static long totalSpaceG;

    public static Object Get(boolean Format){
        // Format Return
        if(Format){ return GetOSType() + GetCPUUsage() + GetMemoryUsage() + GetDiskUsage(); }
        else { return new Object[]{OS, CPULoad, MemoryLoad, usedSpaceG, totalSpaceG}; }
    }
    private static String GetOSType(){
        OS = System.getProperty("os.name");
        return "OS:" + OS + "\n";
    }
    private static String GetCPUUsage(){
        CPULoad = (int) (OSMXBean.getProcessCpuLoad() * 100);
        return "CPU:" + Formatter.ProgressBar(CPULoad, 100) + CPULoad + "%\n";
    }
    private static String GetMemoryUsage(){
        double totalVirtualMemory = OSMXBean.getTotalMemorySize();
        double freePhysicalMemorySize = OSMXBean.getFreeMemorySize();
        double value = freePhysicalMemorySize / totalVirtualMemory;
        MemoryLoad = (int) ((1 - value) * 100);
        return "Memory:" + Formatter.ProgressBar(MemoryLoad, 100) + MemoryLoad + "%\n";
    }
    private static String GetDiskUsage(){
        StringBuilder DiskMessage = new StringBuilder();
        File[] roots = File.listRoots();// Get disk root list
        File home;
        for (File file : roots) {
            long free = file.getFreeSpace();
            long total = file.getTotalSpace();
            long use = total - free;
            usedSpaceG = use / 1024 / 1024 / 1024;
            totalSpaceG = total / 1024 / 1024 / 1024;
            DiskMessage.append("Location:\n")
                    .append("   ").append("Root Path:").append(file.getPath()).append("\n")
                    .append("      ").append("Used Disk:").append(usedSpaceG).append("G / ").append(totalSpaceG).append("G").append("\n");
        }
        try{
            home = new File("/home");
            long free = home.getFreeSpace();
            long total = home.getTotalSpace();
            long use = total - free;
            usedSpaceG = use / 1024 / 1024 / 1024;
            totalSpaceG = total / 1024 / 1024 / 1024;
            DiskMessage.append("   ").append("Root Path:").append(home.getPath()).append("\n")
                    .append("      ").append("Used Disk:").append(usedSpaceG).append("G / ").append(totalSpaceG).append("G").append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DiskMessage.toString();
    }
}
