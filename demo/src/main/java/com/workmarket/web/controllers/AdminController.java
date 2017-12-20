package com.workmarket.web.controllers;

import com.google.api.client.util.Maps;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

	private static final Log logger = LogFactory.getLog(AdminController.class);

	@Autowired private Flyway flyway;
	private final static int NUMBER_OF_MIGRATIONS_TO_REPORT = 5;
	private final static String GIT_PROPERTIES = "git.properties";

	@RequestMapping(method=RequestMethod.GET)
	public String index() {
		return "web/pages/admin/index";
	}

	@RequestMapping(value={"/buildinfo"}, method=RequestMethod.GET)
	public String git(Model model) {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties(GIT_PROPERTIES);
			model.addAttribute("git", properties);
		} catch (IOException e) {
			logger.error("Failed to load git.properties", e);
		}
		return "web/pages/admin/buildinfo/index";
	}

	@RequestMapping(value={"/buildinfo/migrationinfo"}, produces = MediaType.APPLICATION_JSON_VALUE, method=RequestMethod.GET)
	public @ResponseBody AjaxResponseBuilder migrations() {
		AjaxResponseBuilder response = new AjaxResponseBuilder();
		Map <String, Object> migrations = Maps.newHashMap();
		MigrationInfo [] migrationInfos;

		try {
			migrationInfos = flyway.info().applied();
		} catch (FlywayException e) {
			logger.error(e.getMessage(), e);
			return response
					.setSuccessful(false);
		}

		int totalNumberOfMigrations =  migrationInfos.length;
		int numberOfMigrationsToReport = Math.min(totalNumberOfMigrations, NUMBER_OF_MIGRATIONS_TO_REPORT);

		for (int ix = totalNumberOfMigrations-1; ix >= totalNumberOfMigrations-numberOfMigrationsToReport; ix--) {
			migrations.put(Integer.toString(ix), buildJSONMigrationInfo(migrationInfos[ix]));
		}

		return response
				.setSuccessful(true)
				.setData(migrations);
	}

	@RequestMapping(value="/COI-insurance", method=RequestMethod.GET)
	public String insurance() {

		return "web/pages/admin/COI-insurance";
	}

	private Map<String, String> buildJSONMigrationInfo(MigrationInfo migrationInfo) {
		return CollectionUtilities.newStringMap(
				"script", migrationInfo.getScript(),
				"state", migrationInfo.getState().name(),
				"installedOn", migrationInfo.getInstalledOn().toString(),
				"executionTime", migrationInfo.getExecutionTime().toString());
	}

	@RequestMapping(value={"/styleguide"}, method=RequestMethod.GET)
	public String styleguide() {
		return "web/pages/admin/styleguide/index";
	}

}
