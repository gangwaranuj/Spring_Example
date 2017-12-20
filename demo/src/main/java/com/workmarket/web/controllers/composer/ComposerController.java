package com.workmarket.web.controllers.composer;

import com.workmarket.domains.model.composer.ComposerField;
import com.workmarket.domains.model.composer.ComposerFieldInstance;
import com.workmarket.domains.model.composer.ComposerFieldResponse;
import com.workmarket.service.composer.ComposerService;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/v2/composer")
public class ComposerController extends BaseController{

  @Autowired private WebRequestContextProvider webRequestContextProvider;
  @Autowired private ComposerService composerService;

  @RequestMapping(
    value = "/set-values",
    method = POST,
    produces = APPLICATION_JSON_VALUE)
  public List<ComposerFieldResponse> setValues(@RequestBody List<ComposerFieldInstance> fields) {
    return composerService.setValues(webRequestContextProvider.getRequestContext(), fields);
  }

  @RequestMapping(
    value = "/get-values",
    method = POST,
    produces = APPLICATION_JSON_VALUE)
  public List<ComposerFieldInstance> getValues(@RequestBody List<ComposerField> fields) {
    return composerService.getValues(webRequestContextProvider.getRequestContext(), fields);
  }

}