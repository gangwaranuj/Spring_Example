
package com.workmarket.web.controllers;

import com.workmarket.service.business.LinkedInService;
import com.workmarket.service.business.LinkedInServiceImpl;
import com.workmarket.service.business.ProfileService;
import org.apache.struts.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.workmarket.web.controllers.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
@RequestMapping("/oauth")
public class OauthController extends BaseController {
	
    @Autowired ProfileService profileService;

    @Autowired
    LinkedInService linkedInService;

	@RequestMapping(value="/linkedin", method=RequestMethod.GET)
	public String linkedin(@RequestParam("import") String importOption, @RequestParam("internal_callback") String interalCallback, HttpServletRequest request) throws UnsupportedEncodingException, MalformedURLException
    {

        URL url = RequestUtils.absoluteURL(request, "/oauth/linkedin_callback?import=" + importOption  + "&internal_callback=" + URLEncoder.encode(interalCallback, "UTF-8"));
        
        System.out.println(url.toExternalForm());

        String link = profileService.getLinkedInAuthorizationUrl(getCurrentUser().getId(), url.toExternalForm());
        
        System.out.println(link);

        return "redirect:" + link;
	}
	
	@RequestMapping(value="/linkedin_callback", method=RequestMethod.GET)
	public String linkedinCallback(@RequestParam("import") String importOption, @RequestParam("internal_callback") String internalCallback, @RequestParam("oauth_token") String authToken, @RequestParam("oauth_verifier") String oauthVerifier  ) throws UnsupportedEncodingException, LinkedInServiceImpl.LinkedInImportFailed
    {
        internalCallback = URLDecoder.decode(internalCallback, "UTF-8");

        Boolean authorized = profileService.authorizeLinkedIn(getCurrentUser().getId(), oauthVerifier);

        if (Boolean.TRUE.equals(authorized))
        {
            if ("1".equals(importOption))
            {
                System.out.println("importing linkedin data...");
                linkedInService.importPersonData(getCurrentUser().getId());
            }

        }

        return "redirect:" + internalCallback;
	}
	
}
	
