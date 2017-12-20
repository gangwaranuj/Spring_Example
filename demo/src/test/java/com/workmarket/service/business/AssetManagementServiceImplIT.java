package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.DeliverableRequirement;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.DeliverableService;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AssetManagementServiceImplIT extends BaseServiceIT {

    @Autowired protected DeliverableService deliverableService;

    @Test
    public void testRemoveDeliverablesAtPositionFromWork() throws Exception {

        User user = newFirstEmployee();
        Work work = newWork(user.getId());
        for (int i = 1; i < 3; i++) {
            deliverableService.addDeliverable(work.getWorkNumber(), newDeliverableAssetDTO(1L, 1));
        }

        assertEquals(deliverableService.findAllAssetAssociationsByDeliverableRequirementIdAndPosition(work.getId(), 1L, 1).size(), 2);

        deliverableService.removeDeliverablesAtPositionFromWork(work.getId(), 1L, 1);

        assertEquals(deliverableService.findAllAssetAssociationsByDeliverableRequirementIdAndPosition(work.getId(), 1L, 1).size(), 0);
    }

    /**
     * Deliverable Group deadline should be be activated until work is assigned
     */
    @Test
    @Transactional
    public void createWork_WithDeliverables_DeadlineInactiveUntilAssigned() throws Exception {

        User buyer = newEmployeeWithCashBalance();
        User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
        authenticationService.setCurrentUser(buyer);

        laneService.addUserToCompanyLane3(contractor.getId(), buyer.getCompany().getId());

        // Create Work with 3 deliverable requirements
        int numRequirements = 3;
        int hoursToCompleteRequirements = 24;
        Work work = newWorkWithDeliverableRequirements(buyer.getId(), numRequirements, hoursToCompleteRequirements);

        // DeliverableRequirementGroup should not have an active deadline until work is assigned
        assertEquals(false, work.getDeliverableRequirementGroup().isDeadlineActive());
    }

    /**
     * Deliverable Group deadline should be activated when work is assigned
     */
    @Test
    @Transactional
    public void assignWork_WithDeliverableRequirements_ActivatesDeadline() throws Exception {

        User buyer = newEmployeeWithCashBalance();
        User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
        authenticationService.setCurrentUser(buyer);

        laneService.addUserToCompanyLane3(contractor.getId(), buyer.getCompany().getId());

        // Create Work with 3 deliverable requirements
        int numRequirements = 3;
        int hoursToCompleteRequirements = 24;
        Work work = newWorkWithDeliverableRequirements(buyer.getId(), numRequirements, hoursToCompleteRequirements);

        // Send assignment to contractor
        Set<String> users = Sets.newHashSet();
        users.add(contractor.getUserNumber());
        workRoutingService.addToWorkResources(work.getWorkNumber(), users);

        // DeliverableRequirementGroup deadline should get activated after contractor accepts work
        workService.acceptWork(contractor.getId(), work.getId());
        assertEquals(true, work.getDeliverableRequirementGroup().isDeadlineActive());
    }

    /**
     * DeliverableGroup deadline should be deactivated when all deliverables are completed
     */
    @Test
    @Transactional
    public void completeWork_withDeliverableRequirements_DeactivatesDeadline() throws Exception {

        User buyer = newEmployeeWithCashBalance();
        User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
        authenticationService.setCurrentUser(buyer);

        laneService.addUserToCompanyLane3(contractor.getId(), buyer.getCompany().getId());

        // Create Work with 3 deliverable requirements
        int numRequirements = 3;
        int hoursToCompleteRequirements = 24;
        Work work = newWorkWithDeliverableRequirements(buyer.getId(), numRequirements, hoursToCompleteRequirements);

        // Send assignment to contractor
        Set<String> users = Sets.newHashSet();
        users.add(contractor.getUserNumber());
        workRoutingService.addToWorkResources(work.getWorkNumber(), users);

        workService.acceptWork(contractor.getId(), work.getId());

        // Complete all requirements
        int position = 0;
        List<DeliverableRequirement> deliverableRequirements = work.getDeliverableRequirementGroup().getDeliverableRequirements();
        for (DeliverableRequirement deliverableRequirement : deliverableRequirements) {
            deliverableService.addDeliverable(work.getWorkNumber(), newDeliverableAssetDTO(deliverableRequirement.getId(), position));
            position++;
        }

        // All deliverables have been completed. DeliverableRequirementGroup should have inactive deadline
        work = workService.findWork(work.getId());
        assertEquals(false, work.getDeliverableRequirementGroup().isDeadlineActive());
    }

    /**
     * Deliverable Group deadline should be reactivated when a deliverable is rejected.
     */
    @Test
    @Transactional
    public void rejectWorkDeliverables_ReactivatesDeadline() throws Exception {

        User buyer = newEmployeeWithCashBalance();
        User contractor = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(true);
        authenticationService.setCurrentUser(buyer);

        laneService.addUserToCompanyLane3(contractor.getId(), buyer.getCompany().getId());

        // Create Work with 3 deliverable requirements
        int numRequirements = 3;
        int hoursToCompleteRequirements = 24;
        Work work = newWorkWithDeliverableRequirements(buyer.getId(), numRequirements, hoursToCompleteRequirements);

        // Send assignment to contractor
        Set<String> users = Sets.newHashSet();
        users.add(contractor.getUserNumber());
        workRoutingService.addToWorkResources(work.getWorkNumber(), users);

        workService.acceptWork(contractor.getId(), work.getId());

        // Keep track of requirement assets so they can later be rejected
        List<WorkAssetAssociation> workAssetAssociationList = new ArrayList<WorkAssetAssociation>();

        // Complete all requirements
        int position = 0;
        List<DeliverableRequirement> deliverableRequirements = work.getDeliverableRequirementGroup().getDeliverableRequirements();
        for (DeliverableRequirement deliverableRequirement : deliverableRequirements) {
            WorkAssetAssociation workAssetAssociation = deliverableService.addDeliverable(work.getWorkNumber(), newDeliverableAssetDTO(deliverableRequirement.getId(), position));
            workAssetAssociationList.add(workAssetAssociation);
            position++;
        }

        // DeliverableRequirementGroup deadline should get activated if an asset is rejected
        deliverableService.rejectDeliverable("test", buyer.getId(), work.getId(), workAssetAssociationList.iterator().next().getAsset().getId());
        work = workService.findWork(work.getId());
        assertEquals(true, work.getDeliverableRequirementGroup().isDeadlineActive());
    }


}