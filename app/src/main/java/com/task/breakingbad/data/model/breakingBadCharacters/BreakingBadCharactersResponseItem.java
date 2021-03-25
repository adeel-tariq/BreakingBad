package com.task.breakingbad.data.model.breakingBadCharacters;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class BreakingBadCharactersResponseItem{

	@SerializedName("birthday")
	private String birthday;

	@SerializedName("img")
	private String img;

	@SerializedName("better_call_saul_appearance")
	private List<Object> betterCallSaulAppearance;

	@SerializedName("occupation")
	private List<String> occupation;

	@SerializedName("appearance")
	private List<Integer> appearance;

	@SerializedName("portrayed")
	private String portrayed;

	@SerializedName("name")
	private String name;

	@SerializedName("nickname")
	private String nickname;

	@SerializedName("char_id")
	private int charId;

	@SerializedName("category")
	private String category;

	@SerializedName("status")
	private String status;

	public void setBirthday(String birthday){
		this.birthday = birthday;
	}

	public String getBirthday(){
		return birthday;
	}

	public void setImg(String img){
		this.img = img;
	}

	public String getImg(){
		return img;
	}

	public void setBetterCallSaulAppearance(List<Object> betterCallSaulAppearance){
		this.betterCallSaulAppearance = betterCallSaulAppearance;
	}

	public List<Object> getBetterCallSaulAppearance(){
		return betterCallSaulAppearance;
	}

	public void setOccupation(List<String> occupation){
		this.occupation = occupation;
	}

	public List<String> getOccupation(){
		return occupation;
	}

	public void setAppearance(List<Integer> appearance){
		this.appearance = appearance;
	}

	public List<Integer> getAppearance(){
		return appearance;
	}

	public void setPortrayed(String portrayed){
		this.portrayed = portrayed;
	}

	public String getPortrayed(){
		return portrayed;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
	}

	public String getNickname(){
		return nickname;
	}

	public void setCharId(int charId){
		this.charId = charId;
	}

	public int getCharId(){
		return charId;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"BreakingBadCharactersResponseItem{" + 
			"birthday = '" + birthday + '\'' + 
			",img = '" + img + '\'' + 
			",better_call_saul_appearance = '" + betterCallSaulAppearance + '\'' + 
			",occupation = '" + occupation + '\'' + 
			",appearance = '" + appearance + '\'' + 
			",portrayed = '" + portrayed + '\'' + 
			",name = '" + name + '\'' + 
			",nickname = '" + nickname + '\'' + 
			",char_id = '" + charId + '\'' + 
			",category = '" + category + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}