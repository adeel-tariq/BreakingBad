package com.task.breakingbad.data.model.breakingBadCharacters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// model class for BreakingBadCharactersResponse from API
public class BreakingBadCharactersResponse {

	@SerializedName("char_id")
	@Expose
	private Integer charId;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("birthday")
	@Expose
	private String birthday;
	@SerializedName("occupation")
	@Expose
	private List<String> occupation = null;
	@SerializedName("img")
	@Expose
	private String img;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("nickname")
	@Expose
	private String nickname;
	@SerializedName("appearance")
	@Expose
	private List<Integer> appearance = null;
	@SerializedName("portrayed")
	@Expose
	private String portrayed;
	@SerializedName("category")
	@Expose
	private String category;
	@SerializedName("better_call_saul_appearance")
	@Expose
	private List<Integer> betterCallSaulAppearance = null;

	public Integer getCharId() {
		return charId;
	}

	public void setCharId(Integer charId) {
		this.charId = charId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public List<String> getOccupation() {
		return occupation;
	}

	public void setOccupation(List<String> occupation) {
		this.occupation = occupation;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public List<Integer> getAppearance() {
		return appearance;
	}

	public void setAppearance(List<Integer> appearance) {
		this.appearance = appearance;
	}

	public String getPortrayed() {
		return portrayed;
	}

	public void setPortrayed(String portrayed) {
		this.portrayed = portrayed;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<Integer> getBetterCallSaulAppearance() {
		return betterCallSaulAppearance;
	}

	public void setBetterCallSaulAppearance(List<Integer> betterCallSaulAppearance) {
		this.betterCallSaulAppearance = betterCallSaulAppearance;
	}

	// toString method to print the whole response which is parsed to this class
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