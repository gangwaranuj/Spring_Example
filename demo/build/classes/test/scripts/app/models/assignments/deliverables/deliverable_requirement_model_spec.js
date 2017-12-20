// define([
// 	'assignments/deliverable_requirement_model',
// 	'assignments/deliverable_assets_collection',
// 	'funcs/wm-isValidImageExtension',
// 	'funcs/wm-isValidFileExtension'
// ], function (DeliverableRequirementModel, DeliverableAssetCollection, isValidImageExtension, isValidFileExtension) {
// 	'use strict';
//
// 	describe('DeliverableRequirementModel', function () {
// 		var model;
//
// 		beforeEach(function () {
// 			model = new DeliverableRequirementModel({
// 				id: 1009,
// 				instructions: "qsqsqs",
// 				numberOfFiles: 4,
// 				type: "photos",
// 				millisOffset: -18000000,
// 				workNumber: "2439510178",
// 				MAX_UPLOAD_SIZE: 52428800,
// 				DELIVERABLE_TYPES: {
// 					"photos": {
// 						"type": "photos",
// 						"description": "Photos"
// 					},
// 					"other": {
// 						"type": "other",
// 						"description": "Other"
// 					},
// 					"sign_off": {
// 						"type": "sign_off",
// 						"description": "Sign Off"
// 					}
// 				},
// 				UNSUPPORTED_FILE_MESSAGE: "File type not supported.",
// 				UNSUPPORTED_IMAGE_FILE_MESSAGE: "Photo requirements only accept image files.",
// 				INVALID_FILE_MESSAGE: "File type not recognized."
// 			});
// 			var assets = [
// 				{
// 					"uploadedBy": "Nicole Fowler",
// 					"uploadDate": 1419004631000,
// 					"rejectedOn": 1419004746000,
// 					"rejectionReason": "asdasdas",
// 					"rejectedBy": "Nicole Fowler",
// 					"deliverableRequirementId": 5129,
// 					"position": 0,
// 					"id": 292161,
// 					"uuid": "d3c6da66-7f26-4a56-9b04-2131a7e88c07",
// 					"transformLargeUuid": "94e025c9-a2b1-4302-86dd-733c1a3656d9",
// 					"name": "doobstudio (2).jpg",
// 					"mimeType": "image/jpeg",
// 					"type": "photos",
// 					"uri": "/asset/d3c6da66-7f26-4a56-9b04-2131a7e88c07",
// 					"workNumber": "9229265288",
// 					"assetHistory": []
// 				},
// 				{
// 					"uploadedBy": "Nicole Fowler",
// 					"uploadDate": 1419008066000,
// 					"deliverableRequirementId": 5129,
// 					"position": 5,
// 					"id": 292186,
// 					"uuid": "3430b55a-cb9f-41b4-afea-3cbc07c3eb1a",
// 					"transformLargeUuid": "54bf8ab1-17da-4c98-a323-bc9a57b6d968",
// 					"name": "doob (1).png",
// 					"mimeType": "image/png",
// 					"type": "photos",
// 					"uri": "/asset/3430b55a-cb9f-41b4-afea-3cbc07c3eb1a",
// 					"workNumber": "9229265288",
// 					"assetHistory": [
// 						{
// 							"uploadedBy": "Nicole Fowler",
// 							"uploadDate": 1419004673000,
// 							"rejectedOn": 1419004753000,
// 							"rejectionReason": "asdasdasd",
// 							"rejectedBy": "Nicole Fowler",
// 							"deliverableRequirementId": 5129,
// 							"position": 5,
// 							"id": 292166,
// 							"uuid": "fbb5036c-9aee-4fbd-bf20-cab725c246ad",
// 							"transformLargeUuid": "f2c2f8a2-44cd-448c-88bd-dbaca07fa038",
// 							"name": "doobstudio (1).jpg",
// 							"mimeType": "image/jpeg",
// 							"type": "photos",
// 							"uri": "/asset/fbb5036c-9aee-4fbd-bf20-cab725c246ad",
// 							"workNumber": "9229265288"
// 						}
// 					]
// 				},
// 				{
// 					"uploadedBy": "Nicole Fowler",
// 					"uploadDate": 1419005964000,
// 					"deliverableRequirementId": 5129,
// 					"position": 6,
// 					"id": 292172,
// 					"uuid": "57bac836-2f42-4714-b6c6-fa570a27d525",
// 					"transformLargeUuid": "0251aa4b-6339-4c76-8c50-9abc7ec431a1",
// 					"name": "doobstudio (2).jpg",
// 					"mimeType": "image/jpeg",
// 					"type": "photos",
// 					"uri": "/asset/57bac836-2f42-4714-b6c6-fa570a27d525",
// 					"workNumber": "9229265288",
// 					"assetHistory": []
// 				}
// 			];
// 			model.deliverableAssets = new DeliverableAssetCollection(assets);
// 		});
//
// 		afterEach(function () {
// 			model = undefined;
// 		});
//
// 		it('has a global instance', function () {
// 			expect(DeliverableRequirementModel).toBeDefined();
// 		});
//
// 		it('can be instantiated', function () {
// 			expect(model).toBeDefined();
// 		});
//
// 		it('is initialized as a valid model', function () {
// 			expect(model.isValid()).toBeTruthy();
// 		});
//
// 		it('hasAtLeastOneNonRejectedAsset', function () {
// 			expect(model.hasAtLeastOneNonRejectedAsset()).toBeTruthy();
// 		});
//
// 		it('has no non-rejected assets', function () {
// 			_.each(model.deliverableAssets.models, function (a) {
// 				a.set('rejectedOn', 1419004746000);
// 			});
// 			expect(model.hasAtLeastOneNonRejectedAsset()).toBeFalsy();
// 		});
//
// 		it('can setPositionOfSelectedAsset', function () {
// 			expect(model.getPositionOfSelectedAsset()).toBeFalsy();
// 			model.setPositionOfSelectedAsset(0);
// 			expect(model.getPositionOfSelectedAsset()).toEqual(0);
// 			expect(model.getSelectedAsset()).toEqual(model.deliverableAssets.getByPosition(model.getPositionOfSelectedAsset()));
// 		});
//
// 		it('can increment position of selected asset by 1', function () {
// 			model.setPositionOfSelectedAsset(0);
// 			model.incrementPositionOfSelectedAsset(1);
// 			expect(model.getPositionOfSelectedAsset()).toEqual(5);
// 			expect(model.getSelectedAsset()).toEqual(model.deliverableAssets.getByPosition(model.getPositionOfSelectedAsset()));
// 		});
//
// 		it('can increment position of selected asset by -1', function () {
// 			model.setPositionOfSelectedAsset(0);
// 			model.incrementPositionOfSelectedAsset(-1);
// 			expect(model.getPositionOfSelectedAsset()).toEqual(6);
// 			expect(model.getSelectedAsset()).toEqual(model.deliverableAssets.getByPosition(model.getPositionOfSelectedAsset()));
// 		});
//
// 		it('can increment position of selected asset by 4', function () {
// 			model.setPositionOfSelectedAsset(0);
// 			model.incrementPositionOfSelectedAsset(4);
// 			expect(model.getPositionOfSelectedAsset()).toEqual(5);
// 			expect(model.getSelectedAsset()).toEqual(model.deliverableAssets.getByPosition(model.getPositionOfSelectedAsset()));
// 		});
//
// 		it('can increment position of selected asset by -3', function () {
// 			model.setPositionOfSelectedAsset(0);
// 			model.incrementPositionOfSelectedAsset(-3);
// 			expect(model.getPositionOfSelectedAsset()).toEqual(0);
// 			expect(model.getSelectedAsset()).toEqual(model.deliverableAssets.getByPosition(model.getPositionOfSelectedAsset()));
// 		});
//
// 		it('can getIndexOfSelectedAsset', function () {
// 			model.setPositionOfSelectedAsset(0);
// 			expect(model.getIndexOfSelectedAsset()).toEqual(0);
// 		});
//
// 		it('accepts .docx files', function () {
// 			model.VALIDATION_FUNCTION = isValidFileExtension;
// 			var validation = model.validateUpload({name: 'flarg.docx', size: 12345});
// 			expect(validation.errors.length).toEqual(0);
// 		});
//
// 		it('does not accept uploads greater than 50mb', function () {
// 			model.VALIDATION_FUNCTION = isValidFileExtension;
// 			var validation = model.validateUpload({name: 'flarg.docx', size: 50 * 1024 * 1024 * 100});
// 			expect(validation.errors.length).toEqual(1);
// 		});
//
// 		it('accepts .png files', function () {
// 			var validation = model.validateUpload({name: 'flarg.png', size: 12345});
// 			expect(validation.errors.length).toEqual(0);
// 		});
//
// 		it('does not accept .docx files if it is a photo deliverable requirement', function () {
// 			model.VALIDATION_FUNCTION = isValidImageExtension;
// 			var validation = model.validateUpload({name: 'flarg.docx', size: 12345});
// 			expect(validation.errors.length).toEqual(1);
// 		});
//
// 		it('accepts .png files if it is a photo deliverable requirement', function () {
// 			model.VALIDATION_FUNCTION = isValidImageExtension;
// 			var validation = model.validateUpload({name: 'flarg.png', size: 12345});
// 			expect(validation.errors.length).toEqual(0);
// 		});
//
// 		it('can getNextPosition', function () {
// 			expect(model.getNextPosition()).toEqual(7);
// 		});
//
// 		it('can have missing assets', function () {
// 			expect(model.getNumberOfMissingAssets()).toEqual(2)
// 		});
//
// 		it('can not have missing assets', function () {
// 			model.set('numberOfFiles', 1);
// 			expect(model.getNumberOfMissingAssets()).toEqual(0)
// 		});
//
// 		it('can be complete', function () {
// 			model.set('numberOfFiles', 1);
// 			expect(model.isComplete()).toBeTruthy();
// 		});
//
// 		it('can be not complete', function () {
// 			expect(model.isComplete()).toBeFalsy();
// 		});
//
// 		it('can need placeholders', function () {
// 			expect(model.getPlaceholdersNeeded()).toEqual(1);
// 			expect(model.needPlaceholder()).toBeTruthy();
// 		});
//
// 		it('can need no placeholders', function () {
// 			model.set('numberOfFiles', 3);
// 			expect(model.getPlaceholdersNeeded()).toEqual(0);
// 			expect(model.needPlaceholder()).toBeFalsy();
// 		});
//
// 	});
// });
