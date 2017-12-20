package com.workmarket.service.composer;

import com.workmarket.common.core.RequestContext;
import com.workmarket.core.composer.ComposerClient;
import com.workmarket.core.composer.gen.Messages.Set;
import com.workmarket.core.composer.gen.Messages.Get;
import com.workmarket.core.composer.gen.Messages.Status;
import com.workmarket.core.composer.gen.Messages.UserIdentity;
import com.workmarket.core.composer.gen.Messages.Scope;
import com.workmarket.core.composer.gen.Messages.Data;
import com.workmarket.core.composer.gen.Messages.DataResp;
import com.workmarket.core.composer.gen.Messages.NamespaceAndNameToValue;
import com.workmarket.domains.model.composer.ComposerField;
import com.workmarket.domains.model.composer.ComposerFieldInstance;
import com.workmarket.domains.model.composer.ComposerFieldResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.List;

@Service
public class ComposerServiceImpl implements ComposerService {

	@Autowired private ComposerClient composerClient;

	@Override
	public List<ComposerFieldResponse> setValues(RequestContext context, List<ComposerFieldInstance> fields) {
		List<ComposerFieldResponse> response = new LinkedList<>();

		for (ComposerFieldInstance field : fields) {
			Status status =
				composerClient.set(Set.newBuilder()
					.setUserIdentity(genIdentity(context))
					.setScope(genScope(field))
					.addValues(NamespaceAndNameToValue.newBuilder()
						.setNamespace(context.getCompanyId())
						.setName(field.getKey())
						.setValue(field.getValue()))
					.build(), context)
					.toBlocking().single();

			response.add(new ComposerFieldResponse(field.getUuid(), field.getKey(), status.getSuccess(), status.getMessageList()));
		}

		return response;
	}

	@Override
	public List<ComposerFieldInstance> getValues(final RequestContext context, List<ComposerField> fields) {
		List<ComposerFieldInstance> response = new LinkedList<>();

		for (ComposerField field : fields) {
			DataResp profile =
				composerClient.get(Get.newBuilder()
					.setUserIdentity(genIdentity(context))
					.setScope(genScope(field))
					.build(), context)
					.toBlocking().single();

			String value = getValueFor(context.getCompanyId(), field.getKey(), profile.getDataList());
			if (StringUtils.isNotEmpty(value)) {
				response.add(new ComposerFieldInstance(field.getType(), field.getUuid(), field.getKey(), value));
			}
		}

		return response;
	}

	private String getValueFor(String namespace, String name, List<Data> allData) {
		for (final Data data : allData) {
			if (data.getNamespace().equals(namespace) && data.getName().equals(name)) {
				return data.getValue();
			}
		}
		return "";
	}

	private UserIdentity.Builder genIdentity(RequestContext context) {
		return UserIdentity.newBuilder()
			.setCompanyUuid(context.getCompanyId())
			.setUserUuid(context.getUserId());
	}

	private Scope.Builder genScope(ComposerField field) {
		return Scope.newBuilder()
			.setUuid(field.getUuid())
			.setScopeType(field.getType());
	}
}
