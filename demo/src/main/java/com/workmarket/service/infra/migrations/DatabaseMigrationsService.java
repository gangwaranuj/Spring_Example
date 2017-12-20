package com.workmarket.service.infra.migrations;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import com.workmarket.service.infra.EnvironmentDetectionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service("databaseMigrationService")
public class DatabaseMigrationsService {

	private static final Log logger = LogFactory.getLog(DatabaseMigrationsService.class);

	@Value("${allow.automatic.migrations}")
	private boolean allowAutomaticMigrations;

	@Autowired private Flyway flyway;
	@Autowired private EnvironmentDetectionService environmentDetectionService;

	@PostConstruct
	public void runMigrations() throws FlywayException {
		boolean isLocalOrDevEnvironment = environmentDetectionService.isLocalOrDevEnvironment();

		if (isLocalOrDevEnvironment && allowAutomaticMigrations) {
			logger.info("Running migrations ...");
			flyway.migrate();
			logger.info("Running migrations OK.");
		}
	}
}