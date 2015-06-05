package com.livescribe.aws.tokensvc.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("pen")
public class PenDTO {
	
	@XStreamAlias("type")
	@XStreamAsAttribute()
	private String type;
	
	@XStreamAlias("name")
	private String name;
	
	@XStreamAlias("displayId")
	private String displayId;

	public PenDTO(String type, String name, String displayId) {
		this.type = type;
		this.name = name;
		this.displayId = displayId;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayId() {
		return displayId;
	}

	public void setDisplayId(String displayId) {
		this.displayId = displayId;
	}
	
}
