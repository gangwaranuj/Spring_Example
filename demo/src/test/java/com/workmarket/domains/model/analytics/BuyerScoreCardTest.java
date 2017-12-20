package com.workmarket.domains.model.analytics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuyerScoreCardTest {

	private BuyerScoreCard buyerScoreCard;

	@Before
	public void setUp() throws Exception {
		buyerScoreCard = new BuyerScoreCard();
	}

	@Test
	public void testGetDateIntervalQualifier_with_AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS_success() throws Exception {
		buyerScoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS, new ScoreCard.DateIntervalData().setNet30(1));
		assertEquals(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS).getNet30Score(), ScoreCardQualifier.GOOD);

		buyerScoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS, new ScoreCard.DateIntervalData().setNet30(8));
		assertEquals(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS).getNet30Score(), ScoreCardQualifier.NEUTRAL);

		buyerScoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS, new ScoreCard.DateIntervalData().setNet30(17));
		assertEquals(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS).getNet30Score(), ScoreCardQualifier.BAD);
		assertTrue(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_APPROVE_WORK_IN_DAYS).getNet30Score().isBad());
	}

	@Test
	public void testGetDateIntervalQualifier_with_AVERAGE_TIME_TO_PAY_WORK_IN_DAYS_success() throws Exception {
		buyerScoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS, new ScoreCard.DateIntervalData().setNet30(-1));
		assertEquals(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS).getNet30Score(), ScoreCardQualifier.GOOD);

		buyerScoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS, new ScoreCard.DateIntervalData().setNet30(0));
		assertEquals(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS).getNet30Score(), ScoreCardQualifier.NEUTRAL);

		buyerScoreCard.addToValues(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS, new ScoreCard.DateIntervalData().setNet30(9));
		assertEquals(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS).getNet30Score(), ScoreCardQualifier.BAD);
		assertTrue(buyerScoreCard.getValueForField(BuyerScoreField.AVERAGE_TIME_TO_PAY_WORK_IN_DAYS).getNet30Score().isBad());
	}
}