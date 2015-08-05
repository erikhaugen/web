package com.livescribe.web.registration.response;

import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.dto.WarrantyDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class WarrantyResponse extends ServiceResponse {
	
	@XStreamAlias("warranty")
	private WarrantyDTO warrantyDto;
	
	/**
	 * <p></p>
	 * 
	 * @param code
	 * @param warranty
	 */
	public WarrantyResponse(ResponseCode code, Warranty warranty) {
		super(code);
		warrantyDto = new WarrantyDTO(warranty);
	}

	public WarrantyDTO getWarrantyDto() {
		return warrantyDto;
	}

	/**
	 * <p></p>
	 * 
	 * @param warrantyDto the warrantyDto to set
	 */
	public void setWarrantyDto(WarrantyDTO warrantyDto) {
		this.warrantyDto = warrantyDto;
	}
}
