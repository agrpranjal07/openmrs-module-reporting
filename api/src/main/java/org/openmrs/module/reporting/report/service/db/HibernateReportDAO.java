/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.reporting.report.service.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.ReportRequest.Status;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.renderer.ReportRenderer;

import java.util.Date;
import java.util.List;

/**
 * ReportService Database Access Interface
 */
public class HibernateReportDAO implements ReportDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	private DbSessionFactory sessionFactory;
	
	//***** INSTANCE METHODS *****
	
	//****** REPORT DESIGNS *****
	
	/**
	 * @param uuid
	 * @return the ReportDesign with the given uuid
	 */
	public ReportDesign getReportDesignByUuid(String uuid) throws DAOException {
		Query q = sessionFactory.getCurrentSession().createQuery("from ReportDesign r where r.uuid = :uuid");
		return (ReportDesign) q.setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * Get the {@link ReportDesign} with the given id
	 * @param id The Integer ReportDesign id
	 * @return the matching {@link ReportDesign} object
	 * @throws DAOException
	 */
	public ReportDesign getReportDesign(Integer id) throws DAOException {
		return (ReportDesign) sessionFactory.getCurrentSession().get(ReportDesign.class, id);
	}
		
	/**
	 * Return a list of {@link ReportDesign}s for the passed {@link ReportDefinition} and {@link ReportRenderer} class,
	 * optionally including those that are retired
	 * @param includeRetired if true, indicates that retired {@link ReportDesign}s should also be included
	 * @return a List<ReportDesign> object containing all of the {@link ReportDesign}s
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ReportDesign> getReportDesigns(ReportDefinition reportDefinition, Class<? extends ReportRenderer> rendererType, 
											   boolean includeRetired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(ReportDesign.class);
		if (reportDefinition != null) {
			crit.add(Expression.eq("reportDefinition", reportDefinition));
		}
		if (rendererType != null) {
			crit.add(Expression.eq("rendererType", rendererType));
		}
		if (includeRetired == false) {
			crit.add(Expression.eq("retired", false));
		}
		return crit.list();
	}
	
	/**
	 * Save or update the given <code>ReportDesign</code> in the database. If this is a new
	 * ReportDesign, the returned ReportDesign will have a new
	 * {@link ReportDesign#getId()} inserted into it that was generated by the database
	 * 
	 * @param reportDesign The <code>ReportDesign</code> to save or update
	 * @throws DAOException
	 */
	public ReportDesign saveReportDesign(ReportDesign reportDesign) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(reportDesign);
		return reportDesign;
	}
	
	/**
	 * Purges a <code>ReportDesign</code> from the database.
	 * @param reportDesign The <code>ReportDesign</code> to remove from the system
	 * @throws DAOException
	 */
	public void purgeReportDesign(ReportDesign reportDesign) {
		sessionFactory.getCurrentSession().delete(reportDesign);
	}
	
	//****** REPORT PROCESSOR CONFIGURATIONS *****
	
	/**
	 * Saves a {@link ReportProcessorConfiguration} to the database and returns it
	 */
	public ReportProcessorConfiguration saveReportProcessorConfiguration(ReportProcessorConfiguration processorConfiguration) {
		sessionFactory.getCurrentSession().saveOrUpdate(processorConfiguration);
		return processorConfiguration;
	}

	/**
	 * @return the {@link ReportProcessorConfiguration} with the passed id
	 */
	public ReportProcessorConfiguration getReportProcessorConfiguration(Integer id) {
		return (ReportProcessorConfiguration) sessionFactory.getCurrentSession().get(ReportProcessorConfiguration.class, id);
	}

	/**
	 * @return the {@link ReportProcessorConfiguration} with the passed uuid
	 */
	public ReportProcessorConfiguration getReportProcessorConfigurationByUuid(String uuid) {
		Query q = sessionFactory.getCurrentSession().createQuery("from ReportProcessorConfiguration r where r.uuid = :uuid");
		return (ReportProcessorConfiguration) q.setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @return all the {@link ReportProcessorConfiguration}s
	 */
	@SuppressWarnings("unchecked")
	public List<ReportProcessorConfiguration> getAllReportProcessorConfigurations(boolean includeRetired) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(ReportProcessorConfiguration.class);
		if (includeRetired == false) {
			crit.add(Expression.eq("retired", false));
		}
		return crit.list();
	}
	
	/**
	 * @return all the {@link ReportProcessorConfiguration}s that are meant to be applied globally, i.e., their reportDesign property is null
	 */
	@SuppressWarnings("unchecked")
	public List<ReportProcessorConfiguration> getGlobalReportProcessorConfigurations() {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(ReportProcessorConfiguration.class);
		crit.add(Expression.eq("retired", false));
		crit.add(Expression.isNull("reportDesign"));
		return crit.list();
	}
	
	/**
	 * Deletes the passed {@link ReportProcessorConfiguration}
	 */
	public void purgeReportProcessorConfiguration(ReportProcessorConfiguration processorConfiguration) {
		sessionFactory.getCurrentSession().delete(processorConfiguration);
	}
	
	//****** REPORT REQUESTS *****
	
	/**
	 * @see ReportDAO#saveReportRequest(ReportRequest)
	 */
	public ReportRequest saveReportRequest(ReportRequest request) {
		sessionFactory.getCurrentSession().saveOrUpdate(request);
		return request;
	}

	/**
	 * @see ReportDAO#getReportRequest(java.lang.Integer)
	 */
	public ReportRequest getReportRequest(Integer id) {
		return (ReportRequest) sessionFactory.getCurrentSession().get(ReportRequest.class, id);
	}

	/**
	 * @see ReportDAO#getReportRequestByUuid(java.lang.String)
	 */
	public ReportRequest getReportRequestByUuid(String uuid) {
		Query q = sessionFactory.getCurrentSession().createQuery("from ReportRequest r where r.uuid = :uuid");
		return (ReportRequest) q.setString("uuid", uuid).uniqueResult();
	}

	/**
	 * @see ReportDAO#getReportRequests(ReportDefinition, Date, Date, Integer, Integer, Status...)
	 */
	@SuppressWarnings("unchecked")
	public List<ReportRequest> getReportRequests(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Integer firstResult, Integer maxResults, Status...statuses) {
		final Criteria criteria = createReportRequestsBaseCriteria(reportDefinition, requestOnOrAfter, requestOnOrBefore, statuses);

		criteria.addOrder(Order.desc("requestDate"));
		criteria.addOrder(Order.desc("evaluateStartDatetime"));
		criteria.addOrder(Order.desc("evaluateCompleteDatetime"));
		criteria.addOrder(Order.desc("priority"));

		if (firstResult != null) {
			criteria.setFirstResult(firstResult);
		}

		if(maxResults != null) {
			criteria.setMaxResults(maxResults);
		}

		return criteria.list();
	}

	@Override
	public long getReportRequestsCount(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Status... statuses) {
		final Criteria criteria = createReportRequestsBaseCriteria(reportDefinition, requestOnOrAfter, requestOnOrBefore, statuses);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).longValue();
	}

	/**
	 * @see ReportDAO#purgeReportRequest(ReportRequest)
	 */
	public void purgeReportRequest(ReportRequest request) {
		sessionFactory.getCurrentSession().delete(request);
	}

	/**
	 * @see ReportDAO#purgeReportRequestsForReportDefinition(String)
	 */
	@Override
	public void purgeReportRequestsForReportDefinition(String reportDefinitionUuid) {
		String hql = "delete from ReportRequest r where r.reportDefinition.definition=:uuid";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setString("uuid", reportDefinitionUuid);
		query.executeUpdate();
	}


	/**
	 * @see ReportDAO#purgeReportDesignsForReportDefinition(String)
	 */
	@Override
	public void purgeReportDesignsForReportDefinition(String reportDefinitionUuid) {
		String hql = "delete from ReportDesign r where r.reportDefinition=:uuid";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setString("uuid", reportDefinitionUuid);
		query.executeUpdate();
	}

	/**
	 * @see ReportDAO#getReportRequestUuids(String)
	 */
	@Override
	public List<String> getReportRequestUuids(String reportDefinitionUuid) {
		String hql = "select uuid from ReportRequest r where r.reportDefinition.definition=:uuid";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setString("uuid", reportDefinitionUuid);
		return query.list();
	}

	//***** PROPERTY ACCESS *****

	/**
	 * @return the sessionFactory
	 */
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Criteria createReportRequestsBaseCriteria(ReportDefinition reportDefinition, Date requestOnOrAfter, Date requestOnOrBefore, Status... statuses) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ReportRequest.class);

		if (reportDefinition != null) {
			criteria.add(Restrictions.eq("reportDefinition.definition", reportDefinition.getUuid()));
		}
		if (requestOnOrAfter != null) {
			criteria.add(Restrictions.ge("requestDate", requestOnOrAfter));
		}
		if (requestOnOrBefore != null) {
			criteria.add(Restrictions.le("requestDate", requestOnOrBefore));
		}
		if (statuses != null && statuses.length > 0) {
			criteria.add(Restrictions.in("status", statuses));
		}

		return criteria;
	}
}

