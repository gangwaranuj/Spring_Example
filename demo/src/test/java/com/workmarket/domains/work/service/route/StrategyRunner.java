package com.workmarket.domains.work.service.route;

import com.Ostermiller.util.CSVParser;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.workmarket.dao.qualification.UserToQualificationDAOImpl;
import com.workmarket.dao.qualification.WorkToQualificationDAOImpl;
import com.workmarket.data.solr.model.SolrUserType;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.ImmutableExplain;
import com.workmarket.domains.work.model.route.Recommendation;
import com.workmarket.domains.work.model.route.RecommendedResource;
import com.workmarket.id.IdGenerator;
import com.workmarket.search.qualification.QualificationClient;
import com.workmarket.business.recommendation.SkillRecommenderClient;
import com.workmarket.service.business.SkillService;
import com.workmarket.service.business.SkillServiceImpl;
import com.workmarket.service.business.SpecialtyService;
import com.workmarket.service.business.SpecialtyServiceImpl;
import com.workmarket.service.business.qualification.QualificationAssociationServiceImpl;
import com.workmarket.service.business.qualification.QualificationRecommender;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.service.infra.business.SuggestionServiceImpl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StrategyRunner {

    private static Logger logger = LoggerFactory.getLogger(LikeGroupsUserRecommender.class);

    private static final String BASE_DIR = "./strategy_test/";
    /**
     * qualification_association_tables.sql script should contain:
     * CREATE TABLE work_to_qualification (
     *   work_id               INT NOT NULL,
     *   qualification_uuid    VARCHAR(36) NOT NULL,
     *   qualification_type_id SMALLINT,
     *   deleted               TINYINT NOT NULL DEFAULT 0,
     *   creator_id            INT NOT NULL,
     *   created_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
     *   modifier_id           INT NOT NULL,
     *   modified_on           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
     *   PRIMARY KEY (work_id, qualification_uuid)
     * );
     */
    private static final String SQL_SCRIPT = "qualification_association_tables.sql";
    private static final String ROUTING_CONTROL = "routing_control.csv";
    private static final String USER_SOLR_ROOT_URL = "http://solr-everly.workmarket.com:8080/";
    private static final String WORK_SOLR_ROOT_URL = "http://solr-everly.workmarket.com:8080/";

    public static void main(String[] args) {
        String strategy = "likeGroups";
        if (args.length  > 0) {
            strategy = args[0];
        }
        try {
            Map<Long, StrategyWork> controlData = loadControl(BASE_DIR + ROUTING_CONTROL);

            BinaryResponseParser responseParser = new BinaryResponseParser();

            HttpSolrServer userServer = new HttpSolrServer(USER_SOLR_ROOT_URL + "usercore", null, responseParser);
            HttpSolrServer workServer = new HttpSolrServer(WORK_SOLR_ROOT_URL + "workcore", null, responseParser);

            MetricRegistry metricRegistry = new MetricRegistry();
            IdGenerator idGenerator = new IdGenerator(metricRegistry);
            EmbeddedDatabase db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("file:" + BASE_DIR + SQL_SCRIPT)
                .build();
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(db);
            UserToQualificationDAOImpl userToQualificationDAO = new UserToQualificationDAOImpl(jdbcTemplate, jdbcTemplate);
            WorkToQualificationDAOImpl workToQualificationDAO = new WorkToQualificationDAOImpl(jdbcTemplate, jdbcTemplate);
            QualificationAssociationServiceImpl qualificationAssociationService =
                new QualificationAssociationServiceImpl(userToQualificationDAO, workToQualificationDAO);
            QualificationClient qualificationClient = new QualificationClient();
            SkillRecommenderClient skillRecommenderClient = new SkillRecommenderClient();
            SkillService skillService = new SkillServiceImpl();
            SpecialtyService specialtyService = new SpecialtyServiceImpl();
            SuggestionService suggestionService = new SuggestionServiceImpl();
            QualificationRecommender qualificationRecommender =
                new QualificationRecommender(
                    qualificationClient, skillRecommenderClient, skillService, specialtyService, suggestionService);


            UserRecommender recommender = null;
            switch (strategy) {
                case "likeGroups":
                    recommender = new LikeGroupsUserRecommender(metricRegistry, userServer, workServer);
                    break;
                case "likeWork":
                    recommender = new LikeWorkUserRecommender(metricRegistry, userServer, workServer);
                    break;
                case "polymath":
                    recommender = new PolymathUserRecommender(
                        metricRegistry,
                        userServer,
                        workServer,
                        idGenerator,
                        qualificationAssociationService,
                        qualificationRecommender);
                    break;
                default:
                    error("Unknown recommender, quit.");
                    System.exit(1);
            }

            for (StrategyWork work : controlData.values()) {
                runTest(work, recommender);
            }

            // now with our test complete write our summary
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            FileWriter details = new FileWriter(BASE_DIR + "details-" + strategy + "-" + dateFormat.format(new Date()) + ".csv");
            details.write("id,user_id,viewed,applied,latitude,longitude,company_id,buyer_user_id,invited,recommended\n");
            FileWriter summary = new FileWriter(BASE_DIR + "summary-" + strategy + "-" + dateFormat.format(new Date()) + ".csv");
            summary.write("work_id,invites,invite_views,invite_applies,recommends,recommend_overlap,recommend_views,recommend_apply\n");
            for (StrategyWork work : controlData.values()) {
                work.writeDetails(details);
                work.writeSummary(summary);
            }

            details.close();
            summary.close();
            info("test run finished!");
            System.exit(0);
        }
        catch (Throwable t) {
            error("Failed executing our test!", t);
        }
    }

    /**
     * Logs an info message.
     * @param message The message
     */
    private static void info(final String message) {
        System.out.println("INFO: " + message);
        logger.info(message);
    }

    /**
     * Logs a debug message.
     * @param message The message
     */
    private static void debug(final String message) {
        System.out.println("DEBUG: " + message);
        logger.debug(message);
    }

    /**
     * Logs an error message.
     * @param message The message
     */
    private static void error(final String message) {
        System.out.println("ERROR: " + message);
        logger.error(message);
    }

    /**
     * Logs an error message.
     * @param message The message
     * @param t The exception condition
     */
    private static void error(final String message, Throwable t) {
        System.out.println("ERROR: " + message + ": " + t.getMessage());
        t.printStackTrace();
        logger.error(message, t);
    }

	/**
     * Run the test for the given work assignment and recommender
     * @param control     The work details we are testing
     * @param recommender The recommender we are testing
     */
    private static void runTest(final StrategyWork control, final UserRecommender recommender) {
        try {

            Work work = createWork(control);

            // find our recommended workers
            Recommendation recommendation = recommender.recommend(work, true);

            // now handle reporting
            info("Explain: " + recommendation.getExplain().toString());

            // write the explain
            writeExplain(control, recommendation.getExplain());

            info("Recommended workers for company " + work.getCompany().getId() + " work " + work.getId() +
                ", " + recommendation.getRecommendedResourceIdsByUserType(SolrUserType.WORKER));
            info("Recommended vendors for company " + work.getCompany().getId() + " work " + work.getId() +
                ", " + recommendation.getRecommendedResourceIdsByUserType(SolrUserType.VENDOR));


            // now push this in to our work
            for (final RecommendedResource user : recommendation.getRecommendedResources()) {
                control.addRecommendedResource(new StrategyWorkResource(user.getId(), user.getUserNumber(), user.getUserType()));
            }
        }
        catch (SolrServerException sse) {
            error("Failed executing our test", sse);
        }
    }

    /**
     * Writes our explain to a file.
     * @param control The work we are recommending workers for
     * @param explain The explain details
     */
    private static void writeExplain(final StrategyWork control, final ImmutableExplain explain) {
        try {
            File explainDir = new File(BASE_DIR + "explain");
            if (!explainDir.exists()) {
                explainDir.mkdirs();
            }
            String filename = BASE_DIR + "explain/" + control.getWorkId() + "_" + control.getTitle().toLowerCase().replaceAll("[^a-z0-9-]", "") + ".txt";
            FileUtils.write(new File(filename), explain.toString());
        } catch (IOException ioe) {
            // don't worry about
        }
    }

    /**
     * Creates a new work assignment
     * @param control The definition of our control work assignment
     * @return Work The new work instance
     */
    private static Work createWork(StrategyWork control) {
        Company company = new Company();
        company.setId(control.getCompanyId());
        User buyer = new User();
        buyer.setId(control.getBuyerUserId());
        Work work = new Work();
        work.setId(control.getWorkId());
        work.setCompany(company);
        work.setBuyer(buyer);
        work.setTitle(control.getTitle());
        work.setDesiredSkills(control.getDesiredSkills());

        Industry industry = new Industry();
        industry.setId(control.getIndustryId());
        work.setIndustry(industry);

        if (control.getLatitude() != null && control.getLongitude() != null) {
            Address address = new Address();
            address.setLatitude(control.getLatitude());
            address.setLongitude(control.getLongitude());
            work.setAddress(address);
        }

        return work;
    }

    /**
     * Loads our control file containing the set of reference data we will be comparing with.
     * @return Map<Long, StrategyWork> The control set
     */
    private static Map<Long, StrategyWork> loadControl(String filename) throws Exception {
        Map<Long, StrategyWork> result = new HashMap<>();

        CSVParser csvParser = new CSVParser(new FileReader(filename), ',');

        boolean header = true;
        for (String[] line : csvParser.getAllValues()) {
            if (header || line.length == 0) {
                header = false;
                continue;
            }

            long workId = Long.parseLong(line[0].trim());
            long userId = Long.parseLong(line[1].trim());
            boolean viewed = BooleanUtils.toBoolean(Integer.parseInt(line[2].trim()));
            boolean applied = BooleanUtils.toBoolean(Integer.parseInt(line[3].trim()));

            BigDecimal latitude = null;
            if (line[4].trim().length() > 0) {
                latitude = new BigDecimal(line[4].trim());
            }

            BigDecimal longitude = null;
            if (line[5].trim().length() > 0) {
                longitude = new BigDecimal(line[5].trim());
            }

            long companyId = Long.parseLong(line[6].trim());
            long buyerUserId = Long.parseLong(line[7].trim());

            String title = line[8].trim();
            String desiredSkills = line[9].trim();

            long industryId = Long.parseLong(line[10].trim());

            StrategyWork work = result.get(workId);
            if (work == null) {
                work = new StrategyWork(workId, companyId, title, desiredSkills, buyerUserId, industryId, latitude, longitude);
                result.put(workId, work);
            }
            work.addResource(new StrategyWorkResource(userId, null, SolrUserType.WORKER, viewed, applied));

        }

        return result;
    }


    /**
     * Value object representing work
     */
    private static class StrategyWork {
        private final long workId;
        private final BigDecimal latitude;
        private final BigDecimal longitude;
        private final long companyId;
        private final long buyerUserId;
        private final String title;
        private final String desiredSkills;
        private final long industryId;

        private final Map<Long, StrategyWorkResource> resources;
        private final Map<Long, StrategyWorkResource> recommendedResources;

        /**
         * Constructor.
         * @param workId The work id
         * @param companyId The company id
         * @param buyerUserId The buyer user id
         * @param industryId The industry id of the work
         * @param latitude The work location latitude
         * @param longitude The work location longitude
         */
        public StrategyWork(final long workId, final long companyId, final String title, final String desiredSkills,
                            final long buyerUserId, final long industryId, final BigDecimal latitude, final BigDecimal longitude) {
            this.workId = workId;
            this.companyId = companyId;
            this.title = title;
            this.desiredSkills = desiredSkills;
            this.buyerUserId = buyerUserId;
            this.industryId = industryId;
            this.latitude = latitude;
            this.longitude = longitude;

            resources = new HashMap<>();
            recommendedResources = new HashMap<>();

        }

        /**
         * Write the details of this strategy.
         * @param fw The writer to write to
         */
        public void writeDetails(final FileWriter fw) throws IOException {
            for (StrategyWorkResource res : resources.values()) {
                writeDetails(res, fw);
            }
            for (StrategyWorkResource res : recommendedResources.values()) {
                if (!wasInvited(res)) {
                    writeDetails(res, fw);
                }
            }
        }

        /**
         * Writes the details of a given resource.
         * @param res The resource we are writing the details of
         * @param fw The writer to write to
         * @throws IOException
         */
        private void writeDetails(final StrategyWorkResource res, final FileWriter fw) throws IOException {
            String viewed = res.isViewed() ? "1" : "0";
            String applied = res.isApplied() ? "1" : "0";
            String invited = res.isInvited() ? "1" : "0";
            String recommended = wasRecommended(res) ? "1" : "0";
            String lat = latitude == null ? "" : latitude.toString();
            String lng = longitude == null ? "" : longitude.toString();

            fw.write(workId + "," + res.getUserId() + "," + viewed + "," + applied + "," + lat + "," + lng + "," + companyId + "," + buyerUserId + "," + invited + "," + recommended + "\n");
        }

        /**
         * Determines if the given worker was recommended.
         * @param res The resource we are checking
         * @return boolean Returns true if the worker is recommended
         */
        private boolean wasRecommended(final StrategyWorkResource res) {
            return recommendedResources.containsKey(res.getUserId());
        }

        /**
         * Determines if the given worker was invited.
         * @param res The resource we are checking
         * @return boolean Returns true if the worker is recommended
         */
        private boolean wasInvited(final StrategyWorkResource res) {
            return resources.containsKey(res.getUserId());
        }

        /**
         * Gets the view count of the resources in the given list.
         * @param resList The list of resources
         * @return long The view count
         */
        private long getViewCount(final Collection<StrategyWorkResource> resList) {
            long viewCount = 0;
            for (StrategyWorkResource swr : resList) {
                if (swr.isViewed()) {
                    viewCount++;
                }
            }

            return viewCount;
        }

        /**
         * Gets the apply count of the resources in the given list.
         * @param resList The list of resources
         * @return long The apply count
         */
        private long getApplyCount(final Collection<StrategyWorkResource> resList) {
            long applyCount = 0;
            for (StrategyWorkResource swr : resList) {
                if (swr.isApplied()) {
                    applyCount++;
                }
            }

            return applyCount;
        }

        /**
         * Write the summary of the results of our strategy execution.
         * @param fw The writer to write to
         */
        public void writeSummary(final FileWriter fw) throws IOException {
            long invites = resources.size();
            long inviteViews = getViewCount(resources.values());
            long inviteApplies = getApplyCount(resources.values());

            long recommends = recommendedResources.size();
            long recommendsOverlap = 0;
            long recommendedViewsOverlap = 0;
            long recommendedAppliesOverlap = 0;

            // determine our other metrics
            for (StrategyWorkResource rswr : recommendedResources.values()) {
                StrategyWorkResource iswr = resources.get(rswr.getUserId());
                if (iswr != null) {
                    recommendsOverlap++;
                    if (iswr.isApplied()) {
                        recommendedAppliesOverlap++;
                    }
                    if (iswr.isViewed()) {
                        recommendedViewsOverlap++;
                    }
                }
            }

            fw.write(workId + "," + invites + "," + inviteViews + "," + inviteApplies + "," + recommends + "," + recommendsOverlap + "," + recommendedViewsOverlap + "," + recommendedAppliesOverlap + "\n");
        }

        /**
         * Gets the workId.
         *
         * @return long The workId
         */
        public long getWorkId() {
            return workId;
        }

        /**
         * Gets the latitude.
         *
         * @return java.math.BigDecimal The latitude
         */
        public BigDecimal getLatitude() {
            return latitude;
        }

        /**
         * Gets the longitude.
         *
         * @return java.math.BigDecimal The longitude
         */
        public BigDecimal getLongitude() {
            return longitude;
        }

        /**
         * Gets the companyId.
         *
         * @return long The companyId
         */
        public long getCompanyId() {
            return companyId;
        }

        /**
         * Gets the buyerUserId.
         *
         * @return long The buyerUserId
         */
        public long getBuyerUserId() {
            return buyerUserId;
        }

        /**
         * Gets the title.
         *
         * @return java.lang.String The title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the desiredSkills.
         *
         * @return java.lang.String The desiredSkills
         */
        public String getDesiredSkills() {
            return desiredSkills;
        }

        /**
         * Gets the industryId.
         *
         * @return long The industryId
         */
        public long getIndustryId() {
            return industryId;
        }

        /**
         * Gets the resources.
         *
         * @return java.util.List<com.workmarket.domains.work.service.route.StrategyRunner.StrategyWorkResource> The resources
         */
        public ImmutableList<StrategyWorkResource> getResources() {
            return ImmutableList.copyOf(resources.values());
        }

        /**
         * Gets the recommended resources.
         *
         * @return java.util.List<com.workmarket.domains.work.service.route.StrategyRunner.StrategyWorkResource> The resources
         */
        public ImmutableList<StrategyWorkResource> getRecommendedResources() {
            return ImmutableList.copyOf(recommendedResources.values());
        }

        /**
         * Add a new resource to our work.
         * @param resource The resource we are adding
         * @return StrategyWork The updated instance
         */
        public StrategyWork addResource(StrategyWorkResource resource) {
            resources.put(resource.getUserId(), resource);
            return this;
        }

        /**
         * Add a new recommended resource to our work.
         * @param resource The resource we are adding
         * @return StrategyWork The updated instance
         */
        public StrategyWork addRecommendedResource(StrategyWorkResource resource) {
            recommendedResources.put(resource.getUserId(), resource);
            return this;
        }
    }

    /**
     * Value object holding a resource assigned to work
     */
    private static class StrategyWorkResource {
        private final long userId;
        private final String userNumber;
        private final SolrUserType userType;
        private final boolean viewed;
        private final boolean applied;
        private final boolean invited;

        /**
         * Our control resources.
         * @param userId The user id invited
         * @param viewed The flag indicating if they viewed the assignment
         * @param applied The flag indicating if they applied for the assignment
         */
        public StrategyWorkResource(final long userId, final String userNumber, final SolrUserType userType, final boolean viewed, boolean applied) {
            this.userId = userId;
            this.userNumber = userNumber;
            this.userType = userType;
            this.viewed = viewed;
            this.applied = applied;
            this.invited = true;
        }

        /**
         * Constructor for our test.
         * @param userId The user id that is being invited by the test
         * @param userNumber the user number
         * @param userType the user type
         */
        public StrategyWorkResource(final long userId, final String userNumber, final SolrUserType userType) {
            this.userId = userId;
            this.userNumber = userNumber;
            this.userType = userType;
            this.viewed = false;
            this.applied = false;
            this.invited = false;
        }

        /**
         * Gets the userId.
         *
         * @return long The userId
         */
        public long getUserId() {
            return userId;
        }

        /**
         * Gets user number.
         *
         * @return String
         */
        public String getUserNumber() {
            return userNumber;
        }

        /**
         * Gets the user type.
         *
         * @return SolrUserType
         */
        public SolrUserType getUserType() {
            return userType;
        }

        /**
         * Gets the viewed.
         *
         * @return boolean The viewed
         */
        public boolean isViewed() {
            return viewed;
        }

        /**
         * Gets the applied.
         *
         * @return boolean The applied
         */
        public boolean isApplied() {
            return applied;
        }

        /**
         * Gets the invited.
         *
         * @return boolean The invited
         */
        public boolean isInvited() {
            return invited;
        }
    }
}
