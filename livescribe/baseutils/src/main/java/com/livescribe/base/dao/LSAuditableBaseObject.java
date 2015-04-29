package com.livescribe.base.dao;

public class LSAuditableBaseObject extends LSBaseDaoObject {

	private long updatedByUserId;
	private long createdByUserId;

	public long getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(long createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public long getUpdatedByUserId() {
		return updatedByUserId;
	}

	public void setUpdatedByUserId(long updatedByUserId) {
		this.updatedByUserId = updatedByUserId;
	}
}
