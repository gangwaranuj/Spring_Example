package com.workmarket.dao.voice;

import java.util.Calendar;

import org.springframework.stereotype.Repository;

import com.workmarket.dao.AbstractDAO;
import com.workmarket.domains.model.voice.VoiceCall;

@Repository
public class VoiceCallDAOImpl extends AbstractDAO<VoiceCall> implements VoiceCallDAO  {
	protected Class<VoiceCall> getEntityClass() {
		return VoiceCall.class;
	}
	
	public VoiceCall getByCallId(String callId)  {
		return (VoiceCall)getFactory().getCurrentSession().getNamedQuery("voiceCall.byCallId")
			.setString("call_id", callId)
			.uniqueResult();
	}
}