package com.workmarket.web.controllers.lms.admin;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.thrift.assessment.Assessment;
import com.workmarket.thrift.assessment.Item;
import com.workmarket.thrift.assessment.ItemRemoveRequest;
import com.workmarket.thrift.assessment.ItemReorderRequest;
import com.workmarket.thrift.assessment.ItemSaveRequest;
import com.workmarket.thrift.assessment.ItemType;
import com.workmarket.thrift.core.Asset;
import com.workmarket.thrift.core.ConstraintViolation;
import com.workmarket.thrift.core.ValidationException;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.MimeTypeUtilities;
import com.workmarket.web.editors.NullSafeNumberEditor;
import com.workmarket.web.editors.ThriftEnumEditor;
import com.workmarket.web.editors.ThriftObjectEditor;
import com.workmarket.web.helpers.AjaxResponseBuilder;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.MessageBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/lms/manage")
public class AssessmentItemFormController extends BaseAssessmentAdminController {

	@Autowired JsonSerializationService jsonService;

	@InitBinder("item")
	private void initItemBinder(WebDataBinder binder) {
		binder.setIgnoreInvalidFields(true);
		binder.registerCustomEditor(int.class, new NullSafeNumberEditor(Integer.class));
		binder.registerCustomEditor(long.class, new NullSafeNumberEditor(Long.class));
		binder.registerCustomEditor(Asset.class, new ThriftObjectEditor(Asset.class));
		binder.registerCustomEditor(ItemType.class, new ThriftEnumEditor(ItemType.class));
	}

	@RequestMapping(
		value="/step2/{assessmentId}",
		method = GET)
	public String step2(@PathVariable("assessmentId") Long id, Model model) throws Exception {
		Assessment assessment = getAssessment(id);
		model.addAttribute("assessment", assessment);
		model.addAttribute("assessmentItems", jsonService.toJson(convertItemsToMap(assessment.getItems())));
		model.addAttribute("uploadTypes", jsonService.toJson(MimeTypeUtilities.getMimeTypesForPage("/lms/manage/step2")));

		return "web/pages/lms/manage/step2";
	}

	@RequestMapping(
		value = "/save_question/{assessmentId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder saveQuestion(
		@PathVariable("assessmentId") Long id,
		@RequestParam(value="notGraded", defaultValue="0") boolean notGraded,
		Item item,
		MessageBundle messages) {

		Assessment assessment = getAssessment(id);

		// A little cleanup to get around Thrift's annoying-ness
		// Not sure yet why the `id` is being set.
		if (item.getId() == 0)
			item.setId(0L);
		// The UI provides a "Not Graded" checkbox. We need the inverse.
		item.setGraded(!notGraded);

		ItemSaveRequest request = new ItemSaveRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(assessment.getId())
			.setItem(item);

		try {
			Item savedItem = thriftAssessmentService.addOrUpdateItem(request);
			return new AjaxResponseBuilder()
				.setSuccessful(true)
				.addData("question", convertItemToMap(savedItem));
		} catch (ValidationException e) {
			BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult("lms");
			for (ConstraintViolation v : e.getErrors()) {
				ThriftValidationMessageHelper.rejectViolation(v, bindingResult);
			}
			messageHelper.setErrors(messages, bindingResult);

			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.setMessages(messages.getAllMessages());
		} catch (Exception e) {
			return new AjaxResponseBuilder()
				.setSuccessful(false)
				.addMessage("There was an error");
		}
	}

	@RequestMapping(
		value = "/remove_question/{assessmentId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder removeQuestion(
		@PathVariable("assessmentId") Long id,
		@RequestParam("question_id") Long itemId) {

		Assessment assessment = getAssessment(id);

		Item item = new Item().setId(itemId);
		ItemRemoveRequest request = new ItemRemoveRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(assessment.getId())
			.setItem(item);

		try {
			thriftAssessmentService.removeItem(request);
			return new AjaxResponseBuilder().setSuccessful(true);
		} catch (Exception e) {
			return new AjaxResponseBuilder().setSuccessful(false);
		}
	}

	@RequestMapping(
		value = "/reorder_questions/{assessmentId}",
		method = POST)
	@ResponseBody
	public AjaxResponseBuilder reorderQuestions(
		@PathVariable("assessmentId") Long id,
		@RequestParam("question_ids") List<Long> itemIds) {
		Assessment assessment = getAssessment(id);

		ItemReorderRequest request = new ItemReorderRequest()
			.setUserId(getCurrentUser().getId())
			.setAssessmentId(assessment.getId());

		for (Long itemId : itemIds)
			request.addToItems(new Item().setId(itemId));

		try {
			thriftAssessmentService.reorderItems(request);
			return new AjaxResponseBuilder().setSuccessful(true);
		} catch (Exception e) {
			return new AjaxResponseBuilder().setSuccessful(false);
		}
	}

	/**
	 * Convert assessment items to a map for controlled JSON serialization
	 * to consistently represent the <code>ItemType</code> enum.
	 * The Thrift JSON serializer represents the enum as an <code>int</code>using <code>getValue()</code>
	 * whereas the <code>MappingJacksonJsonView</code> serializes the enum to a <code>String</code> via
	 * the <code>name()</code> method.
	 *
	 * We want the behavior of the Thrift serializer.
	 */
	private List<Map<String,Object>> convertItemsToMap(List<Item> items) {
		if (items == null)
			return Lists.newArrayList();
		return Lists.transform(items, new Function<Item,Map<String,Object>>() {
			@Override
			public Map<String,Object> apply(@Nullable Item from) {
				return convertItemToMap(from);
			}
		});
	}

	private Map<String,Object> convertItemToMap(Item item) {
		return CollectionUtilities.newObjectMap(
			"id", item.getId(),
			"position", item.getPosition(),
			"prompt", item.getPrompt(),
			"description", item.getDescription(),
			"hint", item.getHint(),
			"type", item.getType().getValue(),
			"otherAllowed", item.isOtherAllowed(),
			"maxLength", item.getMaxLength(),
			"graded", item.isGraded(),
			"manuallyGraded", item.isManuallyGraded(),
			"incorrectFeedback", item.getIncorrectFeedback(),
			"links", item.getLinks(),
			"assets", item.getAssets(),
			"uploads", item.getUploads(),
			"choices", item.getChoices()
		);
	}
}
