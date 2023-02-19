package eu.utilo.authorization.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Helper Klasse mit statischen Methoden
 */
@Slf4j
public class CommonUtils {

    public static Date instantToDate(Instant instant) {
        return Optional.ofNullable(instant)
                .map(item -> Date.from(instant))
                .orElse(null);
    }

    public static Instant dateToInstant(Date date) {
        return Optional.ofNullable(date)
                .map(item -> date.toInstant())
                .orElse(null);
    }

    public static Optional<String> getDatabaseType(JdbcTemplate jdbcTemplate) {

        DataSource dataSource = jdbcTemplate.getDataSource();
        Assert.isTrue(null != dataSource, "Datasource Fehler!");

        String driver;
        try {
            driver = dataSource.getConnection().getMetaData().getDriverName().toUpperCase();
            return Optional.of("MYSQL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.error("Error AuthorizationRepository ClientRepository AuthorizationConsentRepository");
        return Optional.empty();

    }

}
