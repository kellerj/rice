/*
 * Copyright 2005-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.dao.proxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.impl.BusinessObjectDaoJpa;
import org.kuali.rice.kns.dao.impl.BusinessObjectDaoOjb;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BusinessObjectDaoProxy implements BusinessObjectDao {

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectDaoProxy.class);

	private BusinessObjectDao businessObjectDaoJpa;
    private static KualiModuleService kualiModuleService;
    private static HashMap<String, BusinessObjectDao> boDaoValues = new HashMap<String, BusinessObjectDao>();

    private BusinessObjectDao getDao(Class clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService != null) {
            ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
            String dataSourceName = "";
            EntityManager entityManager = null;
            if (moduleConfig != null) {
                dataSourceName = moduleConfig.getDataSourceName();
                entityManager = moduleConfig.getEntityManager();
            }

            if (StringUtils.isNotEmpty(dataSourceName)) {
                if (boDaoValues.get(dataSourceName) != null) {
                    return boDaoValues.get(dataSourceName);
                } else {
                	
                	BusinessObjectDaoJpa boDaoJpa = new BusinessObjectDaoJpa();
                	if (entityManager != null) {
                		boDaoJpa.setEntityManager(entityManager);
                		boDaoJpa.setPersistenceStructureService(KNSServiceLocator.getPersistenceStructureService());
                		boDaoValues.put(dataSourceName, boDaoJpa);
                		return boDaoJpa;
                	} else {
                		throw new ConfigurationException("EntityManager is null. EntityManager must be set in the Module Configuration bean in the appropriate spring beans xml. (see nested exception for details).");
                	}
                }

            }
        }
        return businessObjectDaoJpa;
    }

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#countMatching(java.lang.Class, java.util.Map)
	 */
	public int countMatching(Class clazz, Map<String, ?> fieldValues) {
		return getDao(clazz).countMatching(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#countMatching(java.lang.Class, java.util.Map, java.util.Map)
	 */
	public int countMatching(Class clazz, Map<String, ?> positiveFieldValues, Map<String, ?> negativeFieldValues) {
		return getDao(clazz).countMatching(clazz, positiveFieldValues, negativeFieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#delete(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public void delete(PersistableBusinessObject bo) {
		if (bo != null) {
			getDao(bo.getClass()).delete(bo);
		}
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#delete(java.util.List)
	 */
	public void delete(List<? extends PersistableBusinessObject> boList) {
		if (!boList.isEmpty()) {
			getDao(boList.get(0).getClass()).delete(boList);
		}
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#deleteMatching(java.lang.Class, java.util.Map)
	 */
	public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
		getDao(clazz).deleteMatching(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAll(java.lang.Class)
	 */
	public <T extends BusinessObject> Collection<T> findAll(Class<T> clazz) {
		return getDao(clazz).findAll(clazz);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllActive(java.lang.Class)
	 */
	public <T extends BusinessObject> Collection<T> findAllActive(Class<T> clazz) {
		return getDao(clazz).findAllActive(clazz);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllInactive(java.lang.Class)
	 */
	public <T extends BusinessObject> Collection<T> findAllInactive(Class<T> clazz) {
		return getDao(clazz).findAllInactive(clazz);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllActiveOrderBy(java.lang.Class, java.lang.String, boolean)
	 */
	public <T extends BusinessObject> Collection<T> findAllActiveOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
		return getDao(clazz).findAllActiveOrderBy(clazz, sortField, sortAscending);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
	 */
	public <T extends BusinessObject> Collection<T> findAllOrderBy(Class<T> clazz, String sortField, boolean sortAscending) {
		return getDao(clazz).findAllOrderBy(clazz, sortField, sortAscending);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findBySinglePrimaryKey(java.lang.Class, java.lang.Object)
	 */
	public <T extends BusinessObject> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
		return getDao(clazz).findBySinglePrimaryKey(clazz, primaryKey);
	}
	
	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findByPrimaryKey(java.lang.Class, java.util.Map)
	 */
	public <T extends BusinessObject> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
		return getDao(clazz).findByPrimaryKey(clazz, primaryKeys);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatching(java.lang.Class, java.util.Map)
	 */
	public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
		return getDao(clazz).findMatching(clazz, fieldValues);
	}

	/**
	 * Has the proxied DAO handle the criteria
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatching(org.kuali.rice.core.jpa.criteria.Criteria)
	 */
	public <T extends BusinessObject> Collection<T> findMatching(Criteria criteria) {
		Class clazz = null;
		try {
			clazz = Class.forName(criteria.getEntityName());
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException("Attempted to run JPA Criteria which uses a non-existent class to query against: "+criteria.getEntityName(), cnfe);
		}
		return getDao(clazz).findMatching(criteria);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatchingActive(java.lang.Class, java.util.Map)
	 */
	public <T extends BusinessObject> Collection<T> findMatchingActive(Class<T> clazz, Map<String, ?> fieldValues) {
		return getDao(clazz).findMatchingActive(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatchingOrderBy(java.lang.Class, java.util.Map, java.lang.String, boolean)
	 */
	public <T extends BusinessObject> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField, boolean sortAscending) {
		return getDao(clazz).findMatchingOrderBy(clazz, fieldValues, sortField, sortAscending);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#retrieve(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public PersistableBusinessObject retrieve(PersistableBusinessObject object) {
		return getDao(object.getClass()).retrieve(object);
	}

	/**
	 * Defers to correct DAO for this class
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findByPrimaryKeyUsingKeyObject(java.lang.Class, java.lang.Object)
	 */
	public <T extends BusinessObject> T findByPrimaryKeyUsingKeyObject(Class<T> clazz, Object pkObject) {
		return getDao(clazz).findByPrimaryKeyUsingKeyObject(clazz, pkObject);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#save(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public PersistableBusinessObject save(PersistableBusinessObject bo) {
		PersistableBusinessObject savedBo;
		savedBo = getDao(bo.getClass()).save(bo);
		return savedBo;
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#save(java.util.List)
	 */
	public List<? extends PersistableBusinessObject> save(List businessObjects) {
		if (!businessObjects.isEmpty()) {
			return getDao(businessObjects.get(0).getClass()).save(businessObjects);
		}
		return new ArrayList<PersistableBusinessObject>();
	}

    private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
        return kualiModuleService;
    }

	public void setBusinessObjectDaoJpa(BusinessObjectDao businessObjectDaoJpa) {
		this.businessObjectDaoJpa = businessObjectDaoJpa;
	}

}
