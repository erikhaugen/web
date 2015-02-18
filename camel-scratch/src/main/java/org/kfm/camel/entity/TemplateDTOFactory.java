/**
 * Created:  Dec 15, 2014 5:23:51 PM
 */
package org.kfm.camel.entity;

import org.apache.log4j.Logger;

import com.livescribe.afp.PageTemplate;
import com.livescribe.afp.Region;
import com.livescribe.afp.Shape;
import com.livescribe.util.Units;

/**
 * <p></p>
 * 
 * @author <a href="mailto:kmurdoff@livescribe.com">Kevin F. Murdoff</a>
 * @version 1.0
 */
public class TemplateDTOFactory {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public static TemplateDTO create(Region region, PageTemplate pageTemplate) {
		
		Shape regionZeroShape = region.getShape();
		TemplateDTO templateDto = new TemplateDTO();
		templateDto.setX(Units.auToMM(regionZeroShape.getVertex(0)[0]));
		templateDto.setY(Units.auToMM(regionZeroShape.getVertex(0)[1]));
		templateDto.setWidth(Units.auToMM(regionZeroShape.getVertex(1)[0] - regionZeroShape.getVertex(0)[0]));
		templateDto.setHeight(Units.auToMM(regionZeroShape.getVertex(1)[1] - regionZeroShape.getVertex(0)[1]));
		
		/*
		 * If any of the x, y, width or height variables are Double.MIN_VALUE, 
		 * we didn't find a crop/area region 0x0.  We need to
		 * use 0, 0, main.document.width, main.document.height values instead.
		 */
		if ((templateDto.getX() == Double.MIN_VALUE) ||
				(templateDto.getY() == Double.MIN_VALUE) ||
				(templateDto.getWidth() == Double.MIN_VALUE) ||
				(templateDto.getHeight() == Double.MIN_VALUE)) {
			templateDto.setX(0);
			templateDto.setY(0);
			templateDto.setWidth(Units.auToMM(pageTemplate.getWidth()));
			templateDto.setHeight(Units.auToMM(pageTemplate.getHeight()));
		}
		
		return templateDto;
	}
	
	public static TemplateDTO createDefaultTemplate() {
		
		TemplateDTO templateDto = new TemplateDTO();
		templateDto.setIndex(-1);
		templateDto.setX(Double.MIN_VALUE);
		templateDto.setY(Double.MIN_VALUE);
		templateDto.setWidth(Double.MIN_VALUE);
		templateDto.setHeight(Double.MIN_VALUE);
		
		return templateDto;
	}
}
