package com.workmarket.service.infra.kafka;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.googlecode.protobuf.format.JsonFormat;
import com.workmarket.business.gen.KafkaMessages.UpdateOrgUnitMembership;
import com.workmarket.business.gen.KafkaMessages.UpdateOrgUnitsMembershipsMessage;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.common.kafka.KafkaUtil;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.search.group.indexer.service.GroupIndexer;
import com.workmarket.service.business.event.search.kafka.IndexRequestTopic;
import com.workmarket.service.infra.kafka.config.KafkaConsumerConfig;
import com.workmarket.service.infra.kafka.config.TopicConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KafkaConsumerListenerTest {

	private static final JsonFormat JSON_FORMAT = new JsonFormat();
	private static final String INVALID_ORG_JSON = "{\"org_units\": }";
	private static final String TEST_ORG_UNIT_UUID = "test-uuid";
	private static final String USER_A_UUID = "9876543210";
	private static final String USER_B_UUID = "1112223334";
	final String BOOTSTRAP_SERVER = "BOOTSTRAP_SERVER",
			CONSUMER_GROUP = "CONSUMER_GROUP",
			KEY_DESERIALIZER = "KEY_DESERIALIZER",
			VALUE_DESERIALIZER = "VALUE_DESERIALIZER",
			KAFKA_MESSAGE_FOR_DEFAULT_PROCESSING = "{\"data\":\"[{\\\"uuid\\\":[\\\"000f7b74-c47c-4502-a5f0-6ee6733366d4\\\"]}]\"}",
			KAFKA_MESSAGE_FOR_UPDATE_ORG_MEMBERSHIP_USER_A_ASSIGNED_USER_B_UNASSIGNED =
				"{" +
					"\"data\":" +
						"\"{" +
								"\\\"org_units\\\": [{\\\"uuid\\\": \\\""+ TEST_ORG_UNIT_UUID +"\\\", \\\"unassigned_users\\\":[\\\""+ USER_B_UUID +"\\\"],\\\"assigned_users\\\": [\\\""+ USER_A_UUID +"\\\"]}]" +
						"}\"," +
					"\"meta\":" +
						"{" +
							"\"user_id\":\"d70788d8-048b-6328-ab68-3d4452dd3193\"," +
							"\"data_access_type\":\"UPDATE\"," +
							"\"dataType\":\"org\"" +
						"}" +
				"}",
			KAFKA_MESSAGE_NO_ORG_DATA =
				"{" +
					"\"data\": \"{}\"," +
					"\"meta\":" +
						"{" +
							"\"user_id\":\"d70788d8-048b-6328-ab68-3d4452dd3193\"," +
							"\"data_access_type\":\"UPDATE\"," +
							"\"dataType\":\"org\"" +
						"}" +
				"}",
			KAFKA_MESSAGE_WITH_ORG_DATA_NO_USER_DATA =
					"{" +
						"\"data\": " +
							"\"{" +
								"\\\"org_units\\\": [{\\\"uuid\\\": \\\""+ TEST_ORG_UNIT_UUID +"\\\"}]" +
							"}\"," +
						"\"meta\":" +
							"{" +
								"\"user_id\":\"d70788d8-048b-6328-ab68-3d4452dd3193\"," +
								"\"data_access_type\":\"UPDATE\"," +
								"\"dataType\":\"org\"" +
							"}" +
					"}",
			KAFKA_MESSAGE_MALFORMED_DATA =
					"{" +
						"\"data\": \"{lkasmdlkasmdlkasmdaklsm}\"," +
						"\"meta\":" +
							"{" +
								"\"user_id\":\"d70788d8-048b-6328-ab68-3d4452dd3193\"," +
								"\"data_access_type\":\"UPDATE\"," +
								"\"dataType\":\"org\"" +
							"}" +
					"}";
	final Integer AUTO_COMMIT_INTERVAL_MS = 1001, SESSION_TIMEOUT_MS = 6000, KAFKA_PARTITION = 0;
	final boolean IS_AUTO_COMMIT = true;
	final long KAFKA_OFFSET = 1l;

	@InjectMocks
	KafkaConsumerListener kafkaConsumerListener = spy(KafkaConsumerListener.class);

	KafkaConsumerConfig kafkaConsumerConig;
	ConsumerRecord<String, String> consumerRecord;
	List<TopicConfig> topicConfigs;
	@Mock
	private UserIndexer userIndexer;
	@Mock
	private WorkIndexer workIndexer;
	@Mock
	private GroupIndexer groupIndexer;

	@Before
	public void setup() {
		topicConfigs = Lists.newArrayList(new TopicConfig("index_user"));

		kafkaConsumerConig = mock(KafkaConsumerConfig.class);
		when(kafkaConsumerConig.getBootstrapServers()).thenReturn(BOOTSTRAP_SERVER);
		when(kafkaConsumerConig.getConsumerGroup()).thenReturn(CONSUMER_GROUP);
		when(kafkaConsumerConig.getKeyDeserializer()).thenReturn(KEY_DESERIALIZER);
		when(kafkaConsumerConig.getValueDeserializer()).thenReturn(VALUE_DESERIALIZER);
		when(kafkaConsumerConig.getAutoCommitIntervalMs()).thenReturn(AUTO_COMMIT_INTERVAL_MS);
		when(kafkaConsumerConig.isAutoCommit()).thenReturn(IS_AUTO_COMMIT);
		when(kafkaConsumerConig.getSessionTimeoutMs()).thenReturn(SESSION_TIMEOUT_MS);
		when(kafkaConsumerConig.getTopics()).thenReturn(topicConfigs);

	consumerRecord = new ConsumerRecord<>(
			IndexRequestTopic.INDEX_USER_REQUEST.name(),
			KAFKA_PARTITION,
			KAFKA_OFFSET,
			null,
			KAFKA_MESSAGE_FOR_DEFAULT_PROCESSING);
	}

	@Test
	public void generateProperties_validKafkaConsumerConfig_propertiesGenerated() {
		final Properties properties = kafkaConsumerListener.generateProperties(kafkaConsumerConig);

		assertEquals(BOOTSTRAP_SERVER, properties.get("bootstrap.servers"));
		assertEquals(CONSUMER_GROUP, properties.get("group.id"));
		assertEquals(AUTO_COMMIT_INTERVAL_MS, properties.get("auto.commit.interval.ms"));
		assertEquals(IS_AUTO_COMMIT, properties.get("enable.auto.commit"));
		assertEquals(KEY_DESERIALIZER, properties.get("key.deserializer"));
		assertEquals(VALUE_DESERIALIZER, properties.get("value.deserializer"));
		assertEquals(SESSION_TIMEOUT_MS, properties.get("session.timeout.ms"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_nullKafkaConsumerConfig_illegalArgumentExceptionThrown() {
		kafkaConsumerListener.generateProperties(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_nullBootstrapServerConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getBootstrapServers()).thenReturn(null);

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_emptyStringBootstrapServerConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getBootstrapServers()).thenReturn("");

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_whitespaceBootstrapServerConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getBootstrapServers()).thenReturn("     ");

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_nullConsumerGroupConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getConsumerGroup()).thenReturn(null);

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_emptyConsumerGroupConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getConsumerGroup()).thenReturn("");

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_whitespaceConsumerGroupConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getConsumerGroup()).thenReturn("     ");

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_nullAutoCommitIntervalConfig_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getAutoCommitIntervalMs()).thenReturn(null);

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_lessThanMinimumAutoCommitIntervalMs_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getAutoCommitIntervalMs())
				.thenReturn(KafkaConsumerListener.MINIMUM_AUTO_COMMIT_INTERVAL_MS - 1);

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_greaterThanMaximumAutoCommitIntervalMs_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getAutoCommitIntervalMs())
				.thenReturn(KafkaConsumerListener.MAXIMUM_AUTO_COMMIT_INTERVAL_MS + 1);

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_topicsListIsNull_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getTopics())
				.thenReturn(null);

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void generateProperties_topicsListIsEmpty_illegalArgumentExceptionThrown() {
		when(kafkaConsumerConig.getTopics())
				.thenReturn(Lists.<TopicConfig>newArrayList());

		kafkaConsumerListener.generateProperties(kafkaConsumerConig);
	}

	@Test
	public void processKafkaMessage_indexUserRequestTopic_userIndexerCalled() throws IOException {
		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer, times(1)).reindexByUUID(anyList());
	}

	@Test
	public void processKafkaMessage_indexWorkRequestTopic_workIndexerCalled() throws IOException {
		consumerRecord = new ConsumerRecord<>(
				IndexRequestTopic.INDEX_WORK_REQUEST.name(),
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				KAFKA_MESSAGE_FOR_DEFAULT_PROCESSING);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(workIndexer, times(1)).reindexByUUID(anyList());
	}

	@Test
	public void processKafkaMessage_indexGroupRequestTopic_groupIndexerCalled() throws IOException {
		consumerRecord = new ConsumerRecord<>(
				IndexRequestTopic.INDEX_GROUP_REQUEST.name(),
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				KAFKA_MESSAGE_FOR_DEFAULT_PROCESSING);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(groupIndexer, times(1)).reindexByUUID(anyList());
	}

	@Test
	public void processKafkaMessage_unknownIndexRequestTopic_noIndexerCalled() throws IOException {
		consumerRecord = new ConsumerRecord<>(
				IndexRequestTopic.UNKNOWN.name(),
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				KAFKA_MESSAGE_FOR_DEFAULT_PROCESSING);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer, never()).reindexByUUID(anyList());
		verify(workIndexer, never()).reindexByUUID(anyList());
		verify(groupIndexer, never()).reindexByUUID(anyList());
	}

	@Test(expected = IOException.class)
	public void processKafkaMessage_badJsonFormatting_exceptionThrown() throws IOException {
		consumerRecord = new ConsumerRecord<>(
				IndexRequestTopic.INDEX_USER_REQUEST.name(),
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				"asdkjfnkasjnkr");

		kafkaConsumerListener.processKafkaMessage(consumerRecord);
	}

	@Test(expected = IllegalArgumentException.class)
	public void processKafkaMessage_noTopicSet_exceptionThrown() throws IOException {
		consumerRecord = new ConsumerRecord<>(
				null,
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				KAFKA_MESSAGE_FOR_DEFAULT_PROCESSING);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);
	}

	@Test(expected = IOException.class)
	public void processKafkaMessage_dataIsEmpty_exceptionThrown() throws IOException {
		consumerRecord = new ConsumerRecord<>(
				IndexRequestTopic.INDEX_USER_REQUEST.name(),
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				"{\"data\":\"\"}");

		kafkaConsumerListener.processKafkaMessage(consumerRecord);
	}

	@Test
	public void processKafkaMessage_fromOrg_oneAssigned_oneUnassigned() throws IOException {
		consumerRecord = withConsumerRecord(KAFKA_MESSAGE_FOR_UPDATE_ORG_MEMBERSHIP_USER_A_ASSIGNED_USER_B_UNASSIGNED);
		final String[] expectedUuids = new String[]{USER_A_UUID, USER_B_UUID};

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer).reindexByUUID((Collection<String>) Matchers.argThat(containsInAnyOrder(new String[]{USER_A_UUID, USER_B_UUID})));
	}

	@Test
	public void processKafkaMessage_fromOrg_withNoOrgUnitData() throws IOException {
		consumerRecord = withConsumerRecord(KAFKA_MESSAGE_NO_ORG_DATA);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer, never()).reindexByUUID(anyCollection());
	}

	@Test
	public void processKafkaMessage_fromOrg_withNoUserData() throws IOException {
		consumerRecord = withConsumerRecord(KAFKA_MESSAGE_WITH_ORG_DATA_NO_USER_DATA);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer, never()).reindexByUUID(anyCollection());
	}

	@Test(expected = IOException.class)
	public void processKafkaMessage_fromOrg_withError() throws IOException {
		consumerRecord = withConsumerRecord(INVALID_ORG_JSON);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer, never()).reindexByUUID(anyCollection());
	}

	@Test(expected = IOException.class)
	public void processKafkaMessage_fromOrg_malformedData() throws IOException {
		consumerRecord = withConsumerRecord(KAFKA_MESSAGE_MALFORMED_DATA);

		kafkaConsumerListener.processKafkaMessage(consumerRecord);

		verify(userIndexer, never()).reindexByUUID(anyCollection());
	}

	private ConsumerRecord<String, String> withConsumerRecord(final String message) {
		return new ConsumerRecord<>(
				IndexRequestTopic.ORG_MEMBERSHIP_UPDATE.getIndexRequestTopic(),
				KAFKA_PARTITION,
				KAFKA_OFFSET,
				null,
				message);
	}

}
