package com.workmarket.domains.model;

import java.util.Map;
import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@MappedSuperclass
public class AbstractEntityAbstractEntityAssociation<Strong extends AbstractEntity, Weak extends AbstractEntity> extends DeletableEntity {

	private static final long	serialVersionUID	= 1L;
	private Strong				strong;
	private Weak				weak;

	public AbstractEntityAbstractEntityAssociation() {}

	public AbstractEntityAbstractEntityAssociation(Strong strong, Weak weak) {
		this.strong = strong;
		this.weak = weak;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "strong_id", referencedColumnName = "id", updatable = false)
	public Strong getStrong() {
		return strong;
	}

	public void setStrong(Strong strong) {
		this.strong = strong;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "weak_id", referencedColumnName = "id", updatable = false)
	public Weak getWeak() {
		return weak;
	}

	public void setWeak(Weak weak) {
		this.weak = weak;
	}

	/**
	 * <pre>
	 * Associates the weak entities with the strong one
	 * Returns the updated associations
	 * <b>Note:</b> It modifies existing associations but new ones require you to use the returned collection
	 * </pre>
	 *
	 * @param strong
	 *            The dominant entity in the relation
	 * @param toAssociate
	 *            The weak entities you want associated with the strong one
	 * @param currentAssociations
	 *            The current associations between the strong entity and the weak ones.
	 * @param actualClass
	 *            The actual class of {@code ?} which extends {@code AbstractEntityAbstractEntityAssociation}
	 * @return A {@code Set<? extends AbstractEntityAbstractEntityAssociation<Strong, Weak>>} containing the updated associations between the strong entity and the weak ones defined in toAssociate
	 */
	public static <Strong extends AbstractEntity, Weak extends AbstractEntity> Set<? extends AbstractEntityAbstractEntityAssociation<Strong, Weak>> updateAssociations(Strong strong,
			Set<Weak> toAssociate, Set<? extends AbstractEntityAbstractEntityAssociation<Strong, Weak>> currentAssociations,
			Class<? extends AbstractEntityAbstractEntityAssociation<Strong, Weak>> actualClass) {
		Set<AbstractEntityAbstractEntityAssociation<Strong, Weak>> associations = Sets.newLinkedHashSet();

		/**
		 * If the association already existed and we no longer need it, delete it
		 * If it was deleted and we need it, undelete it and remember not to duplicate it later
		 */
		Map<Long, Weak> toAssociateMap = AbstractEntityUtilities.newEntityIdMap(Lists.newArrayList(toAssociate));
		for (AbstractEntityAbstractEntityAssociation<Strong, Weak> association : currentAssociations) {
			if (!toAssociateMap.keySet().contains(association.getWeak().getId())) {
				if (!association.getDeleted())
					association.setDeleted(true);
			} else {
				if (association.getDeleted())
					association.setDeleted(false);
				toAssociateMap.remove(association.getWeak().getId());
			}
			associations.add(association);
		}

		for (Weak weak : toAssociateMap.values()) {
			AbstractEntityAbstractEntityAssociation<Strong, Weak> association;
			/**
			 * Use object's actual class which extends AbstractEntityAbstractEntityAssociation to initialize the object
			 * This could fail if the class does not define an empty constructor
			 */
			try {
				association = actualClass.newInstance();
				association.setWeak(weak);
				association.setStrong(strong);
				associations.add(association);
			} catch (InstantiationException e) {
				LogFactory.getLog(actualClass.getClass()).error("You must define an empty constructor for this class: " + actualClass.getClass().getName());
			} catch (IllegalAccessException e) {
				LogFactory.getLog(actualClass.getClass()).error("The constructor of this class: " + actualClass.getClass().getName() + " must be public");
			}
		}

		return associations;
	}

	/**
	 * <pre>
	 * Returns the weak entities whose associations with the strong one haven't been deleted
	 * <b>Note:</b> Does not modify existing associations
	 * </pre>
	 *
	 * @param currentAssociations
	 *            The current associations between the strong entity and the weak ones.
	 * @return A {@code Set<Weak>} containing all weak entities whose associations with the strong one haven't been deleted
	 */
	public static <Strong extends AbstractEntity, Weak extends AbstractEntity> Set<Weak> getUndeletedWeak(Set<? extends AbstractEntityAbstractEntityAssociation<Strong, Weak>> currentAssociations) {
		Set<Weak> undeletedWeak = Sets.newLinkedHashSet();
		for (AbstractEntityAbstractEntityAssociation<Strong, Weak> association : currentAssociations) {
			if (!association.getDeleted())
				undeletedWeak.add(association.getWeak());
		}
		return undeletedWeak;
	}

}
