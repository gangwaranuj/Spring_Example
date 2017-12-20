package com.workmarket.domains.payments.dao;

import com.google.common.collect.Sets;
import com.workmarket.dao.PaginationAbstractDAO;
import com.workmarket.domains.model.company.CompanyStatusType;
import com.workmarket.domains.model.invoice.AbstractInvoice;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public abstract class AbstractInvoiceDAOImpl<T extends AbstractInvoice> extends PaginationAbstractDAO<T> implements AbstractInvoiceDAO<T> {

	public T findInvoiceById(long invoiceId) {
		return (T) getFactory().getCurrentSession().get(AbstractInvoice.class, invoiceId);
	}

	@Override
	public Set<? extends AbstractInvoice> findAllDueInvoicesByDueDate(Calendar dueDateFrom, Calendar dueDateThrough) {
		Assert.notNull(dueDateFrom);
		Assert.notNull(dueDateThrough);
		return Sets.newHashSet(findAllDueInvoices(dueDateFrom, dueDateThrough));
	}

	@Override
	public Set<? extends AbstractInvoice> findAllInvoicesPastDue(Calendar dueDate) {
		Assert.notNull(dueDate);
		return Sets.newHashSet(findAllDueInvoices(null, dueDate));
	}
	
	private List<? extends AbstractInvoice> findAllDueInvoices(Calendar dueDateFrom, Calendar dueDateThrough) {
		Criteria query = getFactory().getCurrentSession().createCriteria(AbstractInvoice.class)
				.add(Restrictions.disjunction()
						.add(Restrictions.eq("class", Invoice.INVOICE_TYPE)) 
				)
				.add(Restrictions.eq("invoiceStatusType.code", InvoiceStatusType.PAYMENT_PENDING))
				.add(Restrictions.eq("deleted", Boolean.FALSE))
				.add(Restrictions.gt("balance", BigDecimal.ZERO))
				.createAlias("company", "company")
				.add(Restrictions.eq("company.companyStatusType.code", CompanyStatusType.ACTIVE));

		if (dueDateFrom != null) {
			query.add(Restrictions.ge("dueDate", dueDateFrom));
		}

		if (dueDateThrough != null) {
			query.add(Restrictions.le("dueDate", dueDateThrough));
		}
		return query.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> get(Long... primaryKeys) {
		return getFactory().getCurrentSession().createCriteria(AbstractInvoice.class)
			.add(Restrictions.in("id", primaryKeys))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
			.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> get(Collection<Long> primaryKeys) {
		return getFactory().getCurrentSession().createCriteria(AbstractInvoice.class)
				.add(Restrictions.in("id", primaryKeys))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

	@Override
	public AbstractInvoice findEarliestDueInvoice(long companyId) {
		return (AbstractInvoice) getFactory().getCurrentSession().createCriteria(AbstractInvoice.class)
				.add(Restrictions.eq("company.id", companyId))
				.add(Restrictions.eq("invoiceStatusType.code", InvoiceStatusType.PAYMENT_PENDING))
				.add(Restrictions.eq("deleted", false))
				.addOrder(Order.asc("dueDate"))
				.setMaxResults(1)
				.uniqueResult();
	}
}