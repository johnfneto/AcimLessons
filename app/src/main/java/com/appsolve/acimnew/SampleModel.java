package com.appsolve.acimnew;

import java.util.List;

public class SampleModel {
	private String _chapter;
	private List<DetailsModel> Items;
	
	public void setChapter(String chapter){
		this._chapter = chapter.replace("Chapter", "");
	}
	
	public void setItems(List<DetailsModel> items){
		this.Items = items;
	}
	
	public String getChapter(){
		return this._chapter;
	}
	
	public List<DetailsModel> getItems(){
		return this.Items;
	}
}
