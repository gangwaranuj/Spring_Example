package com.workmarket.domains.work.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.thrift.core.Name;
import com.workmarket.thrift.core.Note;
import com.workmarket.thrift.core.User;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.utility.sql.SQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class ResourceNoteDAOImpl implements ResourceNoteDAO {

	public static final SQLBuilder resourceNoteByWorkIdSQL = addJoinsToNoteDataSQLBuilder(createNoteDataSQLBuilder(), true);
	public static final SQLBuilder notesByWorkIdSQL = addJoinsToNoteDataSQLBuilder(createNoteDataSQLBuilder(), true)
		.addWhereClause("wrcl.work_resource_action_code_id in (1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 14, 15, 16, 17, 18, 19)");
	private static final SQLBuilder resourceNoteByResourceIdSQL = addJoinsToNoteDataSQLBuilder(createNoteDataSQLBuilder(), false)
		.addWhereClause("wrcl.work_resource_id in (:workResourceIds)");

	@Resource(name = "resourceNoteJdbcTemplate")
	private NamedParameterJdbcTemplate jdbcTemplate;

	public static SQLBuilder createNoteDataSQLBuilder() {
		return new SQLBuilder()
			.addColumn("note.id noteId")
			.addColumn("wrcl.work_resource_id work_resource_id")
			.addColumn("wrcl.work_resource_action_code_id work_resource_action_code_id")
			.addColumn("mu.first_name")
			.addColumn("mu.last_name")
			.addColumn("mu.user_number")
			.addColumn("mu.id")
			.addColumn("IF(mu.company_id = 1, 1, 0) AS masq_is_wm_employee")
			.addColumn("obou.first_name")
			.addColumn("obou.last_name")
			.addColumn("obou.user_number")
			.addColumn("obou.id")
			.addColumn("IF(obou.company_id = 1, 1, 0) AS obou_is_wm_employee")
			.addColumn("COALESCE(note.note_content, wrcl.change_log_note) change_log_note")
			.addColumn("wrcl.created_on")
			.addColumn("wru.first_name")
			.addColumn("wru.last_name")
			.addColumn("wru.user_number")
			.addColumn("wru.id userId")
			.addTable("wm_marketcore.work_resource_change_log wrcl");
	}

	public static SQLBuilder addJoinsToNoteDataSQLBuilder(SQLBuilder builder, boolean addWorkId) {
		if (addWorkId) {
			builder.addJoin("inner join work_resource wr on wr.id = wrcl.work_resource_id AND wr.work_id = :workId");
		} else {
			builder.addJoin("inner join work_resource wr on wrcl.work_resource_id = wr.id");
		}
		return builder.addJoin("inner join user wru on wr.user_id = wru.id")
			.addJoin("left join user mu on mu.id = wrcl.masquerade_user_id")
			.addJoin("left join user obou on obou.id = wrcl.on_behalf_of_user_id")
			.addJoin("left join work_resource_change_log_to_note_xref wrcnote on wrcnote.work_resource_change_log_id = wrcl.id ")
			.addJoin("left join note on note.id = wrcnote.note_id ");
	}

	@Override
	public Map<Long, List<ResourceNote>> getResourceNotesByResourceIds(
		Collection<Long> resourceIdsFromPage) {
		if (CollectionUtils.isEmpty(resourceIdsFromPage)) {
			return emptyMap();
		}
		Map<String, Object> resourceHoverParameters = Maps.newHashMapWithExpectedSize(1);
		resourceHoverParameters.put("workResourceIds", resourceIdsFromPage);
		final Collection<ResourceNote> resouceHoverNotes = jdbcTemplate.query(resourceNoteByResourceIdSQL.build(), resourceHoverParameters, new ResourceNoteMapper());
		Map<Long, List<ResourceNote>> resourceHoverNotesByResourceId = Maps.newHashMap();
		for (ResourceNote note : resouceHoverNotes) {
			List<ResourceNote> resourceNotes = resourceHoverNotesByResourceId.get(note.getResourceId());
			if (resourceNotes == null) {
				resourceNotes = Lists.newLinkedList();
			}
			resourceNotes.add(note);
			resourceHoverNotesByResourceId.put(note.getResourceId(), resourceNotes);
		}
		return resourceHoverNotesByResourceId;
	}

	@Override
	public Map<Long, List<ResourceNote>> getResourceNotesByWorkId(Long id) {
		Map<String, Object> resourceHoverParameters = Maps.newHashMapWithExpectedSize(1);
		resourceHoverParameters.put("workId", id);
		final Collection<ResourceNote> resouceHoverNotes = jdbcTemplate.query(resourceNoteByWorkIdSQL.build(), resourceHoverParameters, new ResourceNoteMapper());
		Map<Long, List<ResourceNote>> resourceHoverNotesByResourceId = Maps.newHashMap();
		for (ResourceNote note : resouceHoverNotes) {
			List<ResourceNote> resourceNotes = resourceHoverNotesByResourceId.get(note.getResourceId());
			if (resourceNotes == null) {
				resourceNotes = Lists.newLinkedList();
			}
			resourceNotes.add(note);
			resourceHoverNotesByResourceId.put(note.getResourceId(), resourceNotes);
		}
		return resourceHoverNotesByResourceId;
	}

	@Override
	public List<Note> getResourceNotesForWorkByWorkId(Long id) {
		Map<String, Object> resourceHoverParameters = Maps.newHashMapWithExpectedSize(1);
		resourceHoverParameters.put("workId", id);
		return jdbcTemplate.query(notesByWorkIdSQL.build(), resourceHoverParameters, new ResourceWorkNoteMapper());
	}

	public static class ResourceWorkNoteMapper implements RowMapper<Note> {

		@Override
		public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
			Note note = new Note();
			note.setId(rs.getLong("noteId"));
			note.setIsPrivate(false);

			Timestamp createdOn = rs.getTimestamp("wrcl.created_on");
			note.setCreatedOn(createdOn.getTime());

			User resourceUser = new User();
			resourceUser.setName(new Name().setFirstName(rs.getString("wru.first_name")).setLastName(rs.getString("wru.last_name")));
			resourceUser.setId(rs.getLong("userId"));
			resourceUser.setUserNumber(rs.getString("wru.user_number"));
			note.setCreator(resourceUser);


			String behalfOfUserFirstName = rs.getString("obou.first_name");
			if (behalfOfUserFirstName != null) {
				String behalfOfUserLastName = rs.getString("obou.last_name");
				User onBehalfOfUser = new User();
				onBehalfOfUser.setName(new Name().setFirstName(behalfOfUserFirstName).setLastName(behalfOfUserLastName));
				onBehalfOfUser.setId(rs.getLong("obou.id"));
				onBehalfOfUser.setUserNumber(rs.getString("obou.user_number"));
				boolean isWMEmployee = rs.getBoolean("obou_is_wm_employee");
				onBehalfOfUser.setIsWorkMarketEmployee(isWMEmployee);
				note.setOnBehalfOf(onBehalfOfUser);
			}
			String text = rs.getString("change_log_note");
			if (isNotBlank(text)) {
				note.setText(text);
			}
			short actionCodeId = rs.getShort("work_resource_action_code_id");
			if (!rs.wasNull()) {
				note.setActionCodeId(actionCodeId);
			}
			return note;
		}

	}

	public static class ResourceNoteMapper implements RowMapper<ResourceNote> {

		@Override
		public ResourceNote mapRow(ResultSet rs, int rowNum)
			throws SQLException {
			ResourceNote hoverNote = new ResourceNote();
			hoverNote.setResourceId(rs.getLong("work_resource_id"));
			String behalfOfUserFirstName = rs.getString("obou.first_name");
			if (behalfOfUserFirstName != null) {
				String behalfOfUserLastName = rs.getString("obou.last_name");
				hoverNote.setOnBehalfOfUserName(behalfOfUserFirstName + " " + behalfOfUserLastName);
				User onBehalfOfUser = new User();
				onBehalfOfUser.setName(new Name().setFirstName(behalfOfUserFirstName).setLastName(behalfOfUserLastName));
				onBehalfOfUser.setId(rs.getLong("obou.id"));
				onBehalfOfUser.setUserNumber(rs.getString("obou.user_number"));
				boolean isWMEmployee = rs.getBoolean("obou_is_wm_employee");
				onBehalfOfUser.setIsWorkMarketEmployee(isWMEmployee);
				hoverNote.setOnBehalfOfUser(onBehalfOfUser);
			}
			String masqUserFirstName = rs.getString("mu.first_name");
			if (masqUserFirstName != null) {
				String masqUserLastName = rs.getString("mu.last_name");
				hoverNote.setMasqueradeUserName(masqUserFirstName + " " + masqUserLastName);
			}
			User resourceUser = new User();
			resourceUser.setName(new Name().setFirstName(rs.getString("wru.first_name")).setLastName(rs.getString("wru.last_name")));
			resourceUser.setId(rs.getLong("userId"));
			resourceUser.setUserNumber(rs.getString("wru.user_number"));
			hoverNote.setResourceUser(resourceUser);

			Long actionCodeId = rs.getLong("work_resource_action_code_id");
			hoverNote.setActionCodeId(actionCodeId);
			String note = rs.getString("change_log_note");
			if (!StringUtils.isEmpty(note)) {
				hoverNote.setNote(note);
			}
			Timestamp createdOn = rs.getTimestamp("wrcl.created_on");
			hoverNote.setDateOfNote(createdOn.getTime());
			return hoverNote;

		}

	}


}
