package com.workmarket.domains.work.model.follow;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity(name = "workFollow")
@Table(name = "work_follow")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@NamedQueries({
		// Find a specific user-work instance, if it exists
		@NamedQuery(
				name = "workFollow.byWorkIdUserId",
				query = "FROM workFollow WHERE work.id = :workId AND user.id = :userId"),
		// Find all users following a work
		@NamedQuery(
				name = "workFollow.findFollowers",
				query = "FROM workFollow WHERE work.id = :workId and deleted = 0 ORDER BY user.firstName, user.lastName"),
		// Find all users following a work
		@NamedQuery(
				name = "workFollow.findFollowersByWorkNumber",
				query = "FROM workFollow WHERE work.workNumber = :workNumber and deleted = 0 ORDER BY user.firstName, user.lastName"),
		// Find all works that a user is following
		@NamedQuery(
				name = "workFollow.findFollowing",
				query = "FROM workFollow WHERE user.id = :userId and deleted = 0")
})
@AuditChanges
public class WorkFollow extends DeletableEntity {
	private static final long serialVersionUID = 4028481000302156616L;

	private AbstractWork work;
	private User user;

	public WorkFollow() {
	}

	public WorkFollow(AbstractWork work, User user) {
		setWork(work);
		setUser(user);
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id", referencedColumnName = "id", updatable = false)
	public AbstractWork getWork() {
		return work;
	}

	public void setWork(AbstractWork work) {
		this.work = work;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
