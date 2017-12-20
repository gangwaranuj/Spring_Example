package com.workmarket.web.controllers.admin;

import com.workmarket.dto.VendorSuggestionDTO;
import com.workmarket.service.infra.business.SuggestionService;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.dto.UserSuggestionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/suggest")
public class SuggestionController {

	@Autowired private SuggestionService suggest;

	@RequestMapping("/user")
	@ResponseBody
	public List<UserSuggestionDTO> suggestUsers(@RequestParam("term") String query) {
		return suggest.suggestUser(query);
	}

	@RequestMapping("/company")
	@ResponseBody
	public List<SuggestionDTO> suggestCompanies(@RequestParam("term") String query) {
		return suggest.suggestCompany(query);
	}

	@RequestMapping("/skill")
	@ResponseBody
	public List<SuggestionDTO> suggestSkill(@RequestParam("term") String query) {
		return suggest.suggestSkills(query);
	}

	@RequestMapping("/tool")
	@ResponseBody
	public List<SuggestionDTO> suggestProduct(@RequestParam("term") String query, @RequestParam Long industryId) {
		return suggest.suggestTools(query);
	}

	@RequestMapping("/specialty")
	@ResponseBody
	public List<SuggestionDTO> suggestSpecialty(@RequestParam("term") String query, @RequestParam Long industryId) {
		return suggest.suggestSpecialties(query);
	}

	@RequestMapping("/vendor")
	@ResponseBody
	public List<VendorSuggestionDTO> suggestVendor(@RequestParam("term") String query) {
		return suggest.suggestVendor(query);
	}
}
