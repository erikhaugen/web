/**
 * Created:  Dec 15, 2014 6:06:42 PM
 */
package org.kfm.camel.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.livescribe.afp.Afd;
import com.livescribe.afp.GraphicsElement;
import com.livescribe.util.Units;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class ImageDTOFactory {

	private static Logger logger = Logger.getLogger(ImageDTOFactory.class.getName());

	public static ImageDTO create(Afd afd, GraphicsElement graphicsElement) {
		
		ImageDTO imageDto = createDefault();
		if (graphicsElement instanceof GraphicsElement.Image) {
			imageDto.setX(Units.auToMM((int)graphicsElement.getX()));
			imageDto.setY(Units.auToMM((int)graphicsElement.getY()));
			imageDto.setZ(0); //for future layering of images (if implemented)
			imageDto.setWidth(Units.auToMM((int)graphicsElement.getWidth()));
			imageDto.setHeight(Units.auToMM((int)graphicsElement.getHeight()));
			String fileName = ((GraphicsElement.Image)graphicsElement).getFileName();
			byte[] data = ((GraphicsElement.Image)graphicsElement).getData();
			if (fileName != null) {
//				logger.debug("create() - GraphicsElement.Image file name: " + fileName);
				
				if (fileName.endsWith(".eps")) {
					fileName = fileName.replace(".eps", ".png");
				}
				ImageFileDTO imageFileDto = new ImageFileDTO();
				imageFileDto.setData(data);
			}
		} else {
			logger.warn("create() - GraphicsElement is instanceof " + graphicsElement.getClass().getCanonicalName());
		}
		return imageDto;
	}
	
	public static ImageDTO createDefault() {
		
		ImageDTO imageDto = new ImageDTO();
		imageDto.setX(Double.MIN_VALUE);
		imageDto.setY(Double.MIN_VALUE);
		imageDto.setZ(Double.MIN_VALUE); //for future layering of images (if implemented)
		imageDto.setWidth(Double.MIN_VALUE);
		imageDto.setHeight(Double.MIN_VALUE);
		
		return imageDto;
	}
}
