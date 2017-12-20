package com.workmarket.common.template.voice;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class VoiceTemplateFactoryImpl implements VoiceTemplateFactory {

    @Override
    public VoiceTemplate buildVoiceTemplate(String toNumber, String msg) {
        Assert.notNull(toNumber);
        Assert.isTrue(toNumber.length() > 7);
        Assert.notNull(msg);

        return new VoiceTemplate(toNumber, msg);
    }
}
