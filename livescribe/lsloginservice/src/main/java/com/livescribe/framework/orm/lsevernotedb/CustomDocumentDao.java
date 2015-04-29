package com.livescribe.framework.orm.lsevernotedb;

import java.util.List;

import org.hibernate.Query;

public class CustomDocumentDao extends DocumentDao {

	public List<Document> findDocumentsByPenDisplayId(String penDipslayId) {
		Query q = sessionFactoryEvernote.getCurrentSession().createQuery("from Document d where d.penSerial = :penDisplayId");
		q.setString("penDisplayId", penDipslayId.toUpperCase());
		
		List<Document> list = q.list();

		return list;
	}
}
