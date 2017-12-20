package com.workmarket.domains.groups.service;

import com.workmarket.domains.model.DocumentationManager;
import com.workmarket.domains.model.DocumentationVisitor;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.certification.UserCertificationAssociation;
import com.workmarket.domains.groups.model.UserUserGroupDocumentReference;
import com.workmarket.domains.model.insurance.UserInsuranceAssociation;
import com.workmarket.domains.model.license.UserLicenseAssociation;
import com.workmarket.service.business.UserService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.file.RemoteFileAdapter;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.utility.CollectionUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * User: micah
 * Date: 1/5/14
 * Time: 6:46 PM
 */
@Component
public class DocumentationVisitorImpl implements DocumentationVisitor {
	private static final Log logger = LogFactory.getLog(DocumentationVisitorImpl.class);

	@Autowired private RemoteFileAdapter remoteFileAdapter;
	@Autowired private UserService userService;

	@Override
	public void visit(DocumentationManager documentationManager, UserUserGroupDocumentReference document) {
		InputStream stream = null;
		try {
			stream = remoteFileAdapter.getFileStream(
					document.getReferencedDocument().getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE,
					document.getReferencedDocument().getUUID()
			);
			Map<String, Object> props = userService.getProjectionMapById(document.getReferencedDocument().getCreatorId(), "firstName", "lastName", "userNumber");
			documentationManager.putNextAsset("documents", document.getReferencedDocument(), (String) props.get("firstName"),
					(String) props.get("lastName"), (String) props.get("userNumber"), stream);
			stream.close();
		} catch (HostServiceException|IOException e) {
			logger.error(e);
		} finally {
			try {
				if (stream != null) { stream.close(); }
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void visit(DocumentationManager documentationManager, UserLicenseAssociation userLicenseAssociation) {
		InputStream stream = null;
		try {
			Set<Asset> licenses = userLicenseAssociation.getAssets();
			Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(licenses, "creatorId"),
					"firstName", "lastName", "userNumber");

			for (Asset license : licenses) {
				stream = remoteFileAdapter.getFileStream(
						license.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE,
						license.getUUID()
				);
				Map<String, Object> props = creatorProps.get(license.getCreatorId());
				documentationManager.putNextAsset("licenses", license, (String) props.get("firstName"), (String) props.get("lastName"),
						(String) props.get("userNumber"), stream);
				stream.close();
			}
		} catch (HostServiceException|IOException e) {
			logger.error(e);
		} finally {
			try {
				if (stream != null) { stream.close(); }
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void visit(DocumentationManager documentationManager, UserInsuranceAssociation userInsuranceAssociation) {
		InputStream stream = null;
		try {
			Set<Asset> insurances = userInsuranceAssociation.getAssets();
			Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(insurances, "creatorId"),
					"firstName", "lastName", "userNumber");

			for (Asset insurance : insurances) {
				stream = remoteFileAdapter.getFileStream(
						insurance.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE,
						insurance.getUUID()
				);
				Map<String, Object> props = creatorProps.get(insurance.getCreatorId());
				documentationManager.putNextAsset("insurances", insurance, (String) props.get("firstName"), (String) props.get("lastName"),
						(String) props.get("userNumber"), stream);
				stream.close();
			}
		} catch (HostServiceException|IOException e) {
			logger.error(e);
		} finally {
			try {
				if (stream != null) { stream.close(); }
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void visit(DocumentationManager documentationManager, UserCertificationAssociation userCertificationAssociation) {
		InputStream stream = null;
		try {
			Set<Asset> assets = userCertificationAssociation.getAssets();
			Map<Long, Map<String, Object>> creatorProps = userService.getProjectionMapByIds(CollectionUtilities.newListPropertyProjection(assets, "creatorId"),
					"firstName", "lastName", "userNumber");

			for (Asset certification : assets) {
				stream = remoteFileAdapter.getFileStream(
						certification.getAvailability().hasGuestAvailability() ? RemoteFileType.PUBLIC : RemoteFileType.PRIVATE,
						certification.getUUID()
				);
				Map<String, Object> props = creatorProps.get(certification.getCreatorId());
				documentationManager.putNextAsset("certifications", certification, (String) props.get("firstName"), (String) props.get("lastName"),
						(String) props.get("userNumber"), stream);
				stream.close();
			}
		} catch (HostServiceException|IOException e) {
			logger.error(e);
		} finally {
			try {
				if (stream != null) { stream.close(); }
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}
