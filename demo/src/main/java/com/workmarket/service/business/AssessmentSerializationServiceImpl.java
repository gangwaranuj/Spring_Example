package com.workmarket.service.business;

import com.google.gson.*;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.GradedAssessment;
import com.workmarket.domains.model.assessment.SurveyAssessment;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AssessmentSerializationServiceImpl implements AssessmentSerializationService {
	Gson gson = new GsonBuilder()
		.registerTypeAdapter(GradedAssessment.class, AssessmentSerializer.getInstance())
		.registerTypeAdapter(SurveyAssessment.class, AssessmentSerializer.getInstance())
		.create();

	@Override
	public String toJson(AbstractAssessment assessment) {
		return gson.toJson(assessment);
	}

	@Override
	public String toJson(List<?> assessments) {
		return gson.toJson(assessments);
	}

	@Override
	public AbstractAssessment fromJson(String json) {
		throw new NotImplementedException();
	}

	@Override
	public AbstractAssessment mergeJson(AbstractAssessment object, String json) {
		throw new NotImplementedException();
	}

	private static class AssessmentSerializer implements JsonSerializer<AbstractAssessment> {
		private static final AssessmentSerializer INSTANCE = new AssessmentSerializer();

		private AssessmentSerializer(){}
		public static AssessmentSerializer getInstance() {
			return INSTANCE;
		}

		@Override
		public JsonElement serialize(AbstractAssessment test, Type type, JsonSerializationContext jsc) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("$type", test.getClass().getSimpleName());
			jsonObject.addProperty("id", test.getId());
			jsonObject.addProperty("name", test.getName());
			return jsonObject;
		}
	}
}
