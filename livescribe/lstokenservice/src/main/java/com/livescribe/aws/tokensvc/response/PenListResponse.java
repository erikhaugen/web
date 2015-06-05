package com.livescribe.aws.tokensvc.response;

import java.util.List;

import com.livescribe.aws.tokensvc.dto.PenDTO;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("response")
public class PenListResponse extends ServiceResponse {

	@XStreamAlias("pens")
	private List<PenDTO> pens;
	
	public PenListResponse() {
		super();
	}
	
	public PenListResponse(ResponseCode code) {
		super(code);
	}

	public List<PenDTO> getPens() {
		return pens;
	}

	public void setPens(List<PenDTO> pens) {
		this.pens = pens;
	}
	
}
