/**
 * 
 */
package com.livescribe.admin.service;

import com.livescribe.admin.controller.dto.InfoLookupResultDto;
import com.livescribe.admin.lookup.LookupCriteriaList;

/**
 * @author Mohammad M. Naqvi
 *
 */
public interface InfoLookupService {

	InfoLookupResultDto doInfoLookupByCriteria(LookupCriteriaList criteriaList);

}
