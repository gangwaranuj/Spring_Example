package com.workmarket.web.controllers.admin;

import com.google.common.collect.Lists;
import com.workmarket.domains.velvetrope.model.Admission;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.velvetrope.service.AdmissionService;
import com.workmarket.service.business.PlanService;
import com.workmarket.velvetrope.Venue;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.admin.plans.PlanForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin/plans")
public class AdminPlansController extends BaseController {
	@Autowired private PlanService planService;
	@Autowired private AdmissionService admissionService;

	@RequestMapping(method = GET)
	public String index() {

		return "web/pages/admin/plans/index";
	}

	@RequestMapping(
		value = "/list",
		method = GET)
	public @ResponseBody List<PlanForm> list() {
		List<PlanForm> forms = Lists.newArrayList();
		List<Plan> plans = planService.getAllPlans();

		// TODO[Jim]: Naive implementation. Minimize rounds trips to db.
		for (Plan plan: plans) {
			List<Admission> admissions = admissionService.findAllAdmissionsForPlanId(plan.getId());

			forms.add(new PlanForm(plan, admissions));
		}

		return forms;
	}

	@RequestMapping(
		value = "/list",
		method = POST)
	public @ResponseBody Plan create(@RequestBody PlanForm form) {
		Plan plan = planService.save(form.getPlan());

		admissionService.saveAdmissionsForPlanId(plan.getId(), form.getAdmissions());

		return plan;
	}

	@RequestMapping(
		value = "/list/{id}",
		method = POST,
		headers = "X-HTTP-Method-Override=PUT")
	@ResponseStatus(value = OK)
	public void update(@PathVariable Long id, @RequestBody PlanForm form) {
		planService.update(form.getPlan());
		admissionService.saveAdmissionsForPlanId(id, form.getAdmissions());
	}

	@RequestMapping(
		value = "/list/{id}",
		method = POST,
		headers = "X-HTTP-Method-Override=DELETE")
	public void destroy(@PathVariable Long id) {
		planService.destroy(id);
		admissionService.destroyAdmissionsForPlanId(id);
	}

	@RequestMapping(
		value = "/venues",
		method = GET)
	public @ResponseBody Venue[] venues() {

		return Venue.values();
	}
}
