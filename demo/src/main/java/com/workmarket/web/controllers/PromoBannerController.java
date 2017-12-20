package com.workmarket.web.controllers;

import com.workmarket.service.business.UserService;
import com.workmarket.utility.CollectionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class PromoBannerController extends BaseController {

    @Autowired
    protected UserService userService;

    @RequestMapping(value={"/promo"},
            method=RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> getUserPromoStatus () {
        return CollectionUtilities.newObjectMap(
            "promoDismissed", userService.findPromoDismissed()
        );
    }

    @RequestMapping(value={"/promo"},
            method=RequestMethod.POST,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> updateUserPromoStatus (@RequestParam(value="dismissed", required=true) Integer dismissed) {
        userService.updatePromoDismissed(dismissed);
        return getUserPromoStatus();
    }
}
