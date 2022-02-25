package org.crystal.link2telegram.Utils;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class GetSystemStatus {
    public static Object Get(boolean Format){
        OperatingSystemMXBean OSMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int MemoryLoad;
        int CPULoad;
        double cpu = OSMXBean.getProcessCpuLoad();
        CPULoad = (int) (cpu * 100);
        double totalVirtualMemory = OSMXBean.getTotalMemorySize();
        double freePhysicalMemorySize = OSMXBean.getFreeMemorySize();
        double value = freePhysicalMemorySize / totalVirtualMemory;
        MemoryLoad =  (int) ((1 - value) * 100);
        String OS = System.getProperty("os.name");
        if(Format){
            return "OS:" + OS + "\n" +
                    "CPU:" + Formatter.ProgressBar(CPULoad,100) + CPULoad + "%\n" +
                    "Memory:" + Formatter.ProgressBar(MemoryLoad,100) + MemoryLoad + "%";
        }
        else { return new Object[]{OS, CPULoad, MemoryLoad}; }
    }
}
