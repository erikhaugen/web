package com.livescribe.lsshareapi.service;

import java.util.List;

import com.livescribe.framework.orm.lsevernotedb.Document;


public interface ShareService {

	public List<Document> findDocumentsByPenDisplayId(String displayId);
}
