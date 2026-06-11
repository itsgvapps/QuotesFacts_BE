package com.gvapps.quotesfacts.repository;

import com.gvapps.quotesfacts.model.AnalyticsEventInsertRow;
import com.gvapps.quotesfacts.model.EventTypeLookupKey;
import com.gvapps.quotesfacts.model.EventTypeRow;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AnalyticsEventRepository {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<EventTypeLookupKey, EventTypeRow> findActiveEventTypes(Set<EventTypeLookupKey> keys) {
        if (keys == null || keys.isEmpty()) {
            return Map.of();
        }

        String tuplePlaceholders = keys.stream()
                .map(key -> "(?, ?)")
                .collect(Collectors.joining(", "));

        String sql = """
                SELECT
                    id,
                    event_group,
                    event_key
                FROM gvsdb.app_event_types
                WHERE active = 1
                  AND (event_group, event_key) IN (%s)
                """.formatted(tuplePlaceholders);

        List<Object> params = new ArrayList<>();
        for (EventTypeLookupKey key : keys) {
            params.add(key.eventGroup());
            params.add(key.eventKey());
        }

        List<EventTypeRow> rows = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new EventTypeRow(
                        rs.getInt("id"),
                        rs.getString("event_group"),
                        rs.getString("event_key")
                ),
                params.toArray()
        );

        Map<EventTypeLookupKey, EventTypeRow> result = new HashMap<>();
        for (EventTypeRow row : rows) {
            result.put(new EventTypeLookupKey(row.eventGroup(), row.eventKey()), row);
        }

        return result;
    }

    public int[] insertEventsIgnoreDuplicates(List<AnalyticsEventInsertRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return new int[0];
        }

        String sql = """
                INSERT IGNORE INTO gvsdb.user_event_log
                (
                    event_uuid,
                    unique_id,
                    session_id,
                    package_name,
                    app_version,
                    country_code,
                    language,
                    timezone,
                    device_os,
                    device_model,
                    os_version,
                    event_type_id,
                    event_group,
                    event_key,
                    event_count,
                    screen_name,
                    source_screen,
                    content_type,
                    content_id,
                    category_id,
                    category_name,
                    notification_type,
                    campaign_id,
                    ad_network,
                    ad_unit_id,
                    ad_placement,
                    metadata,
                    occurred_at
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    ?, ?, ?, ?, ?, ?
                )
                """;

        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AnalyticsEventInsertRow row = rows.get(i);

                setString(ps, 1, row.eventUuid());
                setString(ps, 2, row.uniqueId());
                setString(ps, 3, row.sessionId());

                setString(ps, 4, row.packageName());
                setString(ps, 5, row.appVersion());
                setString(ps, 6, row.countryCode());
                setString(ps, 7, row.language());
                setString(ps, 8, row.timezone());
                setString(ps, 9, row.deviceOs());
                setString(ps, 10, row.deviceModel());
                setString(ps, 11, row.osVersion());

                ps.setInt(12, row.eventTypeId());
                setString(ps, 13, row.eventGroup());
                setString(ps, 14, row.eventKey());
                ps.setInt(15, row.eventCount());

                setString(ps, 16, row.screenName());
                setString(ps, 17, row.sourceScreen());
                setString(ps, 18, row.contentType());
                setString(ps, 19, row.contentId());
                setString(ps, 20, row.categoryId());
                setString(ps, 21, row.categoryName());

                setString(ps, 22, row.notificationType());
                setString(ps, 23, row.campaignId());

                setString(ps, 24, row.adNetwork());
                setString(ps, 25, row.adUnitId());
                setString(ps, 26, row.adPlacement());

                setString(ps, 27, row.metadataJson());

                ps.setTimestamp(28, Timestamp.valueOf(row.occurredAt()));
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
}