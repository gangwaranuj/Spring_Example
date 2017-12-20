package com.workmarket.service.infra.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.protobuf.format.JsonFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.protobuf.ProtocolStringList;
import com.workmarket.business.gen.KafkaMessages.UpdateOrgUnitsMembershipsMessage;
import com.workmarket.business.gen.KafkaMessages.UpdateOrgUnitMembership;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.search.group.indexer.service.GroupIndexer;
import com.workmarket.service.business.event.search.kafka.IndexRequestTopic;
import com.workmarket.service.business.event.search.kafka.Indexable;
import com.workmarket.service.infra.kafka.config.KafkaConsumerConfig;
import com.workmarket.service.infra.kafka.config.TopicConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Component
public class KafkaConsumerListener implements Runnable {
	protected static final int MINIMUM_AUTO_COMMIT_INTERVAL_MS = 1000;
	protected static final int MAXIMUM_AUTO_COMMIT_INTERVAL_MS = 60000;
	private static final Log logger = LogFactory.getLog(KafkaConsumerListener.class);
	private static final TypeReference<KafkaData<String>> KAFKA_DATA_STRING_TYPE_REFERENCE = new TypeReference<KafkaData<String>>() {};
	private static final TypeReference<List<Indexable>> LIST_INDEXABLE_TYPE_REFERENCE = new TypeReference<List<Indexable>>() {};
	private static final JsonFormat JSON_FORMAT = new JsonFormat();
	private static final int POLL_TIMEOUT = 100;
	private static final String UUID_KEY = "uuid";
	private static final ObjectMapper MAPPER = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
	@Autowired
	private UserIndexer userIndexer;
	@Autowired
	private WorkIndexer workIndexer;
	@Autowired
	private GroupIndexer groupIndexer;
	@Autowired
	private KafkaConsumerForIndexingConfiguration kafkaConsumerForIndexingConfiguration;

	public void run() {
		KafkaConsumer<String, String> kafkaConsumer = null;
		try {
			final KafkaConsumerConfig kafkaConsumerConfig = kafkaConsumerForIndexingConfiguration.getKafkaConsumerConfig();
			final Properties properties = generateProperties(kafkaConsumerConfig);
			kafkaConsumer = buildConsumerAndSubscribe(kafkaConsumerConfig, properties);
			while (true) {
				final ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(POLL_TIMEOUT);
				for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
					try {
						processKafkaMessage(consumerRecord);
					} catch (Exception ex) {
						logger.error("Error processing kafka message.", ex);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Something went wrong while polling kafka!", ex);
		} finally {
			if (kafkaConsumer != null) {
				kafkaConsumer.close();
			}
		}
	}

	protected void processKafkaMessage(final ConsumerRecord<String, String> consumerRecord)
			throws IOException, NumberFormatException {
		if (IndexRequestTopic.ORG_MEMBERSHIP_UPDATE.getIndexRequestTopic().equals(consumerRecord.topic())) {
			processUpdateOrgUnitsMembershipsMessage(consumerRecord);
		} else {
			processKafkaMessageDefault(consumerRecord);
		}
	}

	private void processKafkaMessageDefault(final ConsumerRecord<String, String> consumerRecord)
			throws IOException, NumberFormatException {
		KafkaData<String> data;
		try {
			data = MAPPER.readValue(consumerRecord.value(), KAFKA_DATA_STRING_TYPE_REFERENCE);
		} catch (IOException ex) {
			throw new IOException("Could not distill json.", ex);
		}

		final String indexTopic = consumerRecord.topic();
		if (data == null || indexTopic == null) {
			logger.error("Kafka data and topic are required.");
			return;
		}

		List<Indexable> indexables;
		try {
			indexables = MAPPER.readValue(data.getData(), LIST_INDEXABLE_TYPE_REFERENCE);
		} catch (IOException ex) {
			throw new IOException("Could not distill json.", ex);
		}

		for (final Indexable indexable : indexables) {
			final List<String> uuidsToIndex = (List<String>) indexable.getFieldValue(UUID_KEY);

			switch (IndexRequestTopic.getIndexRequestTopic(indexTopic)) {
				case INDEX_USER_REQUEST:
					userIndexer.reindexByUUID(uuidsToIndex);
					break;
				case INDEX_WORK_REQUEST:
					workIndexer.reindexByUUID(uuidsToIndex);
					break;
				case INDEX_GROUP_REQUEST:
					groupIndexer.reindexByUUID(uuidsToIndex);
					break;
				default:
					logger.error("Unknown core for indexing: " + indexTopic);
			}
		}
	}

	private void processUpdateOrgUnitsMembershipsMessage(final ConsumerRecord<String, String> consumerRecord) throws IOException {
		KafkaData<String> data;
		try {
			data = MAPPER.readValue(consumerRecord.value(), KAFKA_DATA_STRING_TYPE_REFERENCE);
		} catch (IOException ex) {
			throw new IOException("Could not distill json.", ex);
		}

		if (data == null) {
			logger.error("Kafka data and topic are required.");
			return;
		}

		final UpdateOrgUnitsMembershipsMessage.Builder updateOrgUnitsMembershipsMessageBuilder = UpdateOrgUnitsMembershipsMessage.newBuilder();
		final InputStream dataInputStream = IOUtils.toInputStream(data.getData(), Charset.defaultCharset());
		try {
			// Using JsonFormat because Jackson ObjectMapper doesn't play nicely with Google Proto objects
			JSON_FORMAT.merge(dataInputStream, updateOrgUnitsMembershipsMessageBuilder);
		} catch (IOException ex) {
			throw new IOException("Could not distill json.", ex);
		}

		final UpdateOrgUnitsMembershipsMessage updateOrgUnitsMembershipsMessage = updateOrgUnitsMembershipsMessageBuilder.build();

		final Set<String> userUuids = Sets.newHashSet();
		if (CollectionUtils.isNotEmpty(updateOrgUnitsMembershipsMessage.getOrgUnitsList())) {
			for (final UpdateOrgUnitMembership membership : updateOrgUnitsMembershipsMessage.getOrgUnitsList()) {
				final ProtocolStringList assignedUsers = membership.getAssignedUsersList();
				if (CollectionUtils.isNotEmpty(assignedUsers)) {
					userUuids.addAll(assignedUsers);
				}
				final ProtocolStringList unassignedUsers = membership.getUnassignedUsersList();
				if (CollectionUtils.isNotEmpty(unassignedUsers)) {
					userUuids.addAll(unassignedUsers);
				}
			}
		}

		if (userUuids.isEmpty()) {
			logger.warn("Kafka message had no users to assign or unassign.");
			return;
		}
		userIndexer.reindexByUUID(userUuids);
	}

	private KafkaConsumer<String, String> buildConsumerAndSubscribe(final KafkaConsumerConfig kafkaConsumerConfig, final Properties properties) {
		final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
		final List<String> topics = extract(kafkaConsumerConfig.getTopics(), on(TopicConfig.class).getName());
		consumer.subscribe(topics);

		return consumer;
	}

	protected Properties generateProperties(
			final KafkaConsumerConfig kafkaConsumerConfig) throws IllegalArgumentException {

		Preconditions.checkArgument(kafkaConsumerConfig != null,
				"KafkaConsumerConfig must not be null.");

		final String bootstrap = kafkaConsumerConfig.getBootstrapServers();
		final String groupId = kafkaConsumerConfig.getConsumerGroup();
		final Integer autoCommitInterval = kafkaConsumerConfig.getAutoCommitIntervalMs();
		final List<TopicConfig> topics = kafkaConsumerConfig.getTopics();

		Preconditions.checkArgument(groupId != null && !groupId.trim().isEmpty(),
				"Group ID must not be blank.");
		Preconditions.checkArgument(autoCommitInterval != null &&
						autoCommitInterval >= MINIMUM_AUTO_COMMIT_INTERVAL_MS &&
						autoCommitInterval <= MAXIMUM_AUTO_COMMIT_INTERVAL_MS,
				"Auto commit interval must be at least 1000ms and no greater than 60000. You specified",
				autoCommitInterval);
		Preconditions.checkArgument(bootstrap != null && !bootstrap.trim().isEmpty(),
				"Bootstrap server list must not be blank. You should have at least two servers for bootstrapping.");
		Preconditions.checkArgument(topics != null && !topics.isEmpty(), "You need to specify at least one topic.");

		final Properties properties = new Properties();
		properties.put("bootstrap.servers", bootstrap);
		properties.put("group.id", groupId);
		properties.put("auto.commit.interval.ms", autoCommitInterval);
		properties.put("enable.auto.commit", kafkaConsumerConfig.isAutoCommit());
		properties.put("key.deserializer", kafkaConsumerConfig.getKeyDeserializer());
		properties.put("value.deserializer", kafkaConsumerConfig.getValueDeserializer());
		properties.put("session.timeout.ms", kafkaConsumerConfig.getSessionTimeoutMs());

		return properties;
	}
}
