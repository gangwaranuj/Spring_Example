package com.workmarket.api.v2.controllers;

import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import com.workmarket.api.ApiBaseController;
import com.workmarket.api.exceptions.BadRequestApiException;
import com.workmarket.api.exceptions.ForbiddenException;
import com.workmarket.api.exceptions.NotFoundApiException;
import com.workmarket.api.exceptions.UnprocessableEntityException;
import com.workmarket.api.v2.ApiV2Response;
import com.workmarket.api.v2.model.NationalIdApiDTO;
import com.workmarket.api.v2.model.TaxInfoApiDTO;
import com.workmarket.api.v2.model.TaxInfoSaveApiDTO;
import com.workmarket.api.v2.model.W9PdfPreviewApiDTO;
import com.workmarket.api.v2.utils.TaxInfoSaveValidator;
import com.workmarket.domains.model.tax.AbstractTaxEntity;
import com.workmarket.domains.model.tax.TaxVerificationStatusType;
import com.workmarket.feature.gen.Messages;
import com.workmarket.feature.vo.FeatureToggleAndStatus;
import com.workmarket.service.business.dto.TaxEntityDTO;
import com.workmarket.service.business.tax.TaxService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.infra.business.InvariantDataService;

import groovy.lang.Tuple2;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = {"tax"})
@Controller("apiV2TaxController")
@RequestMapping("/")
public class TaxController extends ApiBaseController {
  private static final Log logger = LogFactory.getLog(TaxController.class);
  @Autowired
  private TaxService taxService;
  @Autowired
  private InvariantDataService invariantDataService;
  @Autowired
  private FeatureEntitlementService featureEntitlementService;

  @VisibleForTesting
  void setFeatureEntitlementService(final FeatureEntitlementService featureEntitlementService) {
    this.featureEntitlementService = featureEntitlementService;
  }

  @RequestMapping(
      value = "/v2/tax/w9_preview",
      method = RequestMethod.POST
  )
  @ApiOperation(value = "Return PDF W9 preview.")
  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  public void previewW9(
      @RequestBody final W9PdfPreviewApiDTO dto,
      final HttpServletResponse response) throws Exception {
    Tuple2<File, String> fileAndName = null;
    try {
      fileAndName = taxService.buildPdfForTaxForm(dto);
      if (fileAndName != null) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", String.format("inline; filename=%s", fileAndName.getSecond()));
        int bytesCopied = IOUtils.copy(new FileInputStream(fileAndName.getFirst()), response.getOutputStream());
        response.setContentLength(bytesCopied);
        response.flushBuffer();
      }
    } catch (final Exception e) {
      logger.error("Error building pdf file", e);
    } finally {
      try {
        if (fileAndName != null && fileAndName.getFirst() != null) {
          fileAndName.getFirst().delete();
        }
      } catch (final Exception e) {
        logger.error("Error closing pdf file", e);
      }
    }
  }

  @RequestMapping(
      value = "/v2/tax/info",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ApiOperation(value = "Get the current user's latest tax info.")
  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  @ResponseBody
  public ApiV2Response<TaxInfoApiDTO> getLatestTaxInfo() throws Exception {
    final Optional<TaxInfoApiDTO> dto = taxService.getTaxInfoForUser(getCurrentUser().getId());

    return dto.transform(new Function<TaxInfoApiDTO, ApiV2Response>() {
      @Nullable
      @Override
      public ApiV2Response apply(final TaxInfoApiDTO taxInfoApiDTO) {
        return ApiV2Response.valueWithResult(taxInfoApiDTO);
      }
    }).or(new Supplier<ApiV2Response>() {
      @Override
      public ApiV2Response get() {
        return ApiV2Response.<TaxInfoApiDTO>OK();
      }
    });
  }

  @RequestMapping(
      value = "/v2/tax/save",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ApiOperation(value = "Create a new entity for the current user.")
  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  @ResponseBody
  public ApiV2Response<TaxInfoApiDTO> save(@RequestBody final TaxInfoSaveApiDTO dto) throws Exception {
    final List<String> errors = TaxInfoSaveValidator.validate(dto);

    if (errors.size() > 0) {
      throw new UnprocessableEntityException(errors);
    }

    //Once TIN/BN/SIN is validated by the system, set status to the VALIDATED for Canadian users and SIGNED_FORM_W8 for international users.
    TaxEntityDTO taxEntityDTO = taxService.convert(dto);
    if(AbstractTaxEntity.COUNTRY_CANADA.equalsIgnoreCase(taxEntityDTO.getTaxCountry())) {
      taxEntityDTO.setTaxVerificationStatusCode(TaxVerificationStatusType.VALIDATED);
    }
    else if(AbstractTaxEntity.COUNTRY_OTHER.equalsIgnoreCase(taxEntityDTO.getTaxCountry())) {
      taxEntityDTO.setTaxVerificationStatusCode(TaxVerificationStatusType.SIGNED_FORM_W8);
    }

    final AbstractTaxEntity entity =
        taxService.saveTaxEntityForCompany(getCurrentUser().getCompanyId(), taxEntityDTO);

    if (entity == null) {
      throw new BadRequestApiException("Could not create tax entity.");
    }

    return ApiV2Response.valueWithResult(taxService.convert(entity)
        .transform(new Function<TaxInfoApiDTO, TaxInfoApiDTO>() {
          @Nullable
          @Override
          public TaxInfoApiDTO apply(@Nullable TaxInfoApiDTO taxInfoApiDTO) {
            return taxInfoApiDTO;
          }
        }).or(new Supplier<TaxInfoApiDTO>() {
          @Override
          public TaxInfoApiDTO get() {
            throw new NotFoundApiException("No active tax info found.");
          }
        }));
  }

  @RequestMapping(
      value = "/v2/tax/national_ids",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ApiOperation(value = "Get the list of National IDs.")
  @ApiResponses(value = {@ApiResponse(code = 200, message = MESSAGE_OK)})
  @ResponseBody
  public ApiV2Response<NationalIdApiDTO> getNationalIdList() throws Exception {
    return ApiV2Response.valueWithResults(invariantDataService.getAllNationalIds());
  }
}
