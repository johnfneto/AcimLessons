package com.appsolve.acimnew;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by johnn on 3/07/15.
 */
public class BookmarksListFragment extends Fragment {
    private final String TAG = "BookmarksListFragment";
    //ListView listView;

    MainActivity mActivity;
    ArrayList<HashMap<String,String>> bookmarksList;
    ListView list;
    SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_bookmarks, container, false);

        mActivity =	(MainActivity) this.getActivity();

        bookmarksList = mActivity.dbHelper.getBookmarks();
        //itemsList.add("First bookmark");
        //itemsList.add("Second bookmark");

        //Log.d(TAG, "bookmarksList :" + mActivity.bookmarksList);


        list = (ListView)view.findViewById(android.R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setDividerHeight(0);

        /*final SimpleAdapter adapter = new SimpleAdapter(getActivity(), mActivity.bookmarksList,
                R.layout.bookmark_item, new String[] { "chapter", "line", "time", "date" },
                new int[] {R.id.chapterText, R.id.lineText, R.id.timeText, R.id.dateText });*/

        adapter = new SimpleAdapter(getActivity(), bookmarksList,
                R.layout.bookmark_item, new String[] { "chapter", "line", "time", "date" },
                new int[] {R.id.chapterText, R.id.lineText, R.id.timeText, R.id.dateText });

        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);




        //listView = (ListView)view.findViewById(R.id.list);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,itemsList);
        //listView.setAdapter(adapter);

        list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                FragmentManager fragmentManager;
                Fragment fragment = null;
                Bundle bundle = new Bundle();

                Log.d(TAG, "pos :" + pos);


                /*if (mActivity.bookmarksList.get(pos).get("place").equals("text"))
                    bundle.putInt("groupPosition", Integer.parseInt(mActivity.bookmarksList.get(pos).get("chapter").replace("Chapter ", "")));
                else if (mActivity.bookmarksList.get(pos).get("place").equals("manual")) {
                    bundle.putInt("groupPosition", Integer.parseInt(mActivity.bookmarksList.get(pos).get("chapter").replace("Manual ", "")));
                    bundle.putString("chapter", mActivity.bookmarksList.get(pos).get("chapter").replace("Manual ", ""));
                }
                else if (mActivity.bookmarksList.get(pos).get("place").equals("lessons"))
                    bundle.putInt("groupPosition", Integer.parseInt(mActivity.bookmarksList.get(pos).get("chapter").replace("Lesson ", "")));
                bundle.putInt("positionY", Integer.parseInt(mActivity.bookmarksList.get(pos).get("scrollY")));
                bundle.putFloat("scrollPosition", Float.parseFloat(mActivity.bookmarksList.get(pos).get("position")));
                bundle.putBoolean("bookMark", true);

                if (mActivity.bookmarksList.get(pos).get("place").equals("text"))
                    fragment = new MainActivity.TextFragment();
                else if (mActivity.bookmarksList.get(pos).get("place").equals("manual"))
                    fragment = new MainActivity.ManualFragment();*/





                if (bookmarksList.get(pos).get("place").equals("text"))
                    bundle.putInt("groupPosition", Integer.parseInt(bookmarksList.get(pos).get("chapter").replace("Chapter ", "")));
                else if (bookmarksList.get(pos).get("place").equals("manual")) {
                    bundle.putInt("groupPosition", Integer.parseInt(bookmarksList.get(pos).get("chapter").replace("Manual ", "")));
                    bundle.putString("chapter", bookmarksList.get(pos).get("chapter").replace("Manual ", ""));
                }
                else if (bookmarksList.get(pos).get("place").equals("lessons"))
                    bundle.putInt("groupPosition", Integer.parseInt(bookmarksList.get(pos).get("chapter").replace("Lesson ", "")));
                bundle.putInt("positionY", Integer.parseInt(bookmarksList.get(pos).get("scrollY")));
                bundle.putFloat("scrollPosition", Float.parseFloat(bookmarksList.get(pos).get("position")));
                bundle.putBoolean("bookMark", true);

                if (bookmarksList.get(pos).get("place").equals("text"))
                    fragment = new MainActivity.TextFragment();
                else if (bookmarksList.get(pos).get("place").equals("manual"))
                    fragment = new MainActivity.ManualFragment();
                fragment.setArguments(bundle);
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
                final int item = pos;

                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View dialogView = factory.inflate(R.layout.custom_dialog3, null);

                final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setView(dialogView);
                dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //mActivity.bookmarksList.remove(item);
                        mActivity.dbHelper.deleteBookmark(Long.parseLong(bookmarksList.get(pos).get("id")));
                        reloadBookmarks();
                        //bookmarksList = mActivity.dbHelper.getBookmarks();

                        dialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                ((TextView) dialogView.findViewById(R.id.title)).setText("Delete Bookmark");
                ((TextView) dialogView.findViewById(R.id.message)).setText("Are you sure you want to delete this bookmark?");

                dialog.show();

                return false;
            }
        });

        return view;
    }

    private void reloadBookmarks() {
        bookmarksList = mActivity.dbHelper.getBookmarks();
        adapter = new SimpleAdapter(getActivity(), bookmarksList,
                R.layout.bookmark_item, new String[] { "chapter", "line", "time", "date" },
                new int[] {R.id.chapterText, R.id.lineText, R.id.timeText, R.id.dateText });
        list.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }
}
