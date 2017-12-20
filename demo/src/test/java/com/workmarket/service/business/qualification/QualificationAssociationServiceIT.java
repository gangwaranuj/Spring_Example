package com.workmarket.service.business.qualification;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.qualification.UserToQualification;
import com.workmarket.domains.model.qualification.WorkToQualification;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.qualification.Qualification;
import com.workmarket.search.qualification.QualificationBuilder;
import com.workmarket.search.qualification.QualificationType;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for qualification association service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class QualificationAssociationServiceIT extends BaseServiceIT {

    private static final QualificationBuilder QUALIFICATION_BUILDER = Qualification.builder()
        .setName("name")
        .setUuid("uuid")
        .setQualificationType(QualificationType.skill);
    private static final Qualification QUALIFICATION1 = QUALIFICATION_BUILDER
        .setUuid("uuid1")
        .setName("name1")
        .setQualificationType(QualificationType.skill)
        .build();
    private static final Qualification QUALIFICATION2 = QUALIFICATION_BUILDER
        .setUuid("uuid2")
        .setName("name2")
        .setQualificationType(QualificationType.skill)
        .build();
    private static final Qualification QUALIFICATION3 = QUALIFICATION_BUILDER
        .setUuid("uuid3")
        .setName("name3")
        .setQualificationType(QualificationType.skill)
        .build();
    private static final Long USER_ID = 123L;
    private static final Long WORK_ID = 456L;

    @Autowired private QualificationAssociationService qualificationAssociationService;

    @Test(expected = IllegalArgumentException.class)
    public void test_addNullQualificationToUser() throws Exception {
        qualificationAssociationService.saveOrUpdate(null);
    }

    @Test
    public void test_addQualificationToUser() throws Exception {
        final String newSkillUuid = "new-skill-uuid";
        final User contractor = newContractorIndependent();
        final UserToQualification userToQualification =
            new UserToQualification(contractor.getId(), newSkillUuid, QualificationType.skill);
        qualificationAssociationService.saveOrUpdate(userToQualification);
        final List<UserToQualification> userToQualifications =
            qualificationAssociationService.findUserQualifications(contractor.getId(), QualificationType.skill, false);
        assertEquals(1, userToQualifications.size());
        assertEquals(newSkillUuid, userToQualifications.get(0).getQualificationUuid());
    }

    @Test
    public void test_addJobTitleToUser() throws Exception {
        final String oldJobTitleUuid = "old-job-title-uuid";
        final String newJobTitleUuid = "new-job-title-uuid";
        final User contractor = newContractorIndependent();
        final UserToQualification userToOldJobTitle =
            new UserToQualification(contractor.getId(), oldJobTitleUuid, QualificationType.job_title);
        qualificationAssociationService.saveOrUpdate(userToOldJobTitle);
        List<UserToQualification> userToQualifications =
            qualificationAssociationService.findUserQualifications(contractor.getId(), QualificationType.job_title, true);
        assertEquals(1, userToQualifications.size());
        assertEquals(oldJobTitleUuid, userToQualifications.get(0).getQualificationUuid());

        final UserToQualification userToNewJobTitle =
            new UserToQualification(contractor.getId(), newJobTitleUuid, QualificationType.job_title);
        qualificationAssociationService.saveOrUpdate(userToNewJobTitle);
        userToQualifications =
            qualificationAssociationService.findUserQualifications(contractor.getId(), QualificationType.job_title, true);
        assertEquals(2, userToQualifications.size());
        for (final UserToQualification utq : userToQualifications) {
            String uuid = utq.getQualificationUuid();
            assertTrue(uuid.equals(oldJobTitleUuid) || uuid.equals(newJobTitleUuid));
            if (uuid.equals(oldJobTitleUuid)) {
                assertTrue(utq.getDeleted());
            } else {
                assertFalse(utq.getDeleted());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setUserQualificationsWithNullUserId() throws Exception {
        final Qualification qualification = QUALIFICATION_BUILDER.build();
        qualificationAssociationService.setUserQualifications(
            null, QualificationType.skill, Lists.newArrayList(qualification));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setUserQualificationsWithEmptyListOFQualifications() throws Exception {
        qualificationAssociationService.setUserQualifications(
            USER_ID, QualificationType.skill, Lists.<Qualification>newArrayList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setUserQualificationsWithJobTitleType() throws Exception {
        final Qualification qualification = QUALIFICATION_BUILDER.build();
        qualificationAssociationService.setUserQualifications(
            USER_ID, QualificationType.job_title, Lists.newArrayList(qualification));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setUserQualificationsWithJobTitleQualification() throws Exception {
        final Qualification qualification = QUALIFICATION_BUILDER
            .setQualificationType(QualificationType.job_title)
            .build();
        qualificationAssociationService.setUserQualifications(
            USER_ID, QualificationType.skill, Lists.newArrayList(qualification));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findQualificationsWithNullUserId() throws Exception {
        qualificationAssociationService.findUserQualifications(null, QualificationType.skill, true);
    }

    @Test
    public void test_setAListOfQualificationsToUser() throws Exception {
        final User contractor = newContractorIndependent();
        qualificationAssociationService.setUserQualifications(
            contractor.getId(), QualificationType.skill, Lists.newArrayList(QUALIFICATION1, QUALIFICATION2));
        List<UserToQualification> userToQualifications =
            qualificationAssociationService.findUserQualifications(contractor.getId(), QualificationType.skill, true);
        assertEquals(2, userToQualifications.size());
        for (final UserToQualification utq : userToQualifications) {
            assertTrue(utq.getQualificationUuid().equals(QUALIFICATION1.getUuid())
                || utq.getQualificationUuid().equals(QUALIFICATION2.getUuid()));
        }

        // reset user qualifications with qualification2 removed from the list, therefore, it should be set to deleted
        qualificationAssociationService.setUserQualifications(
            contractor.getId(), QualificationType.skill, Lists.newArrayList(QUALIFICATION1, QUALIFICATION3));
        List<UserToQualification> updatedUserToQualificationsWithDelete =
            qualificationAssociationService.findUserQualifications(contractor.getId(), QualificationType.skill, true);
        assertEquals(3, updatedUserToQualificationsWithDelete.size());
        for (final UserToQualification utq : updatedUserToQualificationsWithDelete) {
            assertTrue(utq.getQualificationUuid().equals(QUALIFICATION1.getUuid())
                || utq.getQualificationUuid().equals(QUALIFICATION2.getUuid())
                || utq.getQualificationUuid().equals(QUALIFICATION3.getUuid()));
            if (utq.getQualificationUuid().equals(QUALIFICATION1.getUuid())
                || utq.getQualificationUuid().equals(QUALIFICATION3.getUuid())) {
                assertFalse(utq.getDeleted());
            } else {
                assertTrue(utq.getDeleted());
            }
        }

        List<UserToQualification> updatedUserToQualificationsWithoutDelete =
            qualificationAssociationService.findUserQualifications(contractor.getId(), QualificationType.skill, false);
        assertEquals(2, updatedUserToQualificationsWithoutDelete.size());
        for (final UserToQualification utq : updatedUserToQualificationsWithoutDelete) {
            assertTrue(utq.getQualificationUuid().equals(QUALIFICATION1.getUuid())
                || utq.getQualificationUuid().equals(QUALIFICATION3.getUuid()));
            assertFalse(utq.getQualificationUuid().equals(QUALIFICATION2.getUuid()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setWorkQualificationsWithNullWorkId() throws Exception {
        qualificationAssociationService.setWorkQualifications(
            null, USER_ID, QualificationType.skill, Lists.newArrayList(QUALIFICATION_BUILDER.build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setWorkQualificationsWithNullUserId() throws Exception {
        qualificationAssociationService.setWorkQualifications(
            WORK_ID, null, QualificationType.skill, Lists.newArrayList(QUALIFICATION_BUILDER.build()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_setWorkQualificationsWithEmptyQualificationList() throws Exception {
        qualificationAssociationService.setWorkQualifications(
            WORK_ID, USER_ID, QualificationType.skill, Lists.<Qualification>newArrayList());
    }

    @Test
    public void test_setWorkQualifications() throws Exception {
        final User contractor = newContractorIndependent();
        final Work work = newWork(contractor.getId(), new WorkDTO());

        qualificationAssociationService.setWorkQualifications(
            work.getId(), contractor.getId(), QualificationType.skill, Lists.newArrayList(QUALIFICATION1, QUALIFICATION2));
        List<WorkToQualification> workToQualifications =
            qualificationAssociationService.findWorkQualifications(work.getId(), QualificationType.skill, true);
        assertEquals(2, workToQualifications.size());
        for (final WorkToQualification wtq : workToQualifications) {
            assertTrue(wtq.getQualificationUuid().equals(QUALIFICATION1.getUuid())
                || wtq.getQualificationUuid().equals(QUALIFICATION2.getUuid()));
        }

        qualificationAssociationService.setWorkQualifications(
            work.getId(), contractor.getId(), QualificationType.skill, Lists.newArrayList(QUALIFICATION1, QUALIFICATION3));
        List<WorkToQualification> updatedWorkToQualifications =
            qualificationAssociationService.findWorkQualifications(work.getId(), QualificationType.skill, false);
        assertEquals(2, updatedWorkToQualifications.size());
        for (final WorkToQualification wtq : updatedWorkToQualifications) {
            assertTrue(wtq.getQualificationUuid().equals(QUALIFICATION1.getUuid())
                || wtq.getQualificationUuid().equals(QUALIFICATION3.getUuid()));
            assertFalse(wtq.getQualificationUuid().equals(QUALIFICATION2.getUuid()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_findWorkQualificationsWithNullWorkId() throws Exception {
        qualificationAssociationService.findWorkQualifications(null, QualificationType.skill, true);
    }
}
