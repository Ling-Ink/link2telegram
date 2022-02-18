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
        if(Format){ return "CPU:" + CPULoad + "%\n" + "Memory:" + MemoryLoad + "%"; }
        else { return new int[]{CPULoad, MemoryLoad}; }
    }
}
