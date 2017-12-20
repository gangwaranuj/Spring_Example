package com.workmarket.domains.velvetrope.rope;

import com.workmarket.domains.model.ProfileActionType;
import com.workmarket.velvetrope.Rope;

import java.util.List;

public class HideBackgroundChecksRope implements Rope {

	private final List<ProfileActionType> missingActions;
	private final ProfileActionType profileActionType;

	public HideBackgroundChecksRope(
		List<ProfileActionType> missingActions,
		ProfileActionType profileActionType
	) {
		this.missingActions = missingActions;
		this.profileActionType = profileActionType;
	}

	@Override
	public void enter() {
		missingActions.remove(profileActionType);
	}
}
