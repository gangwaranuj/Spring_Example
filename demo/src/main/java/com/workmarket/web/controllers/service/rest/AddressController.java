
package com.workmarket.web.controllers.service.rest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.service.business.AddressService;
import com.workmarket.dto.AddressDTO;
import com.workmarket.service.business.dto.AddressVerificationDTO;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.workmarket.web.controllers.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/service/rest/address")
public class AddressController extends BaseController {

	@Autowired private AddressService addressService;

	@RequestMapping(value="/verify", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public void verify(Model model, HttpServletRequest httpRequest) throws Exception {

		Map<String, Object> response = Maps.newHashMap();

		if (httpRequest.getParameter("address").isEmpty()) {
			response.put("successful", false);
		} else {
			AddressVerificationDTO dto = addressService.verify(httpRequest.getParameter("address"));

			List<Object> components = Lists.newArrayList();

			for (AddressDTO addressDTO : dto.getComponentMatches()) {
				Map<String, Object> component = CollectionUtilities.newObjectMap(
					"address1", addressDTO.getAddress1(),
					"address2", addressDTO.getAddress2(),
					"city", addressDTO.getCity(),
					"state", addressDTO.getState(),
					"postal_code", addressDTO.getPostalCode(),
					"country", addressDTO.getCountry()
				);

				components.add(component);
			}

			response.put("successful", true);
			response.put("verified", dto.isVerified());
			response.put("matches", dto.getMatches());
			response.put("components", components);
		}

		model.addAttribute("response", response);
	}

}
