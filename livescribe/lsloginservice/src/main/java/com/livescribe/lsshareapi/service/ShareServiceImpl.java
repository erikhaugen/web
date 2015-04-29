package com.livescribe.lsshareapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.livescribe.framework.orm.lsevernotedb.CustomDocumentDao;
import com.livescribe.framework.orm.lsevernotedb.Document;


public class ShareServiceImpl implements ShareService {
	
	@Autowired
	private CustomDocumentDao documentDao;
	
	@Transactional("lsevernotedb")
	public List<Document> findDocumentsByPenDisplayId(String displayId) {
		
		return documentDao.findDocumentsByPenDisplayId(displayId);
	}
}
