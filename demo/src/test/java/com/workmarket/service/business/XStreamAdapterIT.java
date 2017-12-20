package com.workmarket.service.business;

import com.workmarket.domains.model.voice.twilio.TwilioXmlRestResponse;
import com.workmarket.test.IntegrationTest;
import com.workmarket.xml.XStreamXmlAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class XStreamAdapterIT extends BaseServiceIT {

	public static class Person {
		private String firstName;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
	}

	@Test
	public void test_toXml() throws Exception {
		XStreamXmlAdapter adapter = new XStreamXmlAdapter();

		adapter.getXStream().alias(Person.class.getSimpleName(), Person.class);

		Person person = new Person();
		person.setFirstName("Andy");

		String xml = adapter.toXml(person);

		Assert.assertEquals("<Person>\n" +
				"  <firstName>Andy</firstName>\n" +
				"</Person>", xml);

		Person p = (Person) adapter.fromXml(xml);

		Assert.assertEquals("Andy", p.getFirstName());
	}

	@Test
	public void test_fromTwilioXmlResponse() throws Exception {
		XStreamXmlAdapter adapter = new XStreamXmlAdapter();

		TwilioXmlRestResponse response = (TwilioXmlRestResponse) adapter.fromXml("<?xml version=\"1.0\"?><TwilioResponse><Call><Sid>CAcbad1e36030f8990a26e75a525e2124b</Sid><DateCreated>Mon, 03 Jan 2011 16:37:21 +0000</DateCreated><DateUpdated>Mon, 03 Jan 2011 16:37:21 +0000</DateUpdated><ParentCallSid/><AccountSid>AC3b74443879729546c02f29261990fe33</AccountSid><To>+16466443604</To><From>+16466443604</From><PhoneNumberSid>PNf476d17d735f2bc9616e3eb7238297a5</PhoneNumberSid><Status>queued</Status><StartTime/><EndTime/><Duration/><Price/><Direction>outbound-api</Direction><AnsweredBy/><ApiVersion>2010-04-01</ApiVersion><Annotation/><ForwardedFrom/><GroupSid/><CallerName/><Uri>/2010-04-01/Accounts/AC3b74443879729546c02f29261990fe33/Calls/CAcbad1e36030f8990a26e75a525e2124b</Uri><SubresourceUris><Notifications>/2010-04-01/Accounts/AC3b74443879729546c02f29261990fe33/Calls/CAcbad1e36030f8990a26e75a525e2124b/Notifications</Notifications><Recordings>/2010-04-01/Accounts/AC3b74443879729546c02f29261990fe33/Calls/CAcbad1e36030f8990a26e75a525e2124b/Recordings</Recordings></SubresourceUris></Call></TwilioResponse>");

		Assert.assertEquals("CAcbad1e36030f8990a26e75a525e2124b", response.getCall().getSid());
	}
}

