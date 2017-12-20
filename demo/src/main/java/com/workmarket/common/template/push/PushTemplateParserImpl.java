package com.workmarket.common.template.push;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ianha on 1/23/15.
 */
@Service
public class PushTemplateParserImpl implements PushTemplateParser {
    /**
     * The regex below matches against the push template's which
     * have the following pattern:
     * {
     *   "message":"messagevalue",
     *   "action":"actionvalue"
     * }
     */
    private static final String PUSH_TEMPLATE_REGEX_PATTERN = "\\s*\\{\\s*\"message\"\\s*\\:\\s*\\\"(.*)\\\"\\s*,\\s*\\\"action\"\\s*:\\s*\\\"(.*)\\\"\\s*\\}";
    private static final Pattern pattern = Pattern.compile(PUSH_TEMPLATE_REGEX_PATTERN, Pattern.DOTALL);

    @Override
    public String parseMessage(String pushTemplateAsString) {
        List<String> tokens = parseTokens(pushTemplateAsString);

        return tokens.isEmpty() ? "" : tokens.get(0);
    }

    @Override
    public String parseAction(String pushTemplateAsString) {
        List<String> tokens = parseTokens(pushTemplateAsString);

        return tokens.isEmpty() ? "" : tokens.get(1);
    }

    private List<String> parseTokens(String pushTemplateAsString) {
        Matcher matcher = pattern.matcher(pushTemplateAsString);
        List<String> tokens = new ArrayList<>();

        if (matcher.find()) {
            // The Matcher class bundles the original string into the result set, so just
            // ignore the first index value
            for (int i = 1; i < matcher.groupCount() + 1; i++) {
                tokens.add(matcher.group(i));
            }
        }

        return tokens;
    }
}

