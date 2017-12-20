package com.workmarket.service.infra.security;

import com.workmarket.domains.model.User;
import com.workmarket.service.infra.security.SecurityContext.Container;
import org.springframework.stereotype.Component;

@Component
public class SecurityContext {
	public static class Container {
		private User currentUser;
		private User masqueradeUser;
		private boolean isThriftContext = true;
		private Long currentUserId;
		private Long currentUserCompanyId;
		private String currentUserCompanyUuid;
		private Long masqueradedUserId;
		private Long masqueradedUserCompanyId;
		private String masqueradedUserCompanyUuid;

		public Long getCurrentUserId() {
			return currentUserId;
		}

		public void setCurrentUserId(Long currentUserId) {
			this.currentUserId = currentUserId;
		}

		public Long getCurrentUserCompanyId() {
			return currentUserCompanyId;
		}

		public void setCurrentUserCompanyId(Long currentUserCompanyId) {
			this.currentUserCompanyId = currentUserCompanyId;
		}

		public String getCurrentUserCompanyUuid() {
			return currentUserCompanyUuid;
		}

		public void setCurrentUserCompanyUuid(String currentUserCompanyUuid) {
			this.currentUserCompanyUuid = currentUserCompanyUuid;
		}

		public Long getMasqueradedUserId() {
			return masqueradedUserId;
		}

		public void setMasqueradedUserId(Long masqueradedUserId) {
			this.masqueradedUserId = masqueradedUserId;
		}

		public Long getMasqueradedUserCompanyId() {
			return masqueradedUserCompanyId;
		}

		public void setMasqueradedUserCompanyId(Long masqueradedUserCompanyId) {
			this.masqueradedUserCompanyId = masqueradedUserCompanyId;
		}

		public String getMasqueradedUserCompanyUuid() {
			return masqueradedUserCompanyUuid;
		}

		public void setMasqueradedUserCompanyUuid(String masqueradedUserCompanyUuid) {
			this.masqueradedUserCompanyUuid = masqueradedUserCompanyUuid;
		}

		public User getCurrentUser() {
			return currentUser;
		}

		public void setCurrentUser(User currentUser) {
			this.currentUser = currentUser;

			if (currentUser != null) {
				this.currentUserId = currentUser.getId();

				if (currentUser.getCompany() != null) {
					this.currentUserCompanyId = currentUser.getCompany().getId();
					this.currentUserCompanyUuid = currentUser.getCompany().getUuid();
				}
			}
		}

		public User getMasqueradeUser() {
			return masqueradeUser;
		}

		public void setMasqueradeUser(User masqueradeUser) {
			this.masqueradeUser = masqueradeUser;

			if (masqueradeUser != null) {
				this.masqueradedUserId = masqueradeUser.getId();

				if (masqueradeUser.getCompany() != null) {
					this.masqueradedUserCompanyId = masqueradeUser.getCompany().getId();
					this.masqueradedUserCompanyUuid = masqueradeUser.getCompany().getUuid();
				}
			}
		}

		public Boolean isThriftContext() {
			return isThriftContext;
		}

		public void setIsThriftContext(Boolean thriftContext) {
			this.isThriftContext = thriftContext;
		}
	}

	static ThreadLocal<Container> threadLocalStore = new ThreadLocal<>();

	/** Should only be used by things that need to execute things in background threads. */
	public synchronized Container getContainer() {
		if (threadLocalStore.get() == null) {
			threadLocalStore.set(new Container());
		}

		return threadLocalStore.get();
	}

	/** Should only be used by things that need to execute things in background threads. */
	public synchronized void setContainer(Container container) {
		threadLocalStore.set(container);
	}

	public synchronized void clearContext() {
		threadLocalStore.remove();
	}

	public synchronized void setCurrentUser(User user) {
		getContainer().setCurrentUser(user);
	}

	public synchronized User getCurrentUser() {
		return getContainer().getCurrentUser();
	}

	public synchronized Long getCurrentUserId() {
		return getContainer().getCurrentUserId();
	}

	public synchronized Long getCurrentUserCompanyId() {
		return getContainer().getCurrentUserCompanyId();
	}

	public synchronized String getCurrentUserCompanyUuid() {
		return getContainer().getCurrentUserCompanyUuid();
	}


	public synchronized void setMasqueradeUser(User masqueradeUser) {
		getContainer().setMasqueradeUser(masqueradeUser);
	}

	public synchronized User getMasqueradeUser() {
		return getContainer().getMasqueradeUser();
	}

	public synchronized Long getMasqueradeUserId() {
		return getContainer().getMasqueradedUserId();
	}

	public synchronized Long getMasqueradeUserCompanyId() {
		return getContainer().getMasqueradedUserCompanyId();
	}

	public synchronized boolean isMasquerading() {
		return getMasqueradeUser() != null;
	}

	public synchronized void setIsThriftContext(boolean thriftContext) {
		getContainer().setIsThriftContext(thriftContext);
	}

	public synchronized boolean isThriftContext() {
		return getContainer().isThriftContext();
	}

	public synchronized void setCurrentAndMasqueradeUsers(User currentUser, User masqueradeUser) {
		getContainer().setCurrentUser(currentUser);
		getContainer().setMasqueradeUser(masqueradeUser);
	}
}
