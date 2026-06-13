package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.model.AnalyticsEventInsertRow;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

@Repository
public class AnalyticsEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int[] insertEventsIgnoreDuplicates(List<AnalyticsEventInsertRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return new int[0];
        }

        String sql = """
                INSERT IGNORE INTO gvsdb.user_analytics_event_log
                (
                    unique_id,
                    event_uuid,
                    session_id,
                    app_id,
                    package_name,
                    app_version,
                    country_code,
                    language,
                    timezone,
                    device_os,
                    device_model,
                    os_version,
                    event_name,
                    event_category,
                    event_count,
                    event_value,
                    screen_name,
                    screen_class,
                    source_screen,
                    content_type,
                    item_id,
                    item_name,
                    item_category,
                    item_category_id,
                    item_list_id,
                    item_list_name,
                    search_term,
                    campaign_id,
                    campaign_name,
                    notification_type,
                    ad_network,
                    ad_unit_id,
                    ad_format,
                    ad_placement,
                    event_params,
                    occurred_at
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                """;

        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AnalyticsEventInsertRow row = rows.get(i);

                setString(ps, 1, row.uniqueId());
                setString(ps, 2, row.eventUuid());
                setString(ps, 3, row.sessionId());

                setString(ps, 4, row.appId());
                setString(ps, 5, row.packageName());
                setString(ps, 6, row.appVersion());

                setString(ps, 7, row.countryCode());
                setString(ps, 8, row.language());
                setString(ps, 9, row.timezone());

                setString(ps, 10, row.deviceOs());
                setString(ps, 11, row.deviceModel());
                setString(ps, 12, row.osVersion());

                setString(ps, 13, row.eventName());
                setString(ps, 14, row.eventCategory());
                ps.setInt(15, row.eventCount());
                setBigDecimal(ps, 16, row.eventValue());

                setString(ps, 17, row.screenName());
                setString(ps, 18, row.screenClass());
                setString(ps, 19, row.sourceScreen());

                setString(ps, 20, row.contentType());
                setString(ps, 21, row.itemId());
                setString(ps, 22, row.itemName());
                setString(ps, 23, row.itemCategory());
                setString(ps, 24, row.itemCategoryId());
                setString(ps, 25, row.itemListId());
                setString(ps, 26, row.itemListName());

                setString(ps, 27, row.searchTerm());

                setString(ps, 28, row.campaignId());
                setString(ps, 29, row.campaignName());
                setString(ps, 30, row.notificationType());

                setString(ps, 31, row.adNetwork());
                setString(ps, 32, row.adUnitId());
                setString(ps, 33, row.adFormat());
                setString(ps, 34, row.adPlacement());

                setString(ps, 35, row.eventParamsJson());
                ps.setTimestamp(36, Timestamp.valueOf(row.occurredAt()));
            }

            @Override
            public int getBatchSize() {
                return rows.size();
            }
        });
    }

    private void setString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }

    private void setBigDecimal(PreparedStatement ps, int index, java.math.BigDecimal value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setBigDecimal(index, value);
        }
    }
}