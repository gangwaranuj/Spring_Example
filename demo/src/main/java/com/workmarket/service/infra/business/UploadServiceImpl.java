package com.workmarket.service.infra.business;

import com.codahale.metrics.MetricRegistry;
import com.google.api.client.util.Objects;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.workmarket.common.kafka.KafkaClient;
import com.workmarket.common.kafka.KafkaData;
import com.workmarket.helpers.WMCallable;
import com.workmarket.jan20.IsEqual;
import com.workmarket.jan20.Trial;
import com.workmarket.jan20.TrialResult;
import com.workmarket.configuration.Constants;
import com.workmarket.dao.upload.UploadDAO;
import com.workmarket.domains.authentication.features.FeatureEvaluator;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.work.service.part.HibernateTrialWrapper;
import com.workmarket.id.IdGenerator;
import com.workmarket.media.MediaClient;
import com.workmarket.media.MediaSuccessionResponse;
import com.workmarket.media.Status;
import com.workmarket.media.StatusCode;
import com.workmarket.media.SuccessionMediaCreateRequest;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.file.RemoteFile;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.FileUtilities;
import com.workmarket.web.helpers.MessageBundleHelper;
import com.workmarket.web.helpers.UploadHelper;
import com.workmarket.web.models.MessageBundle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class UploadServiceImpl implements UploadService {

	private static final Log logger = LogFactory.getLog(UploadServiceImpl.class);

	@Autowired private FeatureEvaluator featureEvaluator;
	@Autowired private HibernateTrialWrapper hibernateWrapper;
	@Autowired private IdGenerator idGenerator;
	@Autowired private MediaClient mediaClient;
	@Autowired private MediaResponseAdapter mediaResponseAdapter;
	@Autowired private MessageBundleHelper messageHelper;
	@Autowired private MetricRegistry metricRegistry;
	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private UploadDAO uploadDAO;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Value("${uploadDirectory}")
	private String uploadDirectory;

	private ThreadPoolExecutor experimentExecutor;
	private Trial trial;

	private static KafkaClient KAFKA_CLIENT;
	private static final String TRIAL_LOG_TOPIC = "media.experiment.compareFailures";

	@Override
	public String getAuthorizedUriByUuid(String uuid) throws HostServiceException {
		return getAuthorizedUri(uploadDAO.findUploadByUUID(uuid), false);
	}

	@Override
	public String getAuthorizedDownloadUriByUuid(String uuid) throws HostServiceException {
		return getAuthorizedUri(uploadDAO.findUploadByUUID(uuid), true);
	}

	private String getAuthorizedUri(Upload upload, boolean isDownload) throws HostServiceException {
		// TODO Do we need additional authorization?

		if (upload == null)
			return null;

		try {
			URL uri = remoteFileAdapter.getAuthorizedURL(RemoteFileType.TMP, upload.getUUID(), null, CollectionUtilities.newStringMap(
				"response-content-type", upload.getMimeType(),
				"response-content-disposition", String.format("%s; filename=%s", isDownload ? "attachment" : "inline", upload.getFilename())
			));
			return uri.toString();
		} catch (HostServiceException e) {
			logger.error(String.format("Unable to get authorized URI for upload [id => %d, uuid => %s]", upload.getId(), upload.getUUID()), e);
			throw e;
		}
	}

	@Override
	public Upload storeUpload(InputStream stream, String filename, String mime, long byteSize) throws IOException, HostServiceException {
		Assert.hasText(filename);
		Assert.hasText(mime);

		UUID uuid = UUID.randomUUID();

		RemoteFile remote = remoteFileAdapter.put(stream, byteSize, RemoteFileType.TMP, mime, uuid.toString());

		Upload upload = new Upload();
		upload.setSourcePath(filename);
		upload.setFilename(filename);
		upload.setUUID(uuid.toString());
		upload.setMimeType(mime);
		upload.setFileByteSize(Long.valueOf(byteSize).intValue());
		upload.setRemoteUri(remote.getRemoteUri());
		upload.setCdnUri(remote.getCdnUri());

		uploadDAO.saveOrUpdate(upload);

		return upload;
	}

	@Override
	public MediaSuccessionResponse storeExperimentUpload(
		InputStream stream,
		String filename,
		String mime,
		long byteSize) throws IOException, HostServiceException {
		Assert.hasText(filename);
		Assert.hasText(mime);

		return runMediaCreateTrial(stream, filename, mime, byteSize);
	}

	@Override
	public Upload storeUpload(String sourcePath, String filename, String mime) throws IOException, HostServiceException {
		Assert.notNull(uploadDirectory);
		Assert.hasText(sourcePath);
		Assert.hasText(filename);
		Assert.hasText(mime);
		Assert.isTrue(new File(sourcePath).exists(), "Upload file does not exists");

		UUID uuid = UUID.randomUUID();

		File source = new File(sourcePath);
		int byteSize = (int) source.length();

		RemoteFile remote = remoteFileAdapter.put(source, RemoteFileType.TMP, uuid.toString());

		FileUtils.deleteQuietly(source);

		Upload upload = new Upload();
		upload.setSourcePath(sourcePath);
		upload.setFilename(filename);
		upload.setUUID(uuid.toString());
		upload.setMimeType(mime);
		upload.setFileByteSize(byteSize);
		upload.setRemoteUri(remote.getRemoteUri());
		upload.setCdnUri(remote.getCdnUri());

		uploadDAO.saveOrUpdate(upload);

		return upload;
	}

	@Override
	public Upload findUploadByUUID(String uuid) {
		Assert.hasText(uuid);

		Upload upload = uploadDAO.findUploadByUUID(uuid);
		if (upload != null) {
			upload.setFilePath(uploadDirectory + File.separatorChar + upload.getUUID());
		}
		return upload;
	}

	@Override
	public Map<String, Object> doFileUpload(
		String fileName,
		String contentType,
		long contentLength,
		InputStream inputStream) throws IOException, HostServiceException {

		MessageBundle messages = validateContentMetadata(contentType, contentLength);
		if (messages.hasErrors()) {
			return CollectionUtilities.newObjectMap(
				"successful", false,
				"errors", messages.getErrors()
			);
		}

		MediaSuccessionResponse mediaSuccessionResponse =
			storeExperimentUpload(inputStream, fileName, contentType, contentLength);
		Assert.notNull(mediaSuccessionResponse);

		return CollectionUtilities.newObjectMap(
			"successful", true,
			"file_name", mediaSuccessionResponse.getName(),
			"uuid", mediaSuccessionResponse.getUuid(),
			"mime_type", mediaSuccessionResponse.getMimeType(),
			"mime_type_icon", UploadHelper.getMimeTypeIcon(mediaSuccessionResponse.getMimeType()),
			"of_type_image", mediaSuccessionResponse.getMimeType().contains("image"),
			"createdOn", Calendar.getInstance().getTimeInMillis()
		);
	}

	@Override
	public MessageBundle validateContentMetadata(String contentType, long contentLength) {
		MessageBundle messages = messageHelper.newBundle();

		if (!UploadHelper.isValidMimeType(contentType)) {
			if (StringUtils.isBlank(contentType)) {
				messageHelper.addError(messages, "upload.invalid_no_extension");
			} else {
				messageHelper.addError(messages, "upload.invalid", contentType);
			}
		}

		if (contentLength > Constants.MAX_UPLOAD_SIZE) {
			messageHelper.addError(messages, "upload.sizelimit", Constants.MAX_UPLOAD_SIZE);
		}

		return messages;
	}

	private KafkaData<Object> getStringObjectMap(
		final Object a,
		final Object b,
		final String name,
		final List<String> errors,
		final String method) {
		final Map<String, Object> metadata = new HashMap<>();
		metadata.put("method", method);
		metadata.put("name", name);
		metadata.put("control", a);
		metadata.put("experiment", b);
		metadata.put("mismatches", errors);
		metadata.put("timestamp", new DateTime().toString());
		return new KafkaData<Object>(metadata);
	}

	private IsEqual<Throwable> makeBothOrNeitherThrow(final String method) {
		return new IsEqual<Throwable>() {
			@Override
			public boolean apply(final Throwable a, final Throwable b) {
				final boolean result = ((a == null) == (b == null));
				if (!result) {
					KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(a, b, "bothOrNeitherThrow", ImmutableList.<String>of(),
						method));
				}
				return result;
			}
		};
	}

	private IsEqual<MediaSuccessionResponse> makeUploadIsEqual(final String method) {
		return new IsEqual<MediaSuccessionResponse>() {
			@Override
			public boolean apply(final MediaSuccessionResponse control, final MediaSuccessionResponse experiment) {
				final boolean success = Objects.equal(control.getEntityTag(), experiment.getEntityTag());

				if (!success) {
					KAFKA_CLIENT.send(TRIAL_LOG_TOPIC, getStringObjectMap(control, experiment, "makeMediaIsEqual",
						ImmutableList.<String>of(), method));
				}

				return success;
			}
		};
	}

	private IsEqual<TrialResult<MediaSuccessionResponse>> makeMediaTrialIsEqual(final String method) {
		return Trial.makeIsEqual(makeBothOrNeitherThrow(method), makeUploadIsEqual(method).pairwiseEqual());
	}

	private MediaSuccessionResponse runMediaCreateTrial(
		final InputStream stream,
		final String filename,
		final String mime,
		final long byteSize) {

		final String uuid = UUID.randomUUID().toString();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// Need to read the stream twice for succession, can be removed as part of turn down.
		final byte[] bytes = getBytes(stream, byteArrayOutputStream);

		final Callable<Upload> control = new WMCallable<Upload>(webRequestContextProvider) {
			@Override
			public Upload apply() throws HostServiceException {
				Assert.hasText(filename);
				Assert.hasText(mime);

				ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
				RemoteFile remote = remoteFileAdapter.put(inputStream, byteSize, RemoteFileType.TMP, mime, uuid);

				Upload upload = new Upload();
				upload.setSourcePath(filename);
				upload.setFilename(filename);
				upload.setUUID(uuid);
				upload.setMimeType(mime);
				upload.setFileByteSize(Long.valueOf(byteSize).intValue());
				upload.setRemoteUri(remote.getRemoteUri());
				upload.setCdnUri(remote.getCdnUri());
				upload.setETag(remote.getETag());

				uploadDAO.saveOrUpdate(upload);

				return upload;
			}
		};

		final Callable<Observable<MediaSuccessionResponse>> experiment = new WMCallable<Observable<MediaSuccessionResponse>>(webRequestContextProvider) {
			@Override
			public Observable<MediaSuccessionResponse> apply() {
				String s3Key = FileUtilities.createRemoteFileandDirectoryStructor(uuid);
				try {
					File file = File.createTempFile(uuid, null);
					SuccessionMediaCreateRequest mediaCreateRequest =
						new SuccessionMediaCreateRequest(filename, null, s3Key);
					file.deleteOnExit();
					ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
					FileUtils.copyInputStreamToFile(inputStream, file);
					return mediaClient.successionCreate(mediaCreateRequest, file, webRequestContextProvider.getRequestContext());
				} catch (IOException e) {
					e.printStackTrace();
				}

				MediaSuccessionResponse mediaSuccessionResponse =
					MediaSuccessionResponse.builder()
						.setStatus(new Status(StatusCode.FAILED, "Couldn't create temporary file"))
						.build();
				return Observable.just(mediaSuccessionResponse);
			}
		};

		try {
			final Observable<MediaSuccessionResponse> result = doTrial(
				convertSingle(control), experiment, makeMediaTrialIsEqual("runCreateMediaTrial"), "createMedia");
			return result.toBlocking().single();
		} catch (final Exception e) {
			throw new RuntimeException("media create failed", e);
		}
	}

	private byte[] getBytes(final InputStream stream, final ByteArrayOutputStream byteArrayOutputStream) {
		final byte[] bytes;
		try {
			IOUtils.copy(stream, byteArrayOutputStream);
			bytes = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bytes;
	}

	private Callable<Observable<MediaSuccessionResponse>> convertSingle(final Callable<Upload> original) {
		return new WMCallable<Observable<MediaSuccessionResponse>>(webRequestContextProvider) {
			@Override
			public Observable<MediaSuccessionResponse> apply() throws Exception {
				final MediaSuccessionResponse mediaSuccessionResponse = mediaResponseAdapter.asMediaSuccessionResponse(original.call());
				if (mediaSuccessionResponse != null) {
					return Observable.from(ImmutableList.of(mediaSuccessionResponse));
				}
				return Observable.empty();
			}
		};
	}

	private <T> Observable<T> doTrial(
		final Callable<Observable<T>> control,
		final Callable<Observable<T>> experiment,
		final IsEqual<TrialResult<T>> isEqual,
		final String metric) throws Exception {
		return trial.doTrial(control, experiment, isEqual, metric);
	}

	@Autowired
	public void setKafkaClient(@Qualifier("AppKafkaClient") final KafkaClient client) {
		KAFKA_CLIENT = client;
	}

	@VisibleForTesting
	public void setTrial(Trial trial) {
		this.trial = trial;
	}

	@PostConstruct
	public void postConstruct() {
		experimentExecutor =
			new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10));
		trial = new Trial(experimentExecutor, metricRegistry, "media.succession", new Supplier<Trial.WhichReturn>() {
			@Override
			public Trial.WhichReturn get() {
				return featureToggleWhichReturn();
			}
		}, Trial.IDENTITY_WRAPPER, hibernateWrapper);
	}

	private Trial.WhichReturn featureToggleWhichReturn() {
		if (featureEvaluator.hasGlobalFeature("mediaTrialWhichReturnControlOnly")) {
			return Trial.WhichReturn.CONTROL_ONLY;
		}

		if (featureEvaluator.hasGlobalFeature("mediaTrialWhichReturnExperiment")) {
			return Trial.WhichReturn.EXPERIMENT;
		}

		return Trial.WhichReturn.CONTROL;
	}
}
