/**
 * Created:  Dec 22, 2014 2:50:12 PM
 */
package org.kfm.camel.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.kfm.camel.converter.StrokeConverter;
import org.kfm.camel.entity.evernote.XMLCoordinate;
import org.kfm.camel.entity.evernote.XMLStroke;
import org.kfm.camel.entity.evernote.XMLStrokes;
import org.kfm.camel.util.Utils;
import org.kfm.jpa.AfdImage;
import org.kfm.jpa.Page;
import org.kfm.jpa.Session;
import org.kfm.jpa.Template;
import org.kfm.jpa.TimeMap;

import com.evernote.edam.type.Data;
import com.evernote.edam.type.Resource;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ImageFactory {

	private static Logger logger = Logger.getLogger(ImageFactory.class.getName());

	private static final float	IMAGE_RESOLUTION_PPMM = 5.9f;
	
	private static BufferedImage addStrokesToImage(BufferedImage bufferedImage, XMLStrokes xmlStrokes, Page page, float scale) {
		
		logger.debug("addStrokesToImage() called");
		
		List<TimeMap> timeMaps = page.getTimeMaps();
		
		// Get our max for image
		float tMaxX = 0;
		float tMaxY = 0;
		for (int i = 0; i < xmlStrokes.list.size(); i++) {
			
			XMLStroke tStroke = xmlStrokes.list.get(i);

			for (int j = 0; j < tStroke.coords.size(); j++) {
				XMLCoordinate tCoord = tStroke.coords.get(j);
				if (tCoord.x > tMaxX)
					tMaxX = tCoord.x;
				if (tCoord.y > tMaxY)
					tMaxY = tCoord.y; 
			}
		}

		// Convert from mm to our pixels
		tMaxX = (float)Math.ceil(tMaxX * IMAGE_RESOLUTION_PPMM * scale);
		tMaxY = (float)Math.ceil(tMaxY * IMAGE_RESOLUTION_PPMM * scale);

		BufferedImage strokeImage = new BufferedImage((int)tMaxX, (int)tMaxY, BufferedImage.TYPE_INT_ARGB);

		Graphics2D imageGraphics = strokeImage.createGraphics();
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		imageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		//		tImageGraphics.setStroke(new BasicStroke(aScale));
		imageGraphics.setStroke(new BasicStroke(1.5f));
		//		tImageGraphics.setColor(Color.black);

		for (int i = 0; i < xmlStrokes.list.size(); i++) {
			XMLStroke tStroke = xmlStrokes.list.get(i);
			for (int j = 1; j < tStroke.coords.size(); j++) {
				
				//	We don't want a case where logic keeps us from "drawing" without any color
				imageGraphics.setColor(Color.black);

				XMLCoordinate prevCoord = tStroke.coords.get(j - 1);
				XMLCoordinate currentCoord = tStroke.coords.get(j);
				if (timeMaps.size() > 0) {
					//Very Processor INTENSIVE
					//Test to see if strokes timestamp is within TimeMap's start and end
					for (TimeMap timeMap : timeMaps) {
						Session session = timeMap.getSession();
						String sessionId = session.getSessionId();
						int sessId = Integer.parseInt(sessionId);
						if (sessId == Integer.MIN_VALUE) {
							continue;
						}
						BigInteger startTimeBI = timeMap.getStartTime();
						long startTime = startTimeBI.longValue();
						BigInteger endTimeBI = timeMap.getEndTime();
						long endTime = endTimeBI.longValue();
						if ((startTime <= tStroke.timestamp.longValue()) &&
								(endTime >= tStroke.timestamp.longValue())){
							//It is we should "draw" as active stroke (green)
							imageGraphics.setColor(new Color(51, 153, 0));
							break;
						}
					}
				}
				imageGraphics.drawLine((int)Math.ceil(prevCoord.x * IMAGE_RESOLUTION_PPMM * scale),
						(int)Math.ceil(prevCoord.y * IMAGE_RESOLUTION_PPMM * scale),
						(int)Math.ceil(currentCoord.x * IMAGE_RESOLUTION_PPMM * scale),
						(int)Math.ceil(currentCoord.y * IMAGE_RESOLUTION_PPMM * scale));
			}
		}
		imageGraphics.dispose();

		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();

		if (strokeImage.getWidth() > imageWidth || strokeImage.getHeight() > imageHeight) {
			if (strokeImage.getWidth() > imageWidth)
				imageWidth = strokeImage.getWidth();
			if (strokeImage.getHeight() > imageHeight)
				imageHeight = strokeImage.getHeight();

			BufferedImage resizedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

			Graphics2D resizedGraphics = resizedImage.createGraphics();
			resizedGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			resizedGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			resizedGraphics.drawImage(bufferedImage, 0, 0, null);
			resizedGraphics.drawImage(strokeImage, 0, 0, null);
			resizedGraphics.dispose();

			bufferedImage = resizedImage;
		}
		else {
			Graphics2D imgGraphics = bufferedImage.createGraphics();
			imgGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			imgGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			imgGraphics.drawImage(strokeImage, 0, 0, null);
			imgGraphics.dispose();
		}

		return bufferedImage;
	}

	public static BufferedImage createImageForPage(Page page, Resource strokeResource, float scale) throws IOException {
		
		String method = "";
		
		Template template = page.getTemplate();
		AfdImage afdImage = page.getAfdImage();
		
		//--------------------------------------------------
		//	Create Image for Template
		//--------------------------------------------------
		
		//	Create an uncropped template image first.
		BufferedImage bufferedImage = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
		int x = 0;
		int y = 0;
		int width = (int)Math.round(afdImage.getWidth() * IMAGE_RESOLUTION_PPMM * scale);
		int height = (int)Math.round(afdImage.getHeight() * IMAGE_RESOLUTION_PPMM * scale);

		int totalWidth = x + width;
		int totalHeight = y + height;

		byte[] afdImageBytes = afdImage.getData();

		logger.debug(method + "x=" + x + ", y=" + y + ", w=" + width + ", h=" + height + ", size=" + afdImageBytes.length);
		
		//	Read the bytes of the original AFD image into another 'original' BufferedImage.
		BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(afdImageBytes));
		
		//	If either the width and/or height of the uncropped image are larger 
		//	than that of the 'original' image ... 
		if (totalWidth > bufferedImage.getWidth() || totalHeight > bufferedImage.getHeight()) {
			
			//	If the width and/or height of the original image is less than
			//	the new buffered image, increase the total width/height of the 
			//	original image to match.
			if (totalWidth < bufferedImage.getWidth())
				totalWidth = bufferedImage.getWidth();
			if (totalHeight < bufferedImage.getHeight())
				totalHeight = bufferedImage.getHeight();

			logger.debug(method + "Resized Image (before) x=" + x + ", y=" + y + ", w=" + width + ", h=" + height);
			
			BufferedImage resizedImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_4BYTE_ABGR);

			Graphics2D resizedGraphics = resizedImage.createGraphics();
			resizedGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			resizedGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			resizedGraphics.drawImage(bufferedImage, 0, 0, null);
			resizedGraphics.drawImage(originalImage, x, y, width, height, null);
			resizedGraphics.dispose();

			logger.debug(method + "Resized Image (after) x=" + x + ", y=" + y + ", w=" + width + ", h=" + height);
			
			bufferedImage = resizedImage;
		} else {
			logger.debug(method + "NO resizing Image (before) x=" + x + ", y=" + y + ", w=" + width + ", h=" + height);
			
			Graphics2D buffImageGraphics = bufferedImage.createGraphics();
			buffImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			buffImageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			buffImageGraphics.drawImage(originalImage, x, y, width, height, null);
			buffImageGraphics.dispose();
			
			logger.debug(method + "NO resizing Image (after) x=" + x + ", y=" + y + ", w=" + width + ", h=" + height);
		}
		
		//--------------------------------------------------
		//	Create Image for Template
		//--------------------------------------------------

		XMLStrokes xmlStrokes = StrokeConverter.fromResource(strokeResource);
		
		BufferedImage imageWithStrokes = addStrokesToImage(bufferedImage, xmlStrokes, page, scale);
		
		BufferedImage croppedImage = cropImageForTemplate(template, imageWithStrokes, scale);
		
		return croppedImage;
	}
	
	public static Resource createImageResource(Page page, Resource strokeResource, Resource imageResource, float scale) throws NoSuchAlgorithmException, IOException {
		
		//	Make sure Float.MIN_VALUE is not used.
		float newScale = ((scale != Float.MIN_VALUE) ? scale : 1);
		
		BufferedImage croppedImage = createImageForPage(page, strokeResource, newScale);
		
		//	Write the combined BufferedImage to a ByteArrayOutputStream for Evernote Resource creation
		ByteArrayOutputStream strokeImageStream = new ByteArrayOutputStream();
		ImageIO.write(croppedImage, "png", strokeImageStream);

		byte[] strokeImageBytes = strokeImageStream.toByteArray();
		byte[] strokeImageHash = Utils.toMD5Hash(strokeImageBytes);
		
		MessageDigest tMD5Digest = MessageDigest.getInstance("MD5");
		byte[] tStrokeImageHash = tMD5Digest.digest(strokeImageBytes);

		logger.debug("Creating Evernote Data for Background (Image).");
		
		Data strokeImageData = new Data();
		strokeImageData.setBody(strokeImageBytes);
		strokeImageData.setBodyHash(strokeImageHash);
		strokeImageData.setSize(strokeImageBytes.length);

		logger.debug("Setting Data of Evernote Resource for Background (Image).");
		
		imageResource.setData(strokeImageData);
		
		return imageResource;
	}
	
	/**
	 * <p></p>
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * 
	 */
	private static Data createStrokeImageData(Page page) throws IOException, NoSuchAlgorithmException {

		Template template = page.getTemplate();
		List<TimeMap> timeMaps = page.getTimeMaps();
		AfdImage afdImage = page.getAfdImage();
		
		BufferedImage bufferedImage = null;
		
		ByteArrayOutputStream strokeImageStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "png", strokeImageStream);
		
		byte[] strokeImageBytes = strokeImageStream.toByteArray();
		byte[] strokeImageHash = Utils.toMD5Hash(strokeImageBytes);
		
		Data strokeImageData = new Data();
		strokeImageData.setBody(strokeImageBytes);
		strokeImageData.setBodyHash(strokeImageHash);
		strokeImageData.setSize(strokeImageBytes.length);
		
		return strokeImageData;
	}

	private static BufferedImage cropImageForTemplate(Template template, BufferedImage bufferedImage, float scale) {
		
		logger.debug("cropImageForTemplate() called template=[" + template.toString() + "] scale=" + scale);			
		int cropX = (int)Math.round(template.getX() * IMAGE_RESOLUTION_PPMM * scale);
		int cropY = (int)Math.round(template.getY() * IMAGE_RESOLUTION_PPMM * scale);
		int cropWidth = (int)Math.round(template.getWidth() * IMAGE_RESOLUTION_PPMM * scale);
		int cropHeight = (int)Math.round(template.getHeight() * IMAGE_RESOLUTION_PPMM * scale);

		int strokeX = cropX;
		int strokeY = cropY;
		int strokeWidth = cropWidth;
		int strokeHeight = cropHeight;

		if (strokeX > bufferedImage.getWidth())
			strokeX = bufferedImage.getWidth() - 1;
		if (strokeY > bufferedImage.getHeight())
			strokeY = bufferedImage.getHeight() - 1;
		if ((strokeX + strokeWidth) > bufferedImage.getWidth())
			strokeWidth = bufferedImage.getWidth() - strokeX;
		if ((strokeY + strokeHeight) > bufferedImage.getHeight())
			strokeHeight = bufferedImage.getHeight() - strokeY;

		return bufferedImage.getSubimage(strokeX, strokeY, strokeWidth, strokeHeight);
	}

}
