package com.workmarket.dao.admin;

import com.google.common.base.Optional;
import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.asset.CurrentDefaultBackgroundImage;
import com.workmarket.domains.model.asset.DefaultBackgroundImage;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultBackgroundImageDAOImpl extends AbstractDAO<DefaultBackgroundImage> implements DefaultBackgroundImageDAO {

	@Qualifier("jdbcTemplate") @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected Class<DefaultBackgroundImage> getEntityClass() {
		return DefaultBackgroundImage.class;
	}

	@Override
	public DefaultBackgroundImage getDefaultBackgroundImage(Long id) {
		return (DefaultBackgroundImage) getFactory().getCurrentSession().createCriteria(getEntityClass())
			.add(Restrictions.eq("id", id))
			.uniqueResult();
	}

	@Override
	public Optional<DefaultBackgroundImage> getCurrentDefaultBackgroundImage() {
		CurrentDefaultBackgroundImage image =
			((CurrentDefaultBackgroundImage) getFactory().getCurrentSession().createCriteria(CurrentDefaultBackgroundImage.class).uniqueResult());

		return Optional.fromNullable(image.getDefaultBackgroundImage());
	}

	@Override
	public void setCurrentDefaultBackgroundImage(Long id) {
		final String sql =
			" REPLACE INTO current_default_background_image (default_background_image_id) VALUES (:id) ";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("id", id);
		jdbcTemplate.update(sql, params);
	}

	@Override
	public void deleteBackgroundImage(Long id) {
		getFactory().getCurrentSession()
			.createQuery("delete from defaultBackgroundImage where id = :id")
			.setLong("id", id)
			.executeUpdate();
	}

	@Override
	public DefaultBackgroundImage addBackgroundImage(DefaultBackgroundImage image) {
		saveOrUpdate(image);
		return image;
	}
}
