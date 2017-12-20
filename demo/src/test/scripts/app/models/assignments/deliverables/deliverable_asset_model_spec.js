// define([
// 	'assignments/deliverable_asset_model'
// ], function (DeliverableAssetModel) {
// 	'use strict';
//
// 	describe('DeliverableRequirementModel', function () {
// 		var model, model2;
//
// 		beforeEach(function () {
// 			model = new DeliverableAssetModel({
// 				"uploadedBy": "Nicole Fowler",
// 				"uploadDate": 1419008066000,
// 				"deliverableRequirementId": 5129,
// 				"position": 5,
// 				"id": 292186,
// 				"uuid": "3430b55a-cb9f-41b4-afea-3cbc07c3eb1a",
// 				"transformLargeUuid": "54bf8ab1-17da-4c98-a323-bc9a57b6d968",
// 				"name": "doob (1).png",
// 				"mimeType": "image/png",
// 				"type": "photos",
// 				"uri": "/asset/3430b55a-cb9f-41b4-afea-3cbc07c3eb1a",
// 				"workNumber": "9229265288",
// 				"assetHistory": [
// 					{
// 						"uploadedBy": "Nicole Fowler",
// 						"uploadDate": 1419004673000,
// 						"rejectedOn": 1419004753000,
// 						"rejectionReason": "asdasdasd",
// 						"rejectedBy": "Nicole Fowler",
// 						"deliverableRequirementId": 5129,
// 						"position": 5,
// 						"id": 292166,
// 						"uuid": "fbb5036c-9aee-4fbd-bf20-cab725c246ad",
// 						"transformLargeUuid": "f2c2f8a2-44cd-448c-88bd-dbaca07fa038",
// 						"name": "doobstudio (1).jpg",
// 						"mimeType": "image/jpeg",
// 						"type": "photos",
// 						"uri": "/asset/fbb5036c-9aee-4fbd-bf20-cab725c246ad",
// 						"workNumber": "9229265288"
// 					}
// 				]
// 			});
// 			model2 = new DeliverableAssetModel({
// 				"uploadedBy": "Nicole Fowler",
// 				"uploadDate": 1419005964000,
// 				"deliverableRequirementId": 5129,
// 				"position": 6,
// 				"id": 292172,
// 				"uuid": "57bac836-2f42-4714-b6c6-fa570a27d525",
// 				"transformLargeUuid": "0251aa4b-6339-4c76-8c50-9abc7ec431a1",
// 				"name": "doobstudio (2).jpg",
// 				"mimeType": "image/jpeg",
// 				"type": "photos",
// 				"uri": "/asset/57bac836-2f42-4714-b6c6-fa570a27d525",
// 				"workNumber": "9229265288"
// 			});
// 		});
//
// 		afterEach(function () {
// 			model = undefined;
// 		});
//
// 		it('has a global instance', function () {
// 			expect(DeliverableAssetModel).toBeDefined();
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
// 		it('to have an asset history', function () {
// 			expect(model.get('assetHistory')).toBeDefined();
// 			expect(model2.get('assetHistory')).toBeDefined();
// 		});
//
// 		it('to have a reference to its rejectedAsset if its an updatedAsset', function () {
// 			expect(model.get('rejectedOn')).toBeUndefined();
// 			expect(model.get('assetHistory').length).toEqual(1);
// 			expect(model.get('rejectedAsset')).toBeDefined();
// 		});
//
// 		it('to pop an asset from its history', function () {
// 			var assetToBePopped = model.get('assetHistory')[0];
// 			var poppedAsset = model.popAssetFromHistory();
// 			expect(assetToBePopped).toEqual(poppedAsset);
// 			expect(poppedAsset.id).toEqual(model.get('id'));
// 		});
//
// 		it('to push an asset to its history', function () {
// 			model.pushAssetToHistory(model2.attributes);
// 			expect(model.get('id')).toEqual(model2.get('id'));
// 		});
//
// 	});
// });
