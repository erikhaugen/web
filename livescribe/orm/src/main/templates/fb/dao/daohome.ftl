<#if !pojo.isComponent()>
${pojo.getPackageDeclaration()}
// Generated ${date} by Hibernate Tools ${version}

<#assign classbody>
<#assign declarationName = pojo.importType(pojo.getDeclarationName())>/**
 * Home object for domain model class ${declarationName}.
 * @see ${pojo.getQualifiedDeclarationName()}
 * @author Hibernate Tools
 */
<#if ejb3>
@${pojo.importType("javax.ejb.Stateless")}
</#if>
public class ${declarationName}Dao extends AbstractDao {

<#if ejb3>
    @${pojo.importType("javax.persistence.PersistenceContext")} private ${pojo.importType("javax.persistence.EntityManager")} entityManager;
    
    public void persist(${declarationName} transientInstance) {
        logger.debug("persisting ${declarationName} instance");
        try {
            entityManager.persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void remove(${declarationName} persistentInstance) {
        logger.debug("removing ${declarationName} instance");
        try {
            entityManager.remove(persistentInstance);
            logger.debug("remove successful");
        }
        catch (RuntimeException re) {
            logger.error("remove failed", re);
            throw re;
        }
    }
    
    public ${declarationName} merge(${declarationName} detachedInstance) {
        logger.debug("merging ${declarationName} instance");
        try {
            ${declarationName} result = entityManager.merge(detachedInstance);
            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
            logger.error("merge failed", re);
            throw re;
        }
    }
    
<#if clazz.identifierProperty?has_content>    
    public ${declarationName} findById( ${pojo.getJavaTypeName(clazz.identifierProperty, jdk5)} id) {
        logger.debug("getting ${declarationName} instance with id: " + id);
        try {
            ${declarationName} instance = entityManager.find(${pojo.getDeclarationName()}.class, id);
            logger.debug("get successful");
            return instance;
        }
        catch (RuntimeException re) {
            logger.error("get failed", re);
            throw re;
        }
    }
</#if>
<#else>    
    
    public void persist(${declarationName} transientInstance) {
        logger.debug("persisting ${declarationName} instance");
        try {
            sessionFactoryFB.getCurrentSession().persist(transientInstance);
            logger.debug("persist successful");
        }
        catch (RuntimeException re) {
            logger.error("persist failed", re);
            throw re;
        }
    }
    
    public void attachDirty(${declarationName} instance) {
        logger.debug("attaching dirty ${declarationName} instance");
        try {
            sessionFactoryFB.getCurrentSession().saveOrUpdate(instance);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(${declarationName} instance) {
        logger.debug("attaching clean ${declarationName} instance");
        try {
            sessionFactoryFB.getCurrentSession().lock(instance, ${pojo.importType("org.hibernate.LockMode")}.NONE);
            logger.debug("attach successful");
        }
        catch (RuntimeException re) {
            logger.error("attach failed", re);
            throw re;
        }
    }
    
    public void delete(${declarationName} persistentInstance) {
        logger.debug("deleting ${declarationName} instance");
        try {
            sessionFactoryFB.getCurrentSession().delete(persistentInstance);
            logger.debug("delete successful");
        }
        catch (RuntimeException re) {
            logger.error("delete failed", re);
            throw re;
        }
    }
    
    public ${declarationName} merge(${declarationName} detachedInstance) {
//        logger.debug("merging ${declarationName} instance");
        try {
            ${declarationName} result = (${declarationName}) sessionFactoryFB.getCurrentSession()
                    .merge(detachedInstance);
//            logger.debug("merge successful");
            return result;
        }
        catch (RuntimeException re) {
//            logger.error("merge failed", re);
            throw re;
        }
    }
    
<#if clazz.identifierProperty?has_content>
    public ${declarationName} findById( ${c2j.getJavaTypeName(clazz.identifierProperty, jdk5)} id) {
        logger.debug("getting ${declarationName} instance with id: " + id);
        try {
            ${declarationName} instance = (${declarationName}) sessionFactoryFB.getCurrentSession()
                    .get("${clazz.entityName}", id);
            if (instance==null) {
                logger.debug("get successful, no instance found");
            }
            else {
                logger.debug("get successful, instance found");
            }
            return instance;
        }
        catch (RuntimeException re) {
            logger.error("get failed", re);
            throw re;
        }
    }
</#if>
    
<#if clazz.hasNaturalId()>
    public ${declarationName} findByNaturalId(${c2j.asNaturalIdParameterList(clazz)}) {
        logger.debug("getting ${declarationName} instance by natural id");
        try {
            ${declarationName} instance = (${declarationName}) sessionFactoryFB.getCurrentSession()
                    .createCriteria("${clazz.entityName}")
<#if jdk5>
                    .add( ${pojo.staticImport("org.hibernate.criterion.Restrictions", "naturalId")}()
<#else>
                   .add( ${pojo.importType("org.hibernate.criterion.Restrictions")}.naturalId()
</#if>                    
<#foreach property in pojo.getAllPropertiesIterator()>
<#if property.isNaturalIdentifier()>
                            .set("${property.name}", ${property.name})
</#if>
</#foreach>
                        )
                    .uniqueResult();
            if (instance==null) {
                logger.debug("get successful, no instance found");
            }
            else {
                logger.debug("get successful, instance found");
            }
            return instance;
        }
        catch (RuntimeException re) {
            logger.error("query failed", re);
            throw re;
        }
    }
</#if>    
<#if jdk5>
    public ${pojo.importType("java.util.List")}<${declarationName}> findByExample(${declarationName} instance) {
<#else>
    public ${pojo.importType("java.util.List")} findByExample(${declarationName} instance) {
</#if>
        logger.debug("finding ${declarationName} instance by example");
        try {
<#if jdk5>
            ${pojo.importType("java.util.List")}<${declarationName}> results = (List<${declarationName}>) sessionFactoryFB.getCurrentSession()
<#else>
            ${pojo.importType("java.util.List")} results = sessionFactoryFB.getCurrentSession()
</#if>
                    .createCriteria("${clazz.entityName}")
<#if jdk5>
                    .add( ${pojo.staticImport("org.hibernate.criterion.Example", "create")}(instance) )
<#else>
                    .add(${pojo.importType("org.hibernate.criterion.Example")}.create(instance))
</#if>
            .list();
            logger.debug("find by example successful, result size: " + results.size());
            return results;
        }
        catch (RuntimeException re) {
            logger.error("find by example failed", re);
            throw re;
        }
    }
    
<#if jdk5>
    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
    public ${pojo.importType("java.util.List")}<${declarationName}> findAll() {
<#else>
	@${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
	public ${pojo.importType("java.util.List")} findAll() {
</#if>
		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class);
<#if jdk5>
		${pojo.importType("java.util.List")}<${declarationName}> list = crit.list();
<#else>
		${pojo.importType("java.util.List")} list = crit.list();
</#if>
		return list;
    }
<#if pojo.getDeclarationName() == "UserAddress">
    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
    public ${pojo.importType("java.util.List")}<${declarationName}> findByForeignCountry(${pojo.importType("com.livescribe.dbmigration.fb.Country")} fbCountry) {
    	
    	String pkCountry = ${pojo.importType("com.livescribe.base.utils.WOAppMigrationUtils")}.convertPrimaryKeyToString(fbCountry.getPrimaryKey());
		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class)
			.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.isNull("usStateKey"))
			.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.eq("countryKey", fbCountry.getPrimaryKey()));
		${pojo.importType("java.util.List")}<${declarationName}> list = crit.list();
		
		return list;
    }
    
    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
    public ${pojo.importType("java.util.List")}<${declarationName}> findByUSState(${pojo.importType("com.livescribe.dbmigration.fb.USState")} fbState) {
    	
    	String pkState = ${pojo.importType("com.livescribe.base.utils.WOAppMigrationUtils")}.convertPrimaryKeyToString(fbState.getPrimaryKey());
		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class)
			.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.eq("usStateKey", fbState.getPrimaryKey()));
		${pojo.importType("java.util.List")}<${declarationName}> list = crit.list();
		
		return list;
    }

	@${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
	public ${pojo.importType("java.util.List")}<${declarationName}> findForeignAddresses() {
		
    	try {
    		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession()
            		.createCriteria(${declarationName}.class).add(${pojo.importType("org.hibernate.criterion.Restrictions")}.isNull("usStateKey"));
    		${pojo.importType("java.util.List")}<${declarationName}> list = crit.list();
    		return list;
    	}
    	catch (RuntimeException re) {
            logger.error("findForeignAddresses():  Failed", re);
            throw re;
    	}
	}
</#if>
<#if pojo.getDeclarationName() == "UserProfile">
    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
    public ${pojo.importType("java.util.List")}<${declarationName}> findByYear(int year) {
    	
    	${pojo.importType("java.util.GregorianCalendar")} start = new ${pojo.importType("java.util.GregorianCalendar")}(year, 0, 1, 0, 0, 0);
    	${pojo.importType("java.util.GregorianCalendar")} end = new ${pojo.importType("java.util.GregorianCalendar")}(year, 11, 31, 12, 59, 59);
    	
		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class)
					.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.between("createdate", start.getTime(), end.getTime()));
		${pojo.importType("java.util.List")}<${declarationName}> list = crit.list();
		
		return list;
    }

    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
	public ${pojo.importType("java.util.List")}<${declarationName}> findByCountry(byte[] countryPk) {
		
    	${pojo.importType("java.util.List")}<${declarationName}> list = null;
    	
    	String countryPkStr = ${pojo.importType("com.livescribe.base.utils.WOAppMigrationUtils")}.convertPrimaryKeyToString(countryPk);
    	if ("00007F0000010000DFFFC4010000011752979C02211A9699".equals(countryPkStr)) {
    		//	TODO:  Implement handling for USA users.
    	}
    	else {
			${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class)
						.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.eq("fk_preferredcountry", countryPk));
			list = crit.list();
    	}
		return list;
	}
    
    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
	public ${pojo.importType("java.util.List")}<${declarationName}> findByDateRange(${pojo.importType("java.util.Date")} start, ${pojo.importType("java.util.Date")} end) {
		
    	${pojo.importType("java.util.List")}<${declarationName}> list = null;
    	
		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class)
					.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.between("createdate", start, end));
		list = crit.list();
    	
		return list;
	}
</#if>
<#if pojo.getDeclarationName() == "UGFile">
    @${pojo.importType("org.springframework.transaction.annotation.Transactional")}("transactionManagerFB")
	public ${pojo.importType("java.util.List")}<${declarationName}> findByDateRange(${pojo.importType("java.util.Date")} start, ${pojo.importType("java.util.Date")} end) {
		
    	${pojo.importType("java.util.List")}<${declarationName}> list = null;
    	
		${pojo.importType("org.hibernate.Criteria")} crit = sessionFactoryFB.getCurrentSession().createCriteria(${declarationName}.class)
					.add(${pojo.importType("org.hibernate.criterion.Restrictions")}.between("fileDate", start, end));
		list = crit.list();
    	
		return list;
	}
</#if>
<#foreach queryName in cfg.namedQueries.keySet()>
<#if queryName.startsWith(clazz.entityName + ".")>
<#assign methname = c2j.unqualify(queryName)>
<#assign params = cfg.namedQueries.get(queryName).parameterTypes><#assign argList = c2j.asFinderArgumentList(params, pojo)>
<#if jdk5 && methname.startsWith("find")>
    public ${pojo.importType("java.util.List")}<${declarationName}> ${methname}(${argList}) {
<#elseif methname.startsWith("count")>
    public int ${methname}(${argList}) {
<#else>
    public ${pojo.importType("java.util.List")} ${methname}(${argList}) {
</#if>
        ${pojo.importType("org.hibernate.Query")} query = sessionFactoryFB.getCurrentSession()
                .getNamedQuery("${queryName}");
<#foreach param in params.keySet()>
<#if param.equals("maxResults")>
		query.setMaxResults(maxResults);
<#elseif param.equals("firstResult")>
        query.setFirstResult(firstResult);
<#else>
        query.setParameter("${param}", ${param});
</#if>
</#foreach>
<#if jdk5 && methname.startsWith("find")>
        return (List<${declarationName}>) query.list();
<#elseif methname.startsWith("count")>
        return ( (Integer) query.uniqueResult() ).intValue();
<#else>
        return query.list();
</#if>
    }
</#if>
</#foreach></#if>
}
</#assign>

${pojo.generateImports()}
${classbody}
</#if>
