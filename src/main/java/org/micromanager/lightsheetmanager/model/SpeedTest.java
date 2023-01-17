package org.micromanager.lightsheetmanager.model;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SpeedTest  {

   private ArrayList<int[]> queueData = new ArrayList<>();
   private ArrayList<long[]> imageData = new ArrayList<>();
   private long startTime = System.currentTimeMillis();

   public SpeedTest(int circBufferSizeMax, int acqEngOutputQueueSize, int writingTaskQueueSizeMax) {
      queueData.add(new int[]{circBufferSizeMax, acqEngOutputQueueSize, writingTaskQueueSizeMax});
   }

    public void logStatus(long time, int circularBuffer, int acqEngOutput, int writingTask){
       queueData.add(new int[]{(int) (time - startTime), circularBuffer, acqEngOutput, writingTask});
    }

    public void save(String queuePath, String dataPath, int bytesPerImage) throws IOException {
       File csvFile = new File(queuePath);
       FileWriter fileWriter = new FileWriter(csvFile);

       //write header line here if you need.

       int[] max = queueData.remove(0);
       fileWriter.write("," + max[0] + "," + max[1] + "," + max[2] + "\n");

       for (int[] line : queueData) {
          fileWriter.write(line[0] + "," + line[1] + "," + line[2] + "," + line[3] + "\n");
       }
       fileWriter.close();


       csvFile = new File(dataPath);
       fileWriter = new FileWriter(csvFile);

       for (long[] line : imageData) {
          fileWriter.write(line[0] + "," + (bytesPerImage * line[1]) + "\n");
       }
       fileWriter.close();

    }


   public void imageWritten(int count) {
      imageData.add(new long[]{System.currentTimeMillis(), count});
   }
}