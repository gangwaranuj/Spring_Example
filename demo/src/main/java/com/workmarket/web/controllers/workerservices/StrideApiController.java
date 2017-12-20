package com.workmarket.web.controllers.workerservices;

import com.workmarket.web.controllers.BaseController;
import com.workmarket.service.business.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.nio.charset.Charset;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.codec.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

@Controller
@RequestMapping("/stride")
public class StrideApiController extends BaseController {

	@Autowired
	protected UserService userService;

	private final String STRIDEHEALTH_URL = "https://api.stridehealth.com/1.0/partner";
	private final String STRIDEHEALTH_KEY = "vAhofnIjaVdAihIrHocyesJejrylDeRgiaGdyBbXUOhSFdwWnC";
	private final String STRIDEHEALTH_SALT = "klourkoAdyaPmeCARscz";
	private final String STRIDEHEALTH_GATEWAY = "https://workmarket.stridehealth.com/partner-verify";

	private static final Log logger = LogFactory.getLog(StrideApiController.class);

	@RequestMapping(value={"/user"},
		method=RequestMethod.POST,
		produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String provisionUser () {

		String id = getCurrentUser().getId().toString();
		String email = STRIDEHEALTH_SALT + getCurrentUser().getEmail();
		String accessCodeRaw = getAccessCode();
		String accessCode = accessCodeRaw.substring(accessCodeRaw.indexOf("\"") + 1, accessCodeRaw.indexOf("\"", 2));
		String emailHash;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(email.getBytes("UTF-8"));
			byte[] digest = md.digest();
			digest = Base64.encode(digest);
			emailHash = new String(digest, Charset.forName("UTF-8"));

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(STRIDEHEALTH_URL + "/users");

			post.setHeader("X-Api-Token", STRIDEHEALTH_KEY);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");

			JSONObject data = new JSONObject();
			data.put("emailHash", emailHash);
			data.put("accessCode", accessCode);
			data.put("userId", id);

			StringEntity se = new StringEntity(data.toString());
			post.setEntity(se);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			return(result.toString());
		}
		catch (Exception e) {
			logger.error("There was an error creating a new user", e);
			return("{\"error\": \"" + e.toString() + "\"}");
		}

	}

	@RequestMapping(value={"/user"},
		method=RequestMethod.GET,
		produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getUsers (@RequestParam(value="id", required=false) Long id) {

		String url = STRIDEHEALTH_URL + "/users";

		try {
			HttpClient client = HttpClientBuilder.create().build();
			if (id != null) {
				url += "?userId=" + id;
			}
			HttpGet request = new HttpGet(url);
			request.setHeader("X-Api-Token", STRIDEHEALTH_KEY);
			HttpResponse response = client.execute(request);

			long status = response.getStatusLine().getStatusCode();

			if (status == 200) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				return (result.toString());
			}
			else {
				return ("{\"userNotRegistered\": true}");
			}
		}
		catch (Exception e) {
			logger.error("There was an error getting the user", e);
			return("{\"error\": \"" + e.toString() + "\"}");
		}
	}

	@RequestMapping(value={"/user/active"},
		method=RequestMethod.GET,
		produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getActiveUser () {
			long id = getCurrentUser().getId();
			return getUsers(id);
	}

	@RequestMapping(value={"/user/url"},
		method=RequestMethod.GET,
		produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getUserUrl () {
		String zip = getCurrentUser().getPostalCode();
		return buildUrl("/onboarding/location?zipcode=" + zip);
	}

	@RequestMapping(value={"/user/guideurl"},
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getGuideUrl () {
		return buildUrl("/health-insurance/guide");
	}

	@RequestMapping(value={"/user/promo"},
		method=RequestMethod.GET,
		produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getUserPromoStatus () {
		return ("{\"promoDismissed\":" + userService.findPromoDismissed() + "}");
	}

	@RequestMapping(value={"/user/promo"},
		method=RequestMethod.POST,
		produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String updateUserPromoStatus (@RequestParam(value="dismissed", required=true) Integer dismissed) {
		userService.updatePromoDismissed(dismissed);
		return getUserPromoStatus();
	}


	public String buildUrl (String path) {
		String strideBaseUrl = STRIDEHEALTH_GATEWAY;
		try {
			JSONObject strideData = new JSONObject(getUsers(getCurrentUser().getId()));
			String accessCode = strideData.getString("accessCode");
			String email = URLEncoder.encode(getCurrentUser().getEmail());
			String userUrl = strideBaseUrl + "?accessCode=" + accessCode + "&email=" + email  + "&path=" + path;

			return("{\"url\": \"" + userUrl + "\"}");
		}
		catch (Exception e) {
			logger.error("There was an error building the user URL", e);
			return("{\"error\": \"" + e.toString() + "\"}");
		}
	}

	public String getAccessCode () {

		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(STRIDEHEALTH_URL + "/accessCodes?count=1");
			request.setHeader("X-Api-Token", STRIDEHEALTH_KEY);
			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			return(result.toString());
		}
		catch (Exception e) {
			logger.error("There was an error getting an access code", e);
			return("{\"error\": \"" + e.toString() + "\"}");
		}
	}
}
