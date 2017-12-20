
package com.workmarket.web.controllers;

import com.workmarket.service.business.AssetManagementService;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.web.exceptions.HttpException404;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping({"/asset", "/worker/v2/asset"})
public class AssetController extends BaseController {
	
	@Autowired private AssetManagementService assetService;
	
	@RequestMapping(value="/{uuid}", method=RequestMethod.GET)
	public String index(@PathVariable("uuid") String uuid) throws HostServiceException {
		String uri = assetService.getAuthorizedUriByUuid(uuid);
		if (uri == null)
			throw new HttpException404();
		return String.format("redirect:%s", uri);
	}
	
	@RequestMapping(value="/download/{uuid}", method=RequestMethod.GET)
	public String download(@PathVariable("uuid") String uuid) throws HostServiceException {
		String uri = assetService.getAuthorizedDownloadUriByUuid(uuid);
		if (uri == null) {
			throw new HttpException404();
		}
		return String.format("redirect:%s", uri);
	}

	@RequestMapping(value="/downloadTemp/{uuid}", method=RequestMethod.GET)
	public String downloadTemp(@PathVariable("uuid") String uuid) throws HostServiceException {
		String uri = assetService.getAuthorizedDownloadUriByUuidForTempUpload(uuid);
		if (uri == null) {
			throw new HttpException404();
		}
		return String.format("redirect:%s", uri);
	}

	@RequestMapping(value = "/downloadDefault/{uuid}", method = RequestMethod.GET)
	public String downloadImageOrDefault(@PathVariable("uuid") String uuid) throws HostServiceException {
		String uri = assetService.getAuthorizedDownloadUriByUuid(uuid);
		if (uri == null) {
			// todo: fix
			return String.format("redirect:%s", "/media/images/spacer.gif");
		}
		return String.format("redirect:%s", uri);
	}

}