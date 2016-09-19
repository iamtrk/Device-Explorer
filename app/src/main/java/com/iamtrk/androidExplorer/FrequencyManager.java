package com.iamtrk.androidExplorer;

/**
 * Created by m01231 on 20/08/16.
 */
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Manager class to read the processor usage from the cpufreq file and return the processor frequency
 */
public class FrequencyManager {

    /**
     * The frequency statistics file from which the figures should be read repeatedly
     */
    private RandomAccessFile rafFrequency;

    public FrequencyManager() {
        try {
            rafFrequency = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the current CPU frequency in a human readable format by converting it into a Hz
     *
     * @return The current CPU frequency
     */
    public String getFrequency() {
        try {
            rafFrequency.seek(0);
            int hz = Integer.valueOf(rafFrequency.readLine());
            if (hz < 1000 * 1000) {
                return (hz / 1000) + " MHz";
            }

            final int a = (hz / 1000 / 1000);
            final int b = (hz / 1000 / 100) % 10;
            return a + "." + b + " GHz";
        } catch (IOException e) {
            Log.w("FrequencyManager", "Error reading the CPU frequency", e);
            return "";
        }
    }

    /**
     * Closes the processor statistics file from which the figures are be read repeatedly
     */
    public void destroy() {

        if (rafFrequency != null) {
            try {
                rafFrequency.close();
            } catch (IOException e) {
                Log.w("FrequencyManager", "Unable to successfully close the file");
            }
        }
    }
}

