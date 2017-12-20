package com.workmarket.common.template.push;

/**
 * Parse generated push notification templates. Before, we were parsing the push
 * template into a JSON object, which was causing problems. This class does
 * away from doing that by parsing the template directly.
 *
 * Created by ianha on 1/23/15.
 */
public interface PushTemplateParser {
    /**
     * Return the value of the key "message" in the push template.
     *
     * @param pushTemplateAsString
     * @return Empty string on not found
     */
    String parseMessage(String pushTemplateAsString);

    /**
     * Return the value of the key "action" in the push template.
     * @param pushTemplateAsString
     * @return Empty string on not found
     */
    String parseAction(String pushTemplateAsString);
}
