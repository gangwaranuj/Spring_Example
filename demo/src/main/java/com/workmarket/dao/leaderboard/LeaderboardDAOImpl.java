package com.workmarket.dao.leaderboard;

import com.workmarket.domains.api.v2.leaderboard.models.Leaderboard;
import com.workmarket.domains.api.v2.leaderboard.models.LeaderboardUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 5/23/16.
 */
@Repository
public class LeaderboardDAOImpl implements LeaderboardDAO {
    @Autowired
    @Resource(name = "readOnlyJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<LeaderboardUser> getAllUsersForLeaderboard(final Leaderboard leaderboard) {
        //query uses group_concat and needs to be set to a long max
        jdbcTemplate.execute("SET SESSION group_concat_max_len = 10000000000000000000;", new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps)
                    throws SQLException, DataAccessException {
                return ps.executeUpdate();
            }
        });

        String sql = "select p.user_id, a.city, s.name as state, temp3.revenue, temp3.count_work, skillsTemp.skills, industryTemp.industries, specialtyTemp.specialties, warpTemp.warp_requisition_id "
                + "FROM profile p "
                + "LEFT JOIN address a on a.id = p.address_id "
                + "LEFT JOIN state s on s.id = a.state "
                + "LEFT JOIN ( "
                + " select u.id, u.warp_requisition_id "
                + " from user u "
                + " where u.warp_requisition_id is not null "
                + " ) as warpTemp on warpTemp.id = p.user_id "
                + "LEFT JOIN ( "
                + "	select usa.user_id, GROUP_CONCAT(distinct s.name) as skills "
                + "	FROM user_skill_association usa  "
                + "	JOIN skill s on s.id = usa.skill_id "
                + "	Group by usa.user_id "
                + ") as skillsTemp on skillsTemp.user_id = p.user_id "
                + "LEFT JOIN ( "
                + "	select pia.profile_id, GROUP_CONCAT(distinct i.name) as industries "
                + "	FROM profile_industry_association pia "
                + "	JOIN industry i on i.id = pia.industry_id "
                + "	group by pia.profile_id "
                + ") as industryTemp on industryTemp.profile_id = p.id "
                + "LEFT JOIN ( "
                + "	select usa.user_id, GROUP_CONCAT(distinct s.name) as specialties "
                + "	FROM user_specialty_association usa  "
                + "	JOIN specialty s where s.id = usa.specialty_id "
                + "	group by usa.user_id "
                + ") as specialtyTemp on specialtyTemp.user_id = p.user_id "
                + "LEFT JOIN ( "
                + "select paid_user_id, sum(revenue) as revenue, sum(count_work) as count_work "
                + "from ( "
                + "select whs.active_resource_user_id as paid_user_id, sum(IF(work_status_type_code = 'paid', buyer_fee, 0)) 'revenue', sum(IF(work_status_type_code = 'paid', 1, 0)) 'count_work'  "
                + "from work_history_summary whs  "
                + "where whs.active_resource_user_id is not null and whs.buyer_fee > 0 "
                + "AND modified_on between DATE_SUB(curdate(), INTERVAL 1 QUARTER) and curdate() "
                + "GROUP by whs.active_resource_user_id "
                + "UNION "
                + "select paid_user_id, sum(IF(work_status_type_code = 'paid', IF(percent < .02, work_price*.07, work_price*percent), 0)) 'revenue', sum(IF(work_status_type_code = 'paid', 1, 0)) 'count_work' "
                + "FROM( "
                + "select whs.active_resource_user_id as paid_user_id, work_price, work_status_type_code, ( "
                + "SELECT IF(minimum <> 0, (sc.number_of_periods*spt.payment_amount)/minimum, (sc.number_of_periods*spt.payment_amount)/maximum)  "
                + "FROM  "
                + "user u  "
                + "JOIN company c on c.id = u.company_id  "
                + "JOIN subscription_configuration sc on sc.company_id = c.id "
                + "JOIN subscription_fee_configuration sfc on sfc.subscription_configuration_id = sc.id "
                + "JOIN subscription_payment_tier spt on spt.subscription_fee_configuration_id = sfc.id "
                + "where u.id = buyer_user_id and sfc.active = 1   and spt.subscription_payment_tier_sw_status_type_code = 'active' "
                + "order by spt.id desc "
                + "LIMIT 1 "
                + ") as percent "
                + "from work_history_summary whs  "
                + "where whs.active_resource_user_id is not null "
                + "AND modified_on between DATE_SUB(curdate(), INTERVAL 1 QUARTER) and curdate() "
                + "and account_pricing_type_code = 'subscription' "
                + "GROUP by whs.active_resource_user_id  "
                + ") as temp "
                + "GROUP by paid_user_id "
                + ") as temp2 "
                + "where count_work > 0 "
                + "GROUP by paid_user_id "
                + ") as temp3 on temp3.paid_user_id = p.user_id";

        return jdbcTemplate.query(sql,  new ResultSetExtractor<List<LeaderboardUser>>() {
            @Override
            public List<LeaderboardUser> extractData(ResultSet rs) throws SQLException, DataAccessException {
                //wait for other data to load before creating User objects
                if (!leaderboard.waitForDependencies())
                    return null;

                List<LeaderboardUser> results = new ArrayList<LeaderboardUser>();
                while (rs.next()) {
                    results.add(
                            new LeaderboardUser(rs.getInt("user_id"),
                                    rs.getString("city"),
                                    rs.getString("state"),
                                    rs.getDouble("revenue"),
                                    rs.getInt("count_work"),
                                    rs.getString("skills"),
                                    rs.getString("specialties"),
                                    rs.getString("industries"),
                                    rs.getInt("warp_requisition_id"))
                    );
                }

                return results;
            }
        });
    }
}
