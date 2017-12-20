package com.workmarket.web.controllers.settings;

import com.workmarket.biz.gen.Messages.WMFormat;
import com.workmarket.biz.gen.Messages.WMLocale;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.featuretoggle.FeatureEntitlementService;
import com.workmarket.service.locale.LocaleService;
import com.workmarket.web.controllers.BaseController;
import com.workmarket.web.forms.LanguageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/settings/manage/language")
public class SettingsLanguageController extends BaseController {

    @Autowired private LocaleService localeService;
    @Autowired private UserService userService;
    @Autowired private FeatureEntitlementService featureEntitlementService;

    @RequestMapping(method = GET)
    public String displayLanguage(Model model) {
        final boolean hasLocaleFeature = featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "locale");

        if (!hasLocaleFeature) {
            return "web/pages/error/404";
        }

        LanguageForm languageForm = new LanguageForm();
        User user = userService.findUserById(getCurrentUser().getId());
        final WMFormat preferredFormat = localeService.getPreferredFormat(user.getUuid());
        final WMLocale preferredLocale = localeService.getPreferredLocale(user.getUuid());

        languageForm.setFormat(preferredFormat.getCode());
        languageForm.setLocale(preferredLocale.getCode());

        final List<WMFormat> supportedFormats = localeService.getSupportedFormats();
        model.addAttribute("supportedFormatsList", supportedFormats);

        final List<WMLocale> supportedLocales = localeService.getSupportedLocale();
        model.addAttribute("supportedLocalesList", supportedLocales);

        model.addAttribute("languageForm", languageForm);
        return "web/pages/settings/manage/language";
    }

    @RequestMapping(method = POST)
    public String index(@Valid @ModelAttribute("languageForm") LanguageForm form,
      BindingResult bind,
      RedirectAttributes flash,
      Model model) {
        final boolean hasLocaleFeature = featureEntitlementService.hasFeatureToggle(getCurrentUser().getId(), "locale");

        if (!hasLocaleFeature) {
            return "web/pages/error/404";
        }

        localeService.setPreferredFormat(getCurrentUser().getUuid(), form.getFormat());
        localeService.setPreferredLocale(getCurrentUser().getUuid(), form.getLocale());

        LanguageForm languageForm = new LanguageForm();
        User user = userService.findUserById(getCurrentUser().getId());
        final WMFormat preferredFormat = localeService.getPreferredFormat(user.getUuid());
        final WMLocale preferredLocale = localeService.getPreferredLocale(user.getUuid());

        languageForm.setFormat(preferredFormat.getCode());
        languageForm.setLocale(preferredLocale.getCode());

        final List<WMFormat> supportedFormats = localeService.getSupportedFormats();
        model.addAttribute("supportedFormatsList", supportedFormats);

        final List<WMLocale> supportedLocales = localeService.getSupportedLocale();
        model.addAttribute("supportedLocalesList", supportedLocales);

        model.addAttribute("languageForm", languageForm);
        return "redirect:/settings/manage/language";
    }
}
