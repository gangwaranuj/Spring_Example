package com.workmarket.domains.search.solr;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.Maps;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.biz.kvstore.Data;
import com.workmarket.biz.kvstore.DataBuilder;
import com.workmarket.biz.kvstore.KV;
import com.workmarket.biz.kvstore.KVData;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.common.metric.WMMetricRegistryFacade;
import com.workmarket.data.solr.query.SolrMetricConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.params.SolrParams;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Subscriber;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A wrapper used to handle search tracking - this holds common functionality between our two search proxies
 * so we don't duplicate all of the tracking logic between the two!
 */
public class SolrTracking {
    private static final Logger logger = LoggerFactory.getLogger(SolrTracking.class);

    // initialize our ObjectMapper
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ss'Z'");
    
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String UPDATE_FIELD_SET = "set";
    private static final String UPDATE_FIELD_ADD = "add";
    private static final String UPDATE_FIELD_INC = "inc";
    private static final String UPDATE_FIELD_REMOVE = "remove";
    private static final String UPDATE_FIELD_REMOVEREGEX = "removeregex";
    private static final Set<String> UPDATE_FIELDS = Sets.newHashSet(UPDATE_FIELD_SET, UPDATE_FIELD_ADD, UPDATE_FIELD_INC, UPDATE_FIELD_REMOVE, UPDATE_FIELD_REMOVEREGEX);

    private static final String TOPIC = "index_";

    private static final int KAFKA_MAX_REQUEST_SIZE = 819200; // leave some space for metadata
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String KVSTORE_TOPIC = "kvstore_pojo";


    private final WMMetricRegistryFacade metricRegistryFacade;
    private final KafkaClient kafkaClient;

    private final String core;
    private final String topic;
    private final String entity;

    private final boolean publishToKafka;

    private final Meter defaultSearchMeter;
    private final Map<String, Meter> searchMeters = com.google.common.collect.Maps.newConcurrentMap();
    private final Meter indexMeterBatch;
    private final Meter updateMeterSingle;
    private final Meter updateMeterBatch;
    private final Meter deleteMeter;
    private final Meter deleteByQueryMeter;
    private final Meter kafkaIndexPublishSuccess;
    private final Meter kafkaIndexPublishFailure;
    private final Meter indexMeterSingle;
    private final Counter defaultKafkaDocCounter;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();



    public SolrTracking(final String core, final String solrProxy, final boolean publishToKafka,
                        final MetricRegistry metricRegistry, final KafkaClient kafkaClient) {

        this.core = core;
        this.entity = core.replace("core", "").toLowerCase();
        this.topic = TOPIC + this.entity;

        metricRegistryFacade = new WMMetricRegistryFacade(metricRegistry, solrProxy);
        this.kafkaClient = kafkaClient;
        this.publishToKafka = publishToKafka;

        // get our meters so we don't have to resolve them each time
        indexMeterBatch = metricRegistryFacade.meter(core + ".index.batch");
        indexMeterSingle = metricRegistryFacade.meter(core + ".index.single");
        updateMeterBatch = metricRegistryFacade.meter(core + ".update.batch");
        updateMeterSingle = metricRegistryFacade.meter(core + ".update.single");
        deleteMeter = metricRegistryFacade.meter(core + ".delete");
        deleteByQueryMeter = metricRegistryFacade.meter(core + ".deleteByQuery");
        kafkaIndexPublishSuccess = metricRegistryFacade.meter(core + ".kafka.success");
        kafkaIndexPublishFailure = metricRegistryFacade.meter(core + ".kafka.failure");
        defaultKafkaDocCounter = metricRegistryFacade.counter(core + ".kafka.doc.count");

        defaultSearchMeter = metricRegistryFacade.meter(core + ".search");
    }


    public void add(final Collection<SolrInputDocument> docs) {
        if (docs != null && docs.size() > 0 && isUpdate(docs.iterator().next())) {
            updateMeterBatch.mark();
        } else {
            indexMeterBatch.mark();
        }

        if (isPublishingToKafka()) {
            final String directedTowards = SolrThreadLocal.getDirectedTowards();
            executor.execute(new Runnable() {
                public void run() {
                    publishUpdate(Lists.<SolrInputDocument>newArrayList(docs), directedTowards);
                }
            });
        }
    }

    public void add(final SolrInputDocument doc) {
        // maybe send this message to Kafka here
        if (isUpdate(doc)) {
            updateMeterSingle.mark();
        } else {
            indexMeterSingle.mark();
        }

        if (isPublishingToKafka()) {
            final String directedTowards = SolrThreadLocal.getDirectedTowards();
            executor.execute(new Runnable() {
                public void run() {
                    publishUpdate(doc, directedTowards);
                }
            });
        }
    }

    public void deleteById(final String id) {
        deleteMeter.mark();

        if (isPublishingToKafka()) {
            executor.execute(new Runnable() {
                public void run() {
                    publishDelete(id);
                }
            });
        }
    }

    public void deleteById(final List<String> ids) {
        deleteMeter.mark();

        if (isPublishingToKafka()) {
            executor.execute(new Runnable() {
                public void run() {
                    publishDelete(ids);
                }
            });
        }
    }

    public void deleteByQuery(String query) {
        deleteByQueryMeter.mark();
    }

    public void query(SolrParams params) {
        recordQueryMetric(params);
    }

    public void queryAndStreamResponse(SolrParams params) {
        recordQueryMetric(params);
    }


    private void recordQueryMetric(final SolrParams params) {
        try {
            Meter meter = getOrCreateMeter(params.get(SolrMetricConstants.SEARCH_TYPE));
            meter.mark();
        }
        catch (Throwable t) {
            logger.error("Failed to record metric", t);
        }
    }

    private Meter getOrCreateMeter(String searchType) {
        if (StringUtils.isEmpty(searchType)) {
            return defaultSearchMeter;
        } else {
            Meter meter = searchMeters.get(searchType.toLowerCase());
            if (meter == null) {
                meter = metricRegistryFacade.meter(core + ".search." + searchType);
                searchMeters.put(searchType.toLowerCase(), meter);
            }
            return meter;
        }
    }


    /**
     * Returns a flag indicating if we want to publish this data to Kafka
     * @return boolean Returns true if we should publish, false otherwise
     */
    private boolean isPublishingToKafka() {
        return publishToKafka && (!SolrThreadLocal.isDirected() || !SolrThreadLocal.isDirectedTowards(core));
    }

    /**
     * Publish the docs we are writing to Kafka.
     * @param docs The docs we are publishing
     * @param directedTowards The target
     */
    private void publishUpdate(final List<SolrInputDocument> docs, final String directedTowards) {
        if (docs.size() == 0) {
            return;
        }
        try {
            // add some metrics around docs "seen" here - we do this for reconciling with what is
            // processed inside the service.
            if (directedTowards == null) {
                defaultKafkaDocCounter.inc(docs.size());
            } else {
                Counter directedCounter = metricRegistryFacade.counter(core + ".kafka." + directedTowards + ".doc.count");
                directedCounter.inc(docs.size());
            }

            final String operation = isUpdate(docs.get(0)) ? "update" : "insert";
            if ("ENGAGEMENT".equals(kvStoreNamespaceFromSolrEntity(entity))) {
                publishJsonDocs(docs, directedTowards, operation);
            }
            publishKVData(docs, directedTowards, operation);
        }
        catch (Throwable t) {
            logger.error("Failed to publish update to kafka!", t);
            kafkaIndexPublishFailure.mark();
        }

    }

    private void publishJsonDocs(
        final List<SolrInputDocument> docs,
        final String directedTowards,
        final String operation
    ) throws Exception {
        final ArrayNode jsonArray = JsonNodeFactory.instance.arrayNode();
        int batchSize = 0;
        for (SolrInputDocument doc : docs) {
            JsonNode jsonDoc = toJson(doc);
            String docString = toString(jsonDoc);
            if (docString == null) {
                continue;
            }
            int docSize = docString.getBytes(UTF8).length;
            if (docSize > KAFKA_MAX_REQUEST_SIZE) {
                logger.error("CORE:{} DOC:{} SIZE:{} OVER KAFKA LIMIT", directedTowards, doc.getFieldValue("id").toString(), docSize);
                continue;
            }
            batchSize += docSize;
            if (batchSize < KAFKA_MAX_REQUEST_SIZE) {
                jsonArray.add(jsonDoc);
            } else {
                String kafkaMsg = toString(jsonArray);
                if (kafkaMsg != null) {
                    logger.info("send {} docs to kafka with size {}", jsonArray.size(), batchSize);
                    publish(kafkaMsg, operation, directedTowards, topic);
                }
                jsonArray.removeAll();
                jsonArray.add(jsonDoc);
                batchSize = docSize;
            }
        }
        if (jsonArray.size() > 0) {
            String kafkaMsg = toString(jsonArray);
            if (kafkaMsg != null) {
                logger.info("send {} docs to kafka with size {}", jsonArray.size(), batchSize);
                publish(kafkaMsg, operation, directedTowards, topic);
            }
        }
    }

    private void publishKVData(
        final List<SolrInputDocument> docs,
        final String directedTowards,
        final String operation
    ) throws Exception {
        int batchSize = 0;
        List<Data> dataList = Lists.newArrayList();
        for (SolrInputDocument doc : docs) {
            Optional<Data> data = toKvStoreData(doc);
            if(!data.isPresent()) {
                continue;
            }

            int docSize = MAPPER.writeValueAsString(data.get()).getBytes(UTF8).length;
            if (docSize > KAFKA_MAX_REQUEST_SIZE) {
                logger.error("CORE:{} DOC:{} SIZE:{} OVER KAFKA LIMIT", directedTowards, doc.getFieldValue("id").toString(), docSize);
                continue;
            }
            batchSize += docSize;
            if (batchSize < KAFKA_MAX_REQUEST_SIZE) {
                dataList.add(data.get());
            } else {
                final KVData kafkaMsg = KVData.builder()
                    .setKvData(ImmutableList.copyOf(dataList))
                    .build();
                if (kafkaMsg != null) {
                    logger.info("send {} docs to kafka with size {}", dataList.size(), batchSize);
                    publish(kafkaMsg, operation, directedTowards, KVSTORE_TOPIC);
                }
                dataList.clear();
                dataList.add(data.get());
                batchSize = docSize;
            }
        }
        if (dataList.size() > 0) {
            final KVData kafkaMsg = KVData.builder()
                .setKvData(ImmutableList.copyOf(dataList))
                .build();
            if (kafkaMsg != null) {
                logger.info("send {} docs to kafka with size {}", dataList.size(), batchSize);
                publish(kafkaMsg, operation, directedTowards, KVSTORE_TOPIC);
            }
        }

    }

    private void publish(final Object kafkaMsg, final String operation, final String directedTowards, final String kafkaTopic) {
        Map<String, Object> meta = Maps.newHashMap();
        meta.put("operation", operation);
        meta.put("entity", entity);
        if (directedTowards != null) {
            meta.put("target", directedTowards);
        }
        final KafkaData<Object> kafkaData = new KafkaData<>(kafkaMsg, meta);

        kafkaClient.send(kafkaTopic, kafkaData).subscribe(new Subscriber<RecordMetadata>() {
            @Override
            public void onCompleted() {
                kafkaIndexPublishSuccess.mark();
            }

            @Override
            public void onError(Throwable e) {
                logger.warn("solr kafka message publish with error {}", e.getMessage());
                kafkaIndexPublishFailure.mark();
            }

            @Override
            public void onNext(RecordMetadata recordMetadata) {

            }
        });
    }

    /**
     * Publish the docs we are writing to Kafka.
     * @param doc The doc we are publishing
     * @param directedTowards The target
     */
    private void publishUpdate(final SolrInputDocument doc, final String directedTowards) {
        publishUpdate(Lists.<SolrInputDocument>newArrayList(doc), directedTowards);
    }


    /**
     * Publish the docs we are writing to Kafka.
     * @param id The id of the doc we are deleting
     */
    private void publishDelete(final String id) {
        publishDelete(Lists.newArrayList(id));
    }

    /**
     * Publish the docs we are writing to Kafka.
     * @param ids The ids for the docs we are deleting
     */
    private void publishDelete(final List<String> ids) {
        try {
            ArrayNode jsonArray = JsonNodeFactory.instance.arrayNode();

            for (String id : ids) {
                jsonArray.add(id);
            }

            String kafkaMsg = toString(jsonArray);

            if (kafkaMsg != null) {
                Map<String, Object> meta = Maps.newHashMap();
                meta.put("operation", "delete");
                meta.put("entity", entity);
                KafkaData<String> kafkaData = new KafkaData<>(kafkaMsg, meta);

                // send this event to Kafka
                logger.debug("Sending index request to Kafka: " + kafkaData);
                kafkaClient.send(topic, kafkaData);
            }
        }
        catch (Throwable t) {
            logger.error("Failed to publish delete to kafka!", t);
        }

    }

    private Pair<Boolean, Object> convertFieldValue(final SolrInputField field) {
        Object fieldValue = field.getValue();
        if (isUpdateField(field)) {
            if (((Map)fieldValue).containsKey(UPDATE_FIELD_SET)) {
                fieldValue = ((Map) fieldValue).get(UPDATE_FIELD_SET);
            } else {
                return new ImmutablePair<>(Boolean.FALSE, null);
            }
        }

        if (fieldValue instanceof Date) {
            fieldValue = DATE_FORMATTER.print(((Date)fieldValue).getTime());
        } else if (fieldValue instanceof Calendar) {
            fieldValue = DATE_FORMATTER.print(((Calendar)fieldValue).getTimeInMillis());
        } else if (fieldValue instanceof DateTime) {
            fieldValue = DATE_FORMATTER.print((DateTime) fieldValue);
        } else if (fieldValue instanceof LocalDate) {
            LocalDate localDate = (LocalDate) fieldValue;
            fieldValue = DATE_FORMATTER.print(localDate.toDateTimeAtStartOfDay(DateTimeZone.UTC));
        }
        return new ImmutablePair<>(Boolean.TRUE, fieldValue);
    }


    /**
     * Converts the given solr input doc to JSON
     * @param doc The doc we are converting
     * @return ObjectNode The converted doc
     */
    private ObjectNode toJson(final SolrInputDocument doc) {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        for (SolrInputField field : doc.values()) {
            Pair<Boolean, Object> fieldValue = convertFieldValue(field);
            if (fieldValue.getLeft()) {
                json.putPOJO(field.getName(), fieldValue.getRight());
            }
        }

        return json;
    }

    /**
     * Converts the given solr input doc to KvStore JSON
     * @param doc The doc we are converting
     * @return ObjectNode The converted doc
     */
    private Optional<Data> toKvStoreData(final SolrInputDocument doc) {
		String namespace = kvStoreNamespaceFromSolrEntity(entity);

        if(doc.getFieldValue("uuid") == null) {
            logger.error("SolrInputDocument doesn't have UUID: {}", doc);
            return Optional.absent();
        }
        String uuid = doc.getFieldValue("uuid").toString();

        DataBuilder dataBuilder = Data.builder()
            .setUuid(uuid)
            .setNamespace(namespace)
            .setClient("monolith_client");
        List<KV> kvs = new ArrayList<>();
        for (SolrInputField field : doc.values()) {
            Pair<Boolean, Object> fieldValue = convertFieldValue(field);
            if (fieldValue.getLeft()) {
                KV kv = KV.builder()
                    .setK(field.getName())
                    .setV(fieldValue.getRight())
                    .build();
                kvs.add(kv);
            }
        }
        dataBuilder.setKvs(kvs);

        return Optional.of(dataBuilder.build());
    }

    /**
     * Converts the given JsonNode to a string.
     * @param json The object we are converting to a string
     * @return String The json or null if there is a problem converting
     */
    private String toString(final JsonNode json) {
        try {
            return MAPPER.writeValueAsString(json);
        } catch (JsonProcessingException jpe) {
            logger.error("json serialization error {}", jpe.getMessage());
        }
        return null;
    }

    /**
     * Determines if the given doc is an update request or an insert.
     * @param doc The doc we are looking at
     * @return boolean Returns true if this is an update, false otherwise
     */
    private boolean isUpdate(final SolrInputDocument doc) {
        for (SolrInputField field : doc.values()) {
            if (isUpdateField(field)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given field value is an update.
     * @param field The field we are validating
     * @return boolean Returns true if the field is an update,
     */
    private boolean isUpdateField(SolrInputField field) {
        Object fieldValue = field.getValue();
        if (fieldValue != null) {
            return fieldValue instanceof Map
                && ((Map) fieldValue).size() == 1
                && UPDATE_FIELDS.contains(((Map) fieldValue).keySet().iterator().next());
        }

        return false;
    }

    private String kvStoreNamespaceFromSolrEntity(final String solrEntity) {
        switch (solrEntity) {
            case "user":
                return "ENGAGEMENT";
            case "work":
                return "WORK";
            case "pay":
                return "PAY";
            case "group":
                return "TEAM";
            default:
                return "NOTDEFINED";
        }
    }

}
