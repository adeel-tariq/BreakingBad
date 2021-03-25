package com.task.breakingbad.data.model.breakingBadCharacters;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BreakingBadCharactersResponse{

	@SerializedName("BreakingBadCharactersResponse")
	private List<BreakingBadCharactersResponseItem> breakingBadCharactersResponse;

	public void setBreakingBadCharactersResponse(List<BreakingBadCharactersResponseItem> breakingBadCharactersResponse){
		this.breakingBadCharactersResponse = breakingBadCharactersResponse;
	}

	public List<BreakingBadCharactersResponseItem> getBreakingBadCharactersResponse(){
		return breakingBadCharactersResponse;
	}

	@Override
 	public String toString(){
		return 
			"BreakingBadCharactersResponse{" + 
			"breakingBadCharactersResponse = '" + breakingBadCharactersResponse + '\'' + 
			"}";
		}
}