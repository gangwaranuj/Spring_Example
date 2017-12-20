package com.workmarket.web.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.thrift.work.Work;
import com.workmarket.thrift.work.WorkResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class MenuHelper {
	private static final Log logger = LogFactory.getLog(MenuHelper.class);
	private static Map<String, Object> nav = null;

	static {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> temp = objectMapper.readValue(MenuHelper.class.getClassLoader().getResourceAsStream("menu-definitions.json"), Map.class);
			nav = ImmutableMap.copyOf(temp);
		} catch (IOException ioe) {
			logger.error("Failed to load assignment detail navigation mappings", ioe);
		}
	}

	@SuppressWarnings("unchecked")
	List<Object> getMenu(String id, Map<String, Object> navMap) {
		String[] parts = id.split("\\.");
		List<Object> badRet = Lists.newArrayList();
		Map<String, Object> navPointer = navMap;
		int idx = 0;
		for (String part : parts) {
			if (navPointer.get(part) == null) { return badRet; }
			if (idx++ == parts.length - 1) {
				if (navPointer.get(part) instanceof List) {
					return deepCopy(navPointer.get(part));
				} else {
					return badRet;
				}
			}
			else {
				navPointer = (Map<String, Object>)navPointer.get(part);
			}
		}
		return badRet;
	}

	@SuppressWarnings("unchecked")
	List<Object> getMenu(String id) {
		return getMenu(id, nav);
	}

	private void _removeItem(String itemToRemove, List<Object> nav) {
		for (int i=0; i < nav.size(); i++) {
			Object item = nav.get(i);
			boolean separatorFlag = (i < nav.size() - 1 && nav.get(i+1) instanceof String && nav.get(i+1).equals("-"));
			if (item instanceof  String && item.equals(itemToRemove)) {
				if (separatorFlag) { nav.remove(i+1); }
				nav.remove(i);
			} else if (item instanceof List) {
				_removeItem(itemToRemove, (List<Object>)item);
			}
		}
	}

	public List<Object> removeItem(String itemToRemove, List<Object> nav) {
		List<Object> retList = Lists.newArrayList(nav);
		_removeItem(itemToRemove, retList);
		return retList;
	}

	/**
	 * generate the menu items for assignment nav based on the permissions
	 */
	@SuppressWarnings("unchecked")
	public List<Object> populateNavigationList(ModelMap model, String status, Boolean hasMasqueradeRole) {

		Boolean isAdmin = isTrue((Boolean) model.get("is_admin"));
		Boolean isOwner = isTrue((Boolean) model.get("is_owner"));
		Boolean isResource = isTrue((Boolean) model.get("is_resource"));
		Boolean isActiveResource = isTrue((Boolean) model.get("is_active_resource"));
		Boolean isInternal = isTrue((Boolean) model.get("is_internal"));
		Boolean isCompanyResource = isTrue((Boolean) model.get("isCompanyResource"));
		Boolean isWorkBundle = model.get("parent") != null;

		//Current work has locked invoice
		Work work = (Work) model.get("work");
		WorkResponse workResponse = (WorkResponse) model.get("workResponse");
		Boolean hasLockedInvoices = work.getInvoice() == null || !work.getInvoice().isEditable();
		Boolean isInternalAssignment = PricingStrategyType.INTERNAL.equals(work.getPricing().getType());
		String bundleQualifier = isWorkBundle ? "bundle." : "";

		List<Object> result = Lists.newArrayList();

		// TODO: these need a full audit from Product, seems there is some duplication and weirdness
		if (isCompanyResource) {
			if (isOwner && (isResource || isActiveResource)) {
				if (WorkStatusType.SENT.equals(status)) {
					result = getMenu(String.format("companyResource.ownerAndResourceOrActiveResource.%ssent", bundleQualifier));
				} else if (WorkStatusType.ACTIVE.equals(status)) {
					result = (workResponse.isInWorkBundle()) ?
						getMenu("companyResource.ownerAndResourceOrActiveResource.activeInBundle") :
						getMenu(String.format("companyResource.ownerAndResourceOrActiveResource.%sactive", bundleQualifier));
				} else if (WorkStatusType.COMPLETE.equals(status)) {
					result = getMenu(String.format("companyResource.ownerAndResourceOrActiveResource.%scomplete", bundleQualifier));
				} else if (WorkStatusType.PAID.equals(status) || WorkStatusType.PAYMENT_PENDING.equals(status)) {
					result = getMenu(String.format("companyResource.ownerAndResourceOrActiveResource.%spaidOrPaymentPending", bundleQualifier));
				} else if (WorkStatusType.CANCELLED.equals(status)) {
					result = getMenu(String.format("companyResource.ownerAndResourceOrActiveResource.%scancelled", bundleQualifier));
				} else if (WorkStatusType.DECLINED.equals(status) || WorkStatusType.EXCEPTION.equals(status)) {
					result = getMenu(String.format("companyResource.ownerAndResourceOrActiveResource.%sdeclinedOrException", bundleQualifier));
				}
			} else if ((isAdmin || isInternal) && !isActiveResource && !isResource) {
				// NOTE: WM CSRs get the same toolbar as the admin. If changes are necessary, break out into a unique toolbar config
				if (WorkStatusType.DRAFT.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.default", bundleQualifier));
					}
				} else if (WorkStatusType.SENT.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.default", bundleQualifier));
					}
				} else if (WorkStatusType.ACTIVE.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.default", bundleQualifier));
					}
				} else if (WorkStatusType.COMPLETE.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.default", bundleQualifier));
					}
				} else if ((WorkStatusType.PAYMENT_PENDING.equals(status) || WorkStatusType.CANCELLED_PAYMENT_PENDING.equals(status)) && !hasLockedInvoices) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paymentPendingAndNotLockedInvoices.masqueradeAndInternalAndInternalAssignment");
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paymentPendingAndNotLockedInvoices.masqueradeAndInternal");
					} else if (isInternalAssignment) {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paymentPendingAndNotLockedInvoices.internalAssignment");
					} else {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paymentPendingAndNotLockedInvoices.default");
					}
				} else if ((WorkStatusType.PAID.equals(status)) || (WorkStatusType.PAYMENT_PENDING.equals(status))) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.masqueradeAndInternalAndInternalAssignment");
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.masqueradeAndInternal");
					} else if (isInternalAssignment) {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.internalAssignment");
					} else {
						result = getMenu("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.default");
					}
				} else if (WorkStatusType.CANCELLED.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.default", bundleQualifier));
					}
				} else if (WorkStatusType.DECLINED.equals(status) || WorkStatusType.EXCEPTION.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.default", bundleQualifier));
					}
					// end: admin/owner
					// start: companyResource but not owner/admin
				} else {
					if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdefault.masqueradeAndInternal", bundleQualifier));
					} else {
						result = getMenu(String.format("companyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdefault.default", bundleQualifier));
					}
				}
			} else if (WorkStatusType.SENT.equals(status)) {
				result = getMenu(String.format("companyResource.%ssent", bundleQualifier));
			} else if (WorkStatusType.ACTIVE.equals(status)) {
				if (hasMasqueradeRole && isInternal) {
					result = getMenu(String.format("companyResource.active.%smasqueradeAndInternal", bundleQualifier));
				} else {
					result = (workResponse.isInWorkBundle()) ?
						getMenu("companyResource.active.defaultInBundle") :
						getMenu(String.format("companyResource.active.%sdefault", bundleQualifier));
				}
			} else {
				result = getMenu(String.format("companyResource.%sdefault", bundleQualifier));
			}
		} else { // !isCompanyResource
			if (isOwner && (isResource || isActiveResource)) {
				if (WorkStatusType.SENT.equals(status)) {
					result = getMenu(String.format("notCompanyResource.ownerAndResourceOrActiveResource.%ssent", bundleQualifier));
				} else if (WorkStatusType.ACTIVE.equals(status)) {
					result = (workResponse.isInWorkBundle()) ?
						getMenu("notCompanyResource.ownerAndResourceOrActiveResource.activeInBundle") :
						getMenu(String.format("notCompanyResource.ownerAndResourceOrActiveResource.%sactive", bundleQualifier));
				} else if (WorkStatusType.COMPLETE.equals(status)) {
					result = getMenu(String.format("notCompanyResource.ownerAndResourceOrActiveResource.%scomplete", bundleQualifier));
				} else if (WorkStatusType.PAID.equals(status) || WorkStatusType.PAYMENT_PENDING.equals(status)) {
					result = getMenu(String.format("notCompanyResource.ownerAndResourceOrActiveResource.%spaidOrPaymentPending", bundleQualifier));
				} else if (WorkStatusType.CANCELLED.equals(status)) {
					result = getMenu(String.format("notCompanyResource.ownerAndResourceOrActiveResource.%scancelled", bundleQualifier));
				} else if (WorkStatusType.DECLINED.equals(status) || WorkStatusType.EXCEPTION.equals(status)) {
					result = getMenu(String.format("notCompanyResource.ownerAndResourceOrActiveResource.%sdeclinedOrException", bundleQualifier));
				}
				// admin or internal: NOTE: WM CSRs get the same toolbar as the admin.
			} else if ((isAdmin || isInternal) && !isActiveResource && !isResource) {
				if (WorkStatusType.DRAFT.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdraft.default", bundleQualifier));
					}
				} else if (WorkStatusType.SENT.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%ssent.default", bundleQualifier));
					}
				} else if (WorkStatusType.ACTIVE.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sactive.default", bundleQualifier));
					}
				} else if (WorkStatusType.COMPLETE.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scomplete.default", bundleQualifier));
					}
				} else if (WorkStatusType.PAID.equals(status) || WorkStatusType.PAYMENT_PENDING.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.masqueradeAndInternalAndInternalAssignment");
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.masqueradeAndInternal");
					} else if (isInternalAssignment) {
						result = getMenu("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.internalAssignment");
					} else {
						result = getMenu("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.paidOrPaymentPending.default");
					}
				} else if (WorkStatusType.CANCELLED.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%scancelled.default", bundleQualifier));
					}
				} else if (WorkStatusType.DECLINED.equals(status) || WorkStatusType.EXCEPTION.equals(status)) {
					if (hasMasqueradeRole && isInternal && isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.masqueradeAndInternalAndInternalAssignment", bundleQualifier));
					} else if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.masqueradeAndInternal", bundleQualifier));
					} else if (isInternalAssignment) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.internalAssignment", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdeclinedOrException.default", bundleQualifier));
					}
				} else {
					if (hasMasqueradeRole && isInternal) {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdefault.masqueradeAndInternal", bundleQualifier));
					} else {
						result = getMenu(String.format("notCompanyResource.adminOrInternalAndNotActiveResourceAndNotResource.%sdefault.default", bundleQualifier));
					}
				}
				// end admin/WM CSR
				// start non-company resource
			} else if (WorkStatusType.SENT.equals(status)) {
				result = getMenu(String.format("notCompanyResource.%ssent", bundleQualifier));
			} else if (WorkStatusType.ACTIVE.equals(status)) {
				result = (workResponse.isInWorkBundle()) ?
					getMenu("notCompanyResource.activeInBundle") :
					getMenu(String.format("notCompanyResource.%sactive", bundleQualifier));
			} else if (WorkStatusType.COMPLETE.equals(status) || WorkStatusType.PAID.equals(status)) {
				result = getMenu(String.format("notCompanyResource.%scompleteOrPaid", bundleQualifier));
			} else {
				result = getMenu(String.format("notCompanyResource.%sdefault", bundleQualifier));
			}
		}
		return result;
	}

	private List deepCopy(Object oldObj) {
		ObjectOutputStream oos;
		ObjectInputStream ois;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);

			oos.writeObject(oldObj);
			oos.flush();
			ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bin);

			Object ob = ois.readObject();
			oos.close();
			ois.close();
			return (List)ob;
		}
		catch (Exception e) {
			logger.error("[MenuHelper] Exception cloning nav items = " + e);
		}
		return Lists.newArrayList();
	}
}
