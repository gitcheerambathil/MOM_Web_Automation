package test.philips.com.listeners;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import test.philips.com.utils.Init;
import test.philips.com.utils.ReusableMethods;
import test.philips.com.utils.SetupDriver;

import java.text.SimpleDateFormat;
import java.util.Date;
public class VideoRecord{
    private ScreenRecorder screenRecorder;
       public void startRecording(String strTestcaseName) throws Exception
       {    
              File file = new File(SetupDriver.videoPath);         
              Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
              int width = screenSize.width;
              int height = screenSize.height;
                             
              Rectangle captureSize = new Rectangle(0,0, width, height);
                             
              GraphicsConfiguration gc = GraphicsEnvironment
               .getLocalGraphicsEnvironment()
               .getDefaultScreenDevice()
               .getDefaultConfiguration();
 
           this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
               new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
               new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                    DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                    QualityKey, 1.0f,
                    KeyFrameIntervalKey, 15 * 60),
               new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                    FrameRateKey, Rational.valueOf(30)),
               null, file, strTestcaseName);
          this.screenRecorder.start();
          ReusableMethods.videoFilePath=file+"\\"+ReusableMethods.videoFileName;
       }
 
       public void stopRecording() throws Exception
       {
         this.screenRecorder.stop();
       }
}