package com.joshdholtz.protocol.lib.responses;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.joshdholtz.protocol.lib.ProtocolModel;
import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

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
	
	public abstract void handleResponse(T model);
	public abstract void handleResponse(List<T> models);
	public abstract void handleError();

	public Class<T> getModelClass() {
		Class<T> classType = null;
		
		Type genericType = this.getClass().getGenericSuperclass();
		if(genericType instanceof ParameterizedType)
        {
            ParameterizedType type = (ParameterizedType) genericType;               
            Type[] typeArguments = type.getActualTypeArguments();

            for(Type typeArgument : typeArguments) 
            {   
                classType = ((Class<T>)typeArgument);                  
                Log.d(ProtocolConstants.LOG_TAG, "---------------------- Class has a parameterized type of " + classType.getSimpleName());
            }
        }
		
		return classType;
	}
	
}
