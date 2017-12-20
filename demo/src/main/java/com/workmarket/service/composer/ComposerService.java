package com.workmarket.service.composer;

import java.util.List;
import com.workmarket.common.core.RequestContext;
import com.workmarket.domains.model.composer.ComposerField;
import com.workmarket.domains.model.composer.ComposerFieldInstance;
import com.workmarket.domains.model.composer.ComposerFieldResponse;

public interface ComposerService {

  List<ComposerFieldResponse> setValues(RequestContext context, List<ComposerFieldInstance> fields);

  List<ComposerFieldInstance> getValues(RequestContext context, List<ComposerField> fields);

}