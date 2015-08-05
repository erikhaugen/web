package com.livescribe.web.registration.response;

import java.util.ArrayList;
import java.util.List;

import com.livescribe.framework.orm.vectordb.Warranty;
import com.livescribe.framework.web.response.ResponseCode;
import com.livescribe.framework.web.response.ServiceResponse;
import com.livescribe.web.registration.dto.WarrantyDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class WarrantyListResponse extends ServiceResponse {

	@XStreamAlias("warranties")
	private List<WarrantyDTO> warranties;
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public WarrantyListResponse() {
		super();
		warranties = new ArrayList<WarrantyDTO>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 */
	public WarrantyListResponse(ResponseCode responseCode) {
		super(responseCode);
		warranties = new ArrayList<WarrantyDTO>();
	}
	
	/**
	 * Constructor
	 * 
	 * @param responseCode
	 * @param warrantyList
	 */
	public WarrantyListResponse(ResponseCode responseCode, List<Warranty> warrantyList) {
		super(responseCode);
		this.warranties = new ArrayList<WarrantyDTO>();
		
		if (warrantyList != null) {
			for (Warranty war : warrantyList) {
				WarrantyDTO warDTO = new WarrantyDTO(war);
				this.warranties.add(warDTO);
			}
		}
	}

	public List<WarrantyDTO> getWarranties() {
		return warranties;
	}

	public void setWarranties(List<WarrantyDTO> warranties) {
		this.warranties = warranties;
	}

}
