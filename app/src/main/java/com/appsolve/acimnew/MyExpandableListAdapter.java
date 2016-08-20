package com.appsolve.acimnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ExpandableListView mListView;
	private List<SampleModel> mModel;
	
	public MyExpandableListAdapter(Context pContext, ExpandableListView pListView, List<SampleModel> pModel){
		this.mContext = pContext;
		this.mListView = pListView;
		this.mModel = pModel;
	}
	
	
	public void addItem(DetailsModel item, SampleModel groupData){
		if(!mModel.contains(groupData)){
			mModel.add(groupData);
		}
		
		int ind = mModel.indexOf(groupData);
		List<DetailsModel> lstItems =  mModel.get(ind).getItems();
		lstItems.add(item);
		mModel.get(ind).setItems(lstItems);
	}
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<DetailsModel> item = mModel.get(groupPosition).getItems();
		return item.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
			ViewGroup parent) {
		DetailsModel item = (DetailsModel)getChild(groupPosition, childPosition);
		if(view == null){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item, null);
		}
		
		TextView txtCountry = (TextView)view.findViewById(R.id.txtCountry);
		txtCountry.setText(item.getSection());
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mModel.get(groupPosition).getItems().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mModel.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mModel.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		SampleModel model =  (SampleModel)getGroup(groupPosition);
		if(view == null){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.group_item, null);
		}
		
		TextView txtContinent = (TextView)view.findViewById(R.id.txtContinent);
		txtContinent.setText(model.getChapter());
		return view;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
