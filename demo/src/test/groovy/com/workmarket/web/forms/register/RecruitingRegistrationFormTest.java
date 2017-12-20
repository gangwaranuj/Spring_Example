package com.workmarket.web.forms.register;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class RecruitingRegistrationFormTest {

    @Test
    public void testSerialization() {
        Serializable original = new RecruitingRegistrationForm();
        Serializable copy = (Serializable) SerializationUtils.clone(original);
        assertEquals(original, copy);
    }

}