package com.joshdholtz.protocol.lib.responses;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.joshdholtz.protocol.lib.ProtocolModel;

public abstract class ModelResponseHandler<T extends ProtocolModel> extends JSONResponseHandler {
	
	@Override
	public void handleResponse(JSONObject jsonObject, JSONArray jsonArray) {
		
		if (jsonObject != null) {
			T model = ProtocolModel.createModel(this.getModelClass(), jsonObject);
			handleResponse(model);
		} else if (jsonArray != null) {
			List<T> models = ProtocolModel.createModels(this.getModelClass(), jsonArray);
			handleResponse(models);
		} else {
			handleError();
		}
	}
	
	public abstract Class<T> getModelClass();
	public abstract void handleResponse(T model);
	public abstract void handleResponse(List<T> models);
	public abstract void handleError();

}
