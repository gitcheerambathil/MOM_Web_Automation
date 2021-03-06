package test.philips.com.listeners;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

import test.philips.com.utils.ReusableMethods;
 
public class SpecializedScreenRecorder extends ScreenRecorder {
 
    private String name;
 
    public SpecializedScreenRecorder(GraphicsConfiguration cfg,
           Rectangle captureArea, Format fileFormat, Format screenFormat,
           Format mouseFormat, Format audioFormat, File movieFolder,
           String name) throws IOException, AWTException {
         super(cfg, captureArea, fileFormat, screenFormat, mouseFormat,
                  audioFormat, movieFolder);
         this.name = name;
    }
 
    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
          if (!movieFolder.exists()) {
                movieFolder.mkdirs();
          } else if (!movieFolder.isDirectory()) {
                throw new IOException("\"" + movieFolder + "\" is not a directory.");
          }
                           
          SimpleDateFormat dateFormat = new SimpleDateFormat(
                   "yyyy-MM-dd HH.mm.ss");
          
          ReusableMethods.videoFileName = name + "-" + dateFormat.format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat);
          return new File(movieFolder, ReusableMethods.videoFileName);
    }
 }
