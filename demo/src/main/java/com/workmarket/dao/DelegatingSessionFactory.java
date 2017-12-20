package com.workmarket.dao;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.TypeHelper;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public class DelegatingSessionFactory implements SessionFactory {
    private final SessionFactory delegate;
    private final Session currentSession;

    public DelegatingSessionFactory(final SessionFactory delegate, final Session currentSession) {
        this.delegate = delegate;
        this.currentSession = currentSession;
    }

    public void close() throws HibernateException {
        delegate.close();
    }

    public boolean containsFetchProfileDefinition(String arg0) {
        return delegate.containsFetchProfileDefinition(arg0);
    }

    public void evict(Class arg0, Serializable arg1) throws HibernateException {
        delegate.evict(arg0, arg1);
    }

    public void evict(Class arg0) throws HibernateException {
        delegate.evict(arg0);
    }

    public void evictCollection(String arg0, Serializable arg1) throws HibernateException {
        delegate.evictCollection(arg0, arg1);
    }

    public void evictCollection(String arg0) throws HibernateException {
        delegate.evictCollection(arg0);
    }

    public void evictEntity(String arg0, Serializable arg1) throws HibernateException {
        delegate.evictEntity(arg0, arg1);
    }

    public void evictEntity(String arg0) throws HibernateException {
        delegate.evictEntity(arg0);
    }

    public void evictQueries() throws HibernateException {
        delegate.evictQueries();
    }

    public void evictQueries(String arg0) throws HibernateException {
        delegate.evictQueries(arg0);
    }

    public Map<String, ClassMetadata> getAllClassMetadata() {
        return delegate.getAllClassMetadata();
    }

    public Map getAllCollectionMetadata() {
        return delegate.getAllCollectionMetadata();
    }

    public Cache getCache() {
        return delegate.getCache();
    }

    public ClassMetadata getClassMetadata(Class arg0) {
        return delegate.getClassMetadata(arg0);
    }

    public ClassMetadata getClassMetadata(String arg0) {
        return delegate.getClassMetadata(arg0);
    }

    public CollectionMetadata getCollectionMetadata(String arg0) {
        return delegate.getCollectionMetadata(arg0);
    }

    public Session getCurrentSession() throws HibernateException {
        return currentSession;
    }

    public Set getDefinedFilterNames() {
        return delegate.getDefinedFilterNames();
    }

    public FilterDefinition getFilterDefinition(String arg0) throws HibernateException {
        return delegate.getFilterDefinition(arg0);
    }

    public Reference getReference() throws NamingException {
        return delegate.getReference();
    }

    public Statistics getStatistics() {
        return delegate.getStatistics();
    }

    public TypeHelper getTypeHelper() {
        return delegate.getTypeHelper();
    }

    public boolean isClosed() {
        return delegate.isClosed();
    }

    public Session openSession() throws HibernateException {
        return delegate.openSession();
    }

    public Session openSession(Connection arg0, Interceptor arg1) {
        return delegate.openSession(arg0, arg1);
    }

    public Session openSession(Connection arg0) {
        return delegate.openSession(arg0);
    }

    public Session openSession(Interceptor arg0) throws HibernateException {
        return delegate.openSession(arg0);
    }

    public StatelessSession openStatelessSession() {
        return delegate.openStatelessSession();
    }

    public StatelessSession openStatelessSession(Connection arg0) {
        return delegate.openStatelessSession(arg0);
    }
}
