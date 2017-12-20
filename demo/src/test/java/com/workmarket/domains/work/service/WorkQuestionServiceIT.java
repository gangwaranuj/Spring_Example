package com.workmarket.domains.work.service;

import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkQuestionAnswerPair;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkQuestionServiceIT extends BaseServiceIT {
  @Autowired WorkQuestionService workQuestionService;

  @Test
  public void shouldReturnWorkId() throws Exception {
    final User user = newContractor();
    final Work work = newWork(user.getId());
    final WorkQuestionAnswerPair pair =
        workQuestionService.saveQuestion(work.getId(), user.getId(), "What is the capital of Canada?");

    final Long workId = workQuestionService.findWorkIdByQuestionId(pair.getId());

    assertNotNull(workId);
    assertEquals(work.getId(), workId);
  }
}