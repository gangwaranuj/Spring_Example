package com.workmarket.domains.work.service.part;

import com.google.common.util.concurrent.Callables;
import com.workmarket.common.core.RequestContext;
import com.workmarket.jan20.CallableObservableWrapper;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.service.infra.security.SecurityContext;
import com.workmarket.service.web.EventQueueContext;
import com.workmarket.service.web.WebRequestContextProvider;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

import java.util.concurrent.Callable;

@Component
public class HibernateTrialWrapper implements CallableObservableWrapper {
	private final SessionFactory factory;
	private final EventQueueContext eventQueueContext;
	private final SecurityContext securityContext;
	private final WebRequestContextProvider webRequestContextProvider;

	@Autowired
	public HibernateTrialWrapper(@Qualifier("sessionFactory") final SessionFactory factory,
		final EventQueueContext eventQueueContext, final SecurityContext securityContext,
		final WebRequestContextProvider webRequestContextProvider) {
		this.factory = factory;
		this.eventQueueContext = eventQueueContext;
		this.securityContext = securityContext;
		this.webRequestContextProvider = webRequestContextProvider;
	}

	@Override
	public <T> Callable<Observable<T>> wrap(final Callable<Observable<T>> callableObservable) {
		final RequestContext context = webRequestContextProvider.getRequestContext();
		final SecurityContext.Container securityContainer = securityContext.getContainer();
		final EventQueueContext.Container eventQueueContainer = eventQueueContext.getContainer();

		return Callables.returning(Observable.create(new OnSubscribe<T>() {
			@Override
			public void call(final Subscriber<? super T> subscriber) {
				final Session session = factory.openSession();
				try {
					securityContext.setContainer(securityContainer);
					webRequestContextProvider.setRequestContext(context);
					eventQueueContext.setContainer(eventQueueContainer);
					AbstractDAO.setSession(session);
					callableObservable.call().subscribe(subscriber);
				} catch (final Exception e) {
					subscriber.onError(e);
				} finally {
					webRequestContextProvider.clear();
					securityContext.clearContext();
					eventQueueContext.clearEvents();
					AbstractDAO.setSession(null);
					session.close();
				}
			}
		}));
	}
}
