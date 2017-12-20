package com.workmarket.domains.model.rating;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="rating")
@Table(name="rating")
@NamedQueries({
	@NamedQuery(name="rating.averageForUser", query="select count(*) / (select count(*) from rating r1 where r1.ratedUser.id = :user_id and r1.deleted = 0) from rating r2 where r2.ratedUser.id = :user_id and r2.deleted = 0 and r2.value > 1"),
	@NamedQuery(name="rating.averageForUserSinceNMonthsAgo", query="select count(*) / (select count(*) from rating r1 where r1.ratedUser.id = :user_id and r1.deleted = 0 and r1.createdOn >= :fromDate) from rating r2 where r2.ratedUser.id = :user_id and r2.deleted = 0 and r2.value > 1 and r2.createdOn >= :fromDate"),
	@NamedQuery(name="rating.averageForUserByCompany", query="select count(*) / (select count(*) from rating r1 where r1.ratedUser.id = :user_id and r1.deleted = 0 and r1.ratingCompany = :company_id) from rating r2 where r2.ratedUser.id = :user_id and r2.deleted = 0 and r2.value > 1 and r2.ratingCompany = :company_id"),
	@NamedQuery(name="rating.summaryForUser", query="select count(*) / (select count(*) from rating r1 where r1.ratedUser.id = :user_id and r1.deleted = 0) as percentage, (select count(*) from rating r2 where r2.ratedUser.id = :user_id and r2.deleted = 0) as count from rating r3 where r3.ratedUser.id = :user_id and r3.deleted = 0 and r3.value > 1"),
	@NamedQuery(name="rating.summaryForUserByCompany", query="select count(*) / (select count(*) from rating r1 where r1.ratedUser.id = :user_id and r1.deleted = 0 and r1.ratingCompany = :company_id) as percentage, (select count(*) from rating r2 where r2.ratedUser.id = :user_id and r2.deleted = 0 and r2.ratingCompany = :company_id) as count from rating r3 where r3.ratedUser.id = :user_id and r3.deleted = 0 and r3.value > 1 and r3.ratingCompany = :company_id"),
	@NamedQuery(name="rating.countAllGoodUserRatings",query="select count(r) from rating r where r.ratedUser.id = :user_id and r.deleted = 0 and r.value > 1"),
	@NamedQuery(name="rating.countAllUserRatings", query="select count(r) from rating r where r.ratedUser.id = :user_id and r.deleted = 0"),
	@NamedQuery(name="rating.forUser", query="from rating r where r.ratedUser.id = :user_id and r.deleted = 0"),
	@NamedQuery(name="rating.byUser", query="from rating r where r.ratingUser.id = :user_id and r.deleted = 0"),
	@NamedQuery(name="rating.flaggedForReview", query="from rating where flagged_for_review_flag = 'Y'")
})
@AuditChanges
public class Rating extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	public static Integer EXCELLENT = 3;
	public static Integer SATISFIED = 2;
	public static Integer UNSATISFIED = 1;

	private Integer value;
	private Integer quality;
	private Integer professionalism;
	private Integer communication;
	private String review;
	private User ratingUser;
	private Company ratingCompany;
	private User ratedUser;
	private Work work = null;
	private boolean flaggedForReview = false;
	private boolean ratingSharedFlag = true;
	private boolean reviewSharedFlag = true;
	private boolean buyerRating = false;
	private boolean pending = true;

	public Rating() {}

	public Rating(User ratingUser, User ratedUser, Integer value, String review) {
		this.ratingUser = ratingUser;
		this.ratedUser = ratedUser;
		this.value = value;
		this.review = review;
	}

	@Column(name="value", nullable=false)
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	@Column(name="quality")
	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	@Column(name="professionalism")
	public Integer getProfessionalism() {
		return professionalism;
	}

	public void setProfessionalism(Integer professionalism) {
		this.professionalism = professionalism;
	}

	@Column(name="communication")
	public Integer getCommunication() {
		return communication;
	}

	public void setCommunication(Integer communication) {
		this.communication = communication;
	}

	@Column(name="review", nullable=true)
	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rater_user_id", nullable=false)
	public User getRatingUser() {
		return ratingUser;
	}

	public void setRatingUser(User ratingUser) {
		this.ratingUser = ratingUser;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name="rater_company_id", nullable=false)
	public Company getRatingCompany() {
		return this.ratingCompany;
	}

	public void setRatingCompany(Company ratingCompany) {
		this.ratingCompany = ratingCompany;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rated_user_id", nullable=false)
	public User getRatedUser() {
		return ratedUser;
	}

	public void setRatedUser(User ratedUser) {
		this.ratedUser = ratedUser;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="work_id", referencedColumnName="id", nullable=true)
	public Work getWork() {
		return work;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	@Column(name="flagged_for_review_flag", nullable=false, length=1)
	@Type(type="yes_no")
	public boolean isFlaggedForReview() {
		return flaggedForReview;
	}

	public void setFlaggedForReview(boolean flaggedForReview) {
		this.flaggedForReview = flaggedForReview;
	}

	@Column(name="rating_shared_flag", nullable=false, length=1)
	@Type(type="yes_no")
	public boolean isRatingSharedFlag() {
		return ratingSharedFlag;
	}

	public void setRatingSharedFlag(boolean ratingSharedFlag) {
		this.ratingSharedFlag = ratingSharedFlag;
	}

	@Column(name="review_shared_flag", nullable = false, length=1)
	@Type(type="yes_no")
	public boolean isReviewSharedFlag() {
		return reviewSharedFlag;
	}

	public void setReviewSharedFlag(boolean reviewSharedFlag) {
		this.reviewSharedFlag = reviewSharedFlag;
	}

	@Column(name="is_buyer_rating", nullable = false)
	public boolean isBuyerRating() {
		return buyerRating;
	}

	public void setBuyerRating(boolean buyerRating) {
		this.buyerRating = buyerRating;
	}

	@Column(name="is_pending", nullable = false)
	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	@Transient
	public Integer getScaledValue() {
		return Math.round(value / 20);
	}
}
