 package com.sun.media.codec.video.colorspace;

 import com.sun.media.BasicCodec;
import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.control.FrameProcessingControl;
import javax.media.format.RGBFormat;
import javax.media.format.YUVFormat;
import java.awt.*;

 public class YUVToRGB extends BasicCodec
 {
   private static boolean loaded = false;

   private int peer = 0;
   protected Format[] defInputFormats;
   protected Format[] defOutputFormats;
   private YUVFormat inputFormat = null;
   private RGBFormat outputFormat = null;

   private FrameProcessingControl frameControl = null;
   private boolean dropFrame;

   public YUVToRGB()
   {
     this.defInputFormats = new Format[1];
     this.defInputFormats[0] = new YUVFormat();
     this.defOutputFormats = new Format[1];
     this.defOutputFormats[0] = new RGBFormat();

     if (this.frameControl == null)
     {
       this.frameControl = new FPC();
       this.controls = new Control[1];
       this.controls[0] = this.frameControl;
     }
   }

   protected Format getInputFormat() {
     return this.inputFormat;
   }

   protected Format getOutputFormat() {
     return this.outputFormat;
   }

   public Format[] getSupportedInputFormats()
   {
     return this.defInputFormats;
   }

   public Format[] getSupportedOutputFormats(Format in)
   {
     if (in == null)
       return this.defOutputFormats;
     if ((in instanceof YUVFormat)) {
       YUVFormat yuvIn = (YUVFormat)in;
       Dimension size = yuvIn.getSize();
       int lineStride = 0;
       if (size != null) {
         lineStride = size.width;
         if ((lineStride & 0x1) != 0) {
           lineStride++;
         }
       }
       RGBFormat[] rgbOut = null;

         rgbOut = new RGBFormat[6];

         rgbOut[0] = new RGBFormat(size, lineStride * size.height, Format.shortArray, yuvIn.getFrameRate(), 16, 31744, 992, 31, 1, lineStride, 0, -1);

         rgbOut[1] = new RGBFormat(size, lineStride * size.height, Format.shortArray, yuvIn.getFrameRate(), 16, 63488, 2016, 31, 1, lineStride, 0, -1);

         rgbOut[2] = new RGBFormat(size, lineStride * size.height * 3 + 1, Format.byteArray, yuvIn.getFrameRate(), 24, 1, 2, 3, 3, lineStride * 3, 0, -1);

         rgbOut[3] = new RGBFormat(size, lineStride * size.height * 3 + 1, Format.byteArray, yuvIn.getFrameRate(), 24, 3, 2, 1, 3, lineStride * 3, 0, -1);

         rgbOut[4] = new RGBFormat(size, lineStride * size.height, Format.intArray, yuvIn.getFrameRate(), 32, 16711680, 65280, 255, 1, lineStride, 0, -1);

         rgbOut[5] = new RGBFormat(size, lineStride * size.height, Format.intArray, yuvIn.getFrameRate(), 32, 255, 65280, 16711680, 1, lineStride, 0, -1);
       /* // alignment != 1
         rgbOut = new RGBFormat[2];

         rgbOut[0] = new RGBFormat(size, lineStride * size.height, Format.intArray, yuvIn.getFrameRate(), 32, 16711680, 65280, 255, 1, lineStride, 0, -1);

         rgbOut[1] = new RGBFormat(size, lineStride * size.height, Format.intArray, yuvIn.getFrameRate(), 32, 255, 65280, 16711680, 1, lineStride, 0, -1);
       */

       return rgbOut;
     }
     return new Format[0];
   }

   public Format setInputFormat(Format in) {
     boolean formatChanged = false;

     if (!(in instanceof YUVFormat)) {
       return null;
     }
     YUVFormat yuv = (YUVFormat)in;
     Dimension size = yuv.getSize();
     int yStride = yuv.getStrideY();
     int uvStride = yuv.getStrideUV();
     int type = yuv.getYuvType();
     Class bufType = yuv.getDataType();
     int offsetY = yuv.getOffsetY();
     int offsetU = yuv.getOffsetU();
     int offsetV = yuv.getOffsetV();

     if ((size == null) || (size.width < 1) || (size.height < 1) || ((type & 0x1) != 0) || (bufType != Format.byteArray))
     {
       return null;
     }
     if (((type & 0x20) != 0) && ((offsetY == offsetU) || (offsetY == offsetV) || (offsetV == offsetU)))
     {
       return null;
     }

     if (this.peer != 0) {
       formatChanged = true;
       close();
     }

     this.inputFormat = yuv;

     if ((this.outputFormat != null) && (size != null) && ((!size.equals(this.outputFormat.getSize())) || (formatChanged)))
     {
       int lineStride = size.width;
       if ((lineStride & 0x1) != 0) {
         lineStride++;
       }
       lineStride *= this.outputFormat.getPixelStride();
       this.outputFormat = new RGBFormat(size, lineStride * size.height + 4, this.outputFormat.getDataType(), this.outputFormat.getFrameRate(), this.outputFormat.getBitsPerPixel(), this.outputFormat.getRedMask(), this.outputFormat.getGreenMask(), this.outputFormat.getBlueMask(), this.outputFormat.getPixelStride(), lineStride, this.outputFormat.getFlipped(), this.outputFormat.getEndian());
     }

     if (formatChanged) {
       try {
         open();
       } catch (ResourceUnavailableException rue) {
         return null;
       }
     }
     return in;
   }

   public Format setOutputFormat(Format out) {
     if (!(out instanceof RGBFormat)) {
       return null;
     }
     RGBFormat rgb = (RGBFormat)out;
     Dimension outSize = rgb.getSize();
     int pixelStride = rgb.getPixelStride();
     int lineStride = rgb.getLineStride();

     if ((outSize == null) || (pixelStride < 1) || (lineStride < outSize.width))
     {
       return null;
     }

     this.outputFormat = rgb;
     return out;
   }

   public int process(Buffer inBuffer, Buffer outBuffer) {
     if (isEOM(inBuffer)) {
       propagateEOM(outBuffer);
       return 0;
     }

     if ((inBuffer.isDiscard()) || (this.dropFrame == true)) {
       outBuffer.setDiscard(true);
       return 0;
     }

     long inDataBytes = 0L;
     long outDataBytes = 0L;
     Object inData = null;
     Object outData = null;
     Format inFormat = inBuffer.getFormat();
     if ((inFormat != this.inputFormat) && (!inFormat.equals(this.inputFormat))) {
       setInputFormat(inFormat);
     }

     inData = getInputData(inBuffer);
     inDataBytes = getNativeData(inData);

     outData = getOutputData(outBuffer);

     if ((outData == null) || (outBuffer.getFormat() != this.outputFormat) || (!outBuffer.getFormat().equals(this.outputFormat)))
     {
       RGBFormat rgb = this.outputFormat;
       int stride = rgb.getLineStride();
       int dataSize = rgb.getSize().height * stride + 1;
       Class dataType = rgb.getDataType();

       outBuffer.setLength(dataSize);
       outBuffer.setFormat(this.outputFormat);
     }

     outData = validateData(outBuffer, 0, true);
     outDataBytes = getNativeData(outData);

     if (inBuffer.getLength() < 10) {
       outBuffer.setDiscard(true);
       return 0;
     }

     int inWidth = this.inputFormat.getStrideY();
     int strideUV = this.inputFormat.getStrideUV();
     int inHeight = this.inputFormat.getSize().height;
     int offsetY = this.inputFormat.getOffsetY();
     int offsetU = this.inputFormat.getOffsetU();
     int offsetV = this.inputFormat.getOffsetV();
     int outWidth = this.outputFormat.getLineStride();
     int outHeight = this.outputFormat.getSize().height;
     int decimation = this.inputFormat.getYuvType();
     int clipWidth = this.inputFormat.getSize().width;
     int clipHeight = this.inputFormat.getSize().height;

     switch (decimation & 0xFFFFFFBF) {
     case 2:
       decimation = 1; break;
     case 4:
       decimation = 2; break;
     case 8:
       decimation = 4; break;
     case 512:
       decimation = 5; break;
     case 16:
       decimation = 3; break;
     case 32:
       decimation = 6;
     }

     if (this.outputFormat.getBitsPerPixel() == 24) {
       outWidth /= 3;
     }
     boolean result = convert(this.peer, inData, inDataBytes, outData, outDataBytes, inWidth, inHeight, outWidth, outHeight, clipWidth, clipHeight, offsetY, offsetU, offsetV, inWidth, strideUV, decimation, this.outputFormat.getBitsPerPixel() / 8);

     if (result) {
       outBuffer.setTimeStamp(inBuffer.getTimeStamp());
       outBuffer.setLength(this.outputFormat.getLineStride() * this.outputFormat.getSize().height);

       return 0;
     }
     return 1;
   }

   public synchronized void open() throws ResourceUnavailableException {
     if (!loaded) {
       try {
         //JMFSecurityManager.loadLibrary("jmutil");
         loaded = true;
       } catch (UnsatisfiedLinkError e) {
         throw new ResourceUnavailableException(e.getMessage());
       }
     }
     if ((this.inputFormat == null) || (this.outputFormat == null)) {
       throw new ResourceUnavailableException("Incorrect formats set on YUVToRGB converter");
     }
     if (this.peer != 0)
       close();
     try
     {
       int redMask = this.outputFormat.getRedMask();
       int greenMask = this.outputFormat.getGreenMask();
       int blueMask = this.outputFormat.getBlueMask();
       int bitsPerPixel = this.outputFormat.getBitsPerPixel();
       if ((bitsPerPixel == 24) && (this.outputFormat.getDataType() == Format.byteArray)) {
         redMask = 255 << (redMask - 1) * 8;
         greenMask = 255 << (greenMask - 1) * 8;
         blueMask = 255 << (blueMask - 1) * 8;
       }

       this.peer = initConverter(redMask, greenMask, blueMask, bitsPerPixel, (this.inputFormat.getYuvType() & 0x40) != 0);
     }
     catch (Throwable t)
     {
     }

     if (this.peer == 0)
       throw new ResourceUnavailableException("Unable to initialize YUVToRGB converter");
   }

   public synchronized void close() {
     if (this.peer != 0)
       freeConverter(this.peer);
     this.peer = 0;
   }

   public synchronized void reset()
   {
   }

   public void finalize()
   {
     close();
   }

   public String getName() {
     return "YUV To RGB Converter";
   }

   private native int initConverter(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

   private native boolean convert(int paramInt1, Object paramObject1, long paramLong1, Object paramObject2, long paramLong2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, int paramInt13, int paramInt14);

   private native boolean freeConverter(int paramInt);

   class FPC
     implements FrameProcessingControl
   {
     FPC()
     {
     }

     public void setFramesBehind(float frames)
     {
       if (frames > 0.0F)
         YUVToRGB.this.dropFrame = true;
       else
         YUVToRGB.this.dropFrame = false;
     }

     public boolean setMinimalProcessing(boolean minimal)
     {
       YUVToRGB.this.dropFrame = minimal;
       return YUVToRGB.this.dropFrame;
     }

     public Component getControlComponent() {
       return null;
     }

     public int getFramesDropped() {
       return 0;
     }
   }
 }

