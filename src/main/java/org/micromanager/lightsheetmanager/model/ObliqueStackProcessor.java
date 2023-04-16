package org.micromanager.lightsheetmanager.model;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

public class ObliqueStackProcessor {

   public static final int XY_PROJECTION = 0;
   public static final int OTHOGONAL_VIEWS = 1;
   public static final int FULL_VOLUME = 2;

   private double theta;
   private int zStackSlices_, cameraImageWidth_, cameraImageHeight_;
   private double cameraPixelSizeXY_um_, zStep_um_;
   private double[] imageYCoords_um_, imageZCoords_um_;
   private double reconstructionVoxelSize_um_;
   private int reconstructedImageWidth_, reconstructedImageHeight_, reconstructedImageDepth_;
   private final int[][] destYCoordLUT_, destZCoordLUT_;

   int[][] sumProjectionYX_ = null, sumProjectionYZ_ = null, sumProjectionXZ_ = null;
   short[] meanProjectionYX_ = null, meanProjectionYZ_ = null, meanProjectionXZ_ = null;
   short[][][] reconVolume_ = null;

   final int[][] pixelCountSumProjectionYX_;
//   , pixelCountSumProjectionYZ_ = null,
//         pixelCountSumProjectionXZ_ = null;
   short[][][] pixelCountReconVolume_ = null;

//   private AtomicInteger[][] sumProjectionXY_;
//   private AtomicInteger[][] pixelCountSumProjectionXY_;
//   private AtomicInteger[][] sumProjectionYZ_;
//   private AtomicInteger[][] pixelCountSumProjectionYZ_;
//   private AtomicInteger[][] sumProjectionXZ_;
//   private AtomicInteger[][] pixelCountSumProjectionXZ_;
//   private AtomicInteger[][][] reconVolume_;
//   private AtomicInteger[][][] pixelCountReconVolume_;


   public ObliqueStackProcessor(double theta, double cameraPixelSizeXyUm, double zStep_um,
                                int zStackSlices, int cameraImageWidth, int cameraImageHeight) {
      this.theta = theta;
      this.zStackSlices_ = zStackSlices;
      this.cameraImageWidth_ = cameraImageWidth;
      this.cameraImageHeight_ = cameraImageHeight;
      this.cameraPixelSizeXY_um_ = cameraPixelSizeXyUm;
      this.zStep_um_ = zStep_um;
      computeRemappedCoordinateSpace();
      destYCoordLUT_ = new int[zStackSlices_][cameraImageHeight_];
      destZCoordLUT_ = new int[zStackSlices_][cameraImageHeight_];
      precomputeCoordTransformLUTs();


      pixelCountSumProjectionYX_ =  new int[reconstructedImageHeight_][reconstructedImageWidth_];
      for (int y = 0; y < reconstructedImageHeight_; y++) {
         for (int x = 0; x < reconstructedImageWidth_; x++) {
            pixelCountSumProjectionYX_[y][x] += 1;
         }
      }
   }

   public int getReconstructedImageWidth() {
      return reconstructedImageWidth_;
   }

   public int getReconstructedImageHeight() {
      return reconstructedImageHeight_;
   }

   public int getReconstructedImageDepth() {
      return reconstructedImageDepth_;
   }

   private double[] computeStageCoordsUm(double imageZUm, double cameraYRelative_um) {
      double delta = Math.tan(theta) * imageZUm;
      double cameraYAbsolute_um = cameraYRelative_um + delta;
      double stageYUm = cameraYAbsolute_um * Math.sin(theta);
      double stageZUm = imageZUm * Math.cos(theta);
      return new double[] {stageYUm, stageZUm};
   }

   /*
    * compute the coordinates in um of pixels in the image
    */
   private void computeRemappedCoordinateSpace() {
      imageYCoords_um_ = new double[cameraImageHeight_];
      for (int i = 0; i < cameraImageHeight_; i++) {
         imageYCoords_um_[i] = i * cameraPixelSizeXY_um_;
      }
      imageZCoords_um_ = new double[zStackSlices_];
      for (int i = 0; i < zStackSlices_; i++) {
         imageZCoords_um_[i] = i * zStep_um_;
      }
      // compute the um extent of "stage" coordinates, which is the new XYZ coordinate space
      // that images will be remapped to
      double[] minStageCoords = computeStageCoordsUm(imageZCoords_um_[0], imageYCoords_um_[0]);
      double[] maxStageCoords = computeStageCoordsUm(imageZCoords_um_[zStackSlices_ - 1],
            imageYCoords_um_[cameraImageHeight_ - 1]);
      double totalStageExtentY_um = maxStageCoords[0] - minStageCoords[0];
      double totalStageExtentZ_um = maxStageCoords[1] - minStageCoords[1];

      // Figure out the shape of the remapped image
      // The x pixel size is fixed by the camera/optics.
      // Make z and y pixels sizes equal to this for isotropic voxels in reconstructions/projections
      reconstructionVoxelSize_um_ = cameraPixelSizeXY_um_;
      reconstructedImageHeight_ = (int) (totalStageExtentY_um / reconstructionVoxelSize_um_);
      reconstructedImageWidth_ = cameraImageWidth_;
      reconstructedImageDepth_ = (int) (totalStageExtentZ_um / reconstructionVoxelSize_um_);
   }

   /**
    * Create look up tables that say which YZ pixels in the camera image correspond to which
    * YZ pixels in the reconstructed image. Not needed for X pixels because they map 1 to 1
    */
   private void precomputeCoordTransformLUTs() {
      for (int zIndexCamera = 0; zIndexCamera < zStackSlices_; zIndexCamera++) {
         double imageZUm = imageZCoords_um_[zIndexCamera];
         for (int yIndexCamera = 0; yIndexCamera < cameraImageHeight_; yIndexCamera++) {
            double imageYUm = imageYCoords_um_[yIndexCamera];
            double[] stageCoordsUm = computeStageCoordsUm(imageZUm, imageYUm);

            int stageYIndex = Math.min(
                  (int) Math.round(stageCoordsUm[0] / this.cameraPixelSizeXY_um_),
                  reconstructedImageHeight_ - 1);
            int stageZIndex = Math.min(
                  (int) Math.round(stageCoordsUm[1] / this.cameraPixelSizeXY_um_),
                  reconstructedImageDepth_ - 1);

            destYCoordLUT_[zIndexCamera][yIndexCamera] = stageYIndex;
            destZCoordLUT_[zIndexCamera][yIndexCamera] = stageZIndex;
         }
      }
   }

   /**
    * This is where the meat of the processing occurs. For each line in the camera image, we
    * look up the corresponding line in the reconstructed image and add the pixel values
    * together.
    *
    * @param image
    * @param zStackIndex
    */
   public void processImage(short[] image, int zStackIndex, int[][] destination) {
      // loop through all the lines on the image, each of which corresponds to a
      // different axial distance along the sheet
      for (int yIndexCamera = 0; yIndexCamera < cameraImageHeight_; yIndexCamera++) {
         // look up the Y and Z coordinates in the reconstructed image space
         int stageYIndex = destYCoordLUT_[zStackIndex][yIndexCamera];
         int stageZIndex = destZCoordLUT_[zStackIndex][yIndexCamera];
         // Now copy a full line of pixels from the camera image to the reconstructed image
         synchronized (destination[stageYIndex]) {
            for (int stageXIndex = 0; stageXIndex < cameraImageWidth_; stageXIndex++) {
               short pixelValue = image[yIndexCamera * cameraImageWidth_ + stageXIndex];

//                  destination[stageYIndex][stageXIndex] += (pixelValue & 0xffff);
               destination[stageYIndex][stageXIndex] += (pixelValue & 0xffff);


            }
         }
      }

   }

   public short[] getYXProjection() {
      return meanProjectionYX_;
   }

    public short[] getYZProjection() {
        return meanProjectionYZ_;
    }

    public short[] getXZProjection() {
        return meanProjectionXZ_;
    }

//    public short[][][] getReconstructedVolume() {
//        return reconVolume_;
//    }


   private void processAllImages(ArrayList<short[]> stack) {


      sumProjectionYX_ = new int[reconstructedImageHeight_][reconstructedImageWidth_];

      IntStream.range(0, stack.size())
//            .parallel()
            .forEach(zStackIndex -> {
               this.processImage(stack.get(zStackIndex), zStackIndex, sumProjectionYX_);
            });

      meanProjectionYX_ = new short[reconstructedImageWidth_ * reconstructedImageHeight_];

        //now compute the mean
         for (int y = 0; y < reconstructedImageHeight_; y++) {
            for (int x = 0; x < reconstructedImageWidth_; x++) {
                if (pixelCountSumProjectionYX_[y][x] > 0) {
                    meanProjectionYX_[x + y * reconstructedImageWidth_] =
                            (short) ( sumProjectionYX_[y][x] );
//                                  / (float)pixelCountSumProjectionYX_[y][x]);
                }
            }
        }



   }


   private static int getImageWidth(ImagePlus imagePlus) {
      ImageStack stack = imagePlus.getStack();
      ImageProcessor ip = stack.getProcessor(1);
      return ip.getWidth();
   }

   private static int getImageHeight(ImagePlus imagePlus) {
      ImageStack stack = imagePlus.getStack();
      ImageProcessor ip = stack.getProcessor(1);
      return ip.getHeight();
   }

   private static ArrayList<short[]> loadTiffStack( ImagePlus imagePlus) {
      ArrayList<short[]> images = new ArrayList<>();

      ImageStack stack = imagePlus.getStack();
      int numSlices = stack.getSize();

      for (int i = 1; i <= numSlices; i++) {
         ImageProcessor ip = stack.getProcessor(i);
         short[] pixels = (short[]) ip.getPixels();
         images.add(pixels);
      }

      Collections.reverse(images);
      return images;
   }



   //TODO: swap to ZYX order everywhere?




   public static void main(String[] args) {
      String tiffPath = "C:\\Users\\henry\\Desktop\\demo_snouty.tif";
      double zStepUm = 0.13;
      double pixelSizeXYUm = 0.116;
      double theta = 1.05 * Math.PI / 4;

      ImagePlus imagePlus = new ImagePlus(tiffPath);
      int imageWidth = getImageWidth(imagePlus);
      int imageHeight = getImageHeight(imagePlus);
      ArrayList<short[]> stack = loadTiffStack(imagePlus);

      // downsample the stack taking every other
//      int downsamplingFactorZ = 2;
//      ArrayList<short[]> downsampledStack = new ArrayList<>();
//        for (int i = 0; i < stack.size(); i++) {
//             if (i % downsamplingFactorZ == 0) {
//                downsampledStack.add(stack.get(i));
//             }
//        }
//        stack = downsampledStack;
//        zStepUm *= downsamplingFactorZ;


      // keep only first 256 elements
//        stack = new ArrayList<short[]>(stack.subList(0, 256));

        // create a new stack of images with teh y axis downsized by a factor of
//      int downsamplingFactor = 1;
//        for (int i = 0; i < stack.size(); i++) {
//             short[] image = stack.get(i);
//             short[] newImage = new short[image.length / downsamplingFactor];
//             for (int j = 0; j < newImage.length; j++) {
//                 newImage[j] = image[j * downsamplingFactor];
//             }
//             stack.set(i, newImage);
//         }
//         imageWidth /= downsamplingFactor;


        System.out.println("Stack size: " + stack.size());
        // print image dimensions
        System.out.println("Image width: " + imageWidth + " Image height: " + imageHeight);


      ObliqueStackProcessor processor = new ObliqueStackProcessor(theta, pixelSizeXYUm, zStepUm,
            stack.size(), imageWidth, imageHeight);
      int numLoops = 1;
      for (int i = 0; i < numLoops; i++) {
         long startTime = System.currentTimeMillis();
         processor.processAllImages(stack);
         long endTime = System.currentTimeMillis();
         System.out.println("Processing time: " + (endTime - startTime) + " ms");

      }

        long endTime2 = System.currentTimeMillis();
//        System.out.println("Finalize time: " + (endTime2 - endTime) + " ms");


      short[] projectionXY = processor.getYXProjection();
//      short[] projectionYZ = processor.getYZProjection();
//      short[] projectionXZ = processor.getXZProjection();

      //display in imageJ
      ImageStack imageStack = new ImageStack(
            processor.getReconstructedImageWidth(), processor.getReconstructedImageHeight());
      imageStack.addSlice("projection", projectionXY);
      ImagePlus projectionImage = new ImagePlus("projection", imageStack);
      projectionImage.show();
      // show X
//        ImageStack imageStackXZ = new ImageStack(
//                processor.getReconstructedImageWidth(), processor.getReconstructedImageDepth());
//        imageStackXZ.addSlice("projectionXZ", projectionXZ);
//        ImagePlus projectionImageXZ = new ImagePlus("projectionXZ", imageStackXZ);
//        projectionImageXZ.show();
//        // show Y
//        ImageStack imageStackYZ = new ImageStack(
//                processor.getReconstructedImageHeight(), processor.getReconstructedImageDepth());
//        imageStackYZ.addSlice("projectionYZ", projectionYZ);
//        ImagePlus projectionImageYZ = new ImagePlus("projectionYZ", imageStackYZ);
//        projectionImageYZ.show();

   }



}
