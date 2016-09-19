package com.iamtrk.androidExplorer;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Manager class to read the processor usage from the /proc/stat file and return the processor usage
 */
public class ProcessorManager {

    /**
     * The processor statistics file from which the figures should be read repeatedly
     */
    private RandomAccessFile rafProcessor;
    /**
     * The value of the total processor utilization since the last update
     */
    private long lngPreviousTotal = 0L;
    /**
     * The value of the idle processor utilization since the last update
     */
    private long lngPreviousIdle = 0L;

    public ProcessorManager() {
        try {
            rafProcessor = new RandomAccessFile("/proc/stat", "r");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handler method that updates the notification icon with the current processor usage. It does
     * this by reading the /proc/stat file and specifically the of the first CPU row as are only
     * concerned with the cumulative processor utilization.
     */
    public double getUsage() {
        try {
            rafProcessor.seek(0);
            String strLine = rafProcessor.readLine();
            Log.v("HardwareService", strLine);
            String[] lstColumns = strLine.split(" ");

            long lngCurrentIdle = Long.parseLong(lstColumns[5]);
            long lngCurrentTotal = 0L;

            lstColumns[0] = lstColumns[1] = "0";

            for (String strStatistic : lstColumns) {
                lngCurrentTotal = lngCurrentTotal + Integer.parseInt(strStatistic);
            }
            long lngDifferenceIdle = lngCurrentIdle - lngPreviousIdle;
            long lngDifferenceTotal = lngCurrentTotal - lngPreviousTotal;

            lngPreviousIdle = lngCurrentIdle;
            lngPreviousTotal = lngCurrentTotal;

            long lngUsageDelta = lngDifferenceTotal - lngDifferenceIdle;

            Double dblPercent = 100.0 * (lngUsageDelta / (lngDifferenceTotal + 0.01));
            if (dblPercent < 0D) {
                Log.w("ProcessorManager", "Encountered a negative value of " + dblPercent);
                dblPercent = 0D;
            } else if (dblPercent > 100D) {
                Log.w("ProcessorManager", "Encountered a insane value of " + dblPercent);
                dblPercent = 100D;
            }
            Log.d("HardwareService", "Current processor usage is " + dblPercent);
            return dblPercent;

        } catch (Exception e) {
            Log.e("HardwareService", "Error processing line", e);
        }

        return 0D;
    }

    /**
     * Closes the processor statistics file from which the figures are be read repeatedly
     */
    public void destroy() {

        if (rafProcessor != null) {
            try {
                rafProcessor.close();
            } catch (IOException e) {
                Log.w("HardwareService", "Unable to successfully close the file");
            }
        }
    }
}

