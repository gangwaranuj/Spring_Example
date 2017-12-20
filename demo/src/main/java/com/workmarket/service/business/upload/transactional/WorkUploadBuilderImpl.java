package com.workmarket.service.business.upload.transactional;

import com.workmarket.service.business.dto.DeliverableRequirementDTO;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.upload.parser.CustomFieldParser;
import com.workmarket.service.business.upload.parser.GeneralParser;
import com.workmarket.service.business.upload.parser.LocationContactParser;
import com.workmarket.service.business.upload.parser.LocationParser;
import com.workmarket.service.business.upload.parser.PartGroupParser;
import com.workmarket.service.business.upload.parser.PricingStrategyParser;
import com.workmarket.service.business.upload.parser.ScheduleParser;
import com.workmarket.service.business.upload.parser.SecondaryLocationContactParser;
import com.workmarket.service.business.upload.parser.WorkBundleParser;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.thrift.work.Work;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkUploadBuilderImpl implements WorkUploadBuilder {

	@Autowired private GeneralParser generalParser;
	@Autowired private WorkBundleParser workBundleParser;
	@Autowired private ScheduleParser scheduleParser;
	@Autowired @Qualifier("locationParserInternationalImpl") private LocationParser locationParser;
	@Autowired private LocationContactParser locationContactParser;
	@Autowired private SecondaryLocationContactParser secondaryLocationContactParser;
	@Autowired private PricingStrategyParser pricingStrategyParser;
	@Autowired private PartGroupParser partGroupParser;
	@Autowired private CustomFieldParser customFieldParser;

	@Override
	public WorkUploaderBuildResponse buildFromRow(WorkUploaderBuildData buildData) {
		Work work = buildData.getWork();
		work.setId(0L);
		work.setWorkNumber(null);

		DeliverableRequirementGroupDTO deliverableRequirementGroupDTO = work.getDeliverableRequirementGroupDTO();
		if (deliverableRequirementGroupDTO != null) {
			deliverableRequirementGroupDTO.setId(null);

			List<DeliverableRequirementDTO> deliverableRequirementDTOs = deliverableRequirementGroupDTO.getDeliverableRequirementDTOs();
			if (CollectionUtils.isNotEmpty(deliverableRequirementDTOs)) {
				for (DeliverableRequirementDTO deliverableRequirementDTO : deliverableRequirementDTOs) {
					deliverableRequirementDTO.setId(null);
				}
			}
		}

		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(work);

		generalParser.build(response, buildData);
		workBundleParser.build(response, buildData);
		locationParser.build(response, buildData);
		scheduleParser.build(response, buildData);
		locationContactParser.build(response, buildData);
		secondaryLocationContactParser.build(response, buildData);
		pricingStrategyParser.build(response, buildData);
		partGroupParser.build(response, buildData);
		customFieldParser.build(response, buildData);

		return response;
	}
}
