package cc.ncu.edu.tw.sop_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 2016/4/30.
 */
public class MyAdapter extends BaseAdapter {
    private ArrayList<Map<String,Object>> mAppList;
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private MyView myView;
    private MainActivity mainActivity = new MainActivity();
    private MyAdapter adapter;


    //編輯專案的元件初始化
    private EditText editProjectName;


    //建構式
    public MyAdapter(Context c, ArrayList<Map<String, Object>> appList, int resource, String[] from, int[] to,MainActivity m)
    {
        mContext = c;
        mAppList = appList;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        keyString = new String[from.length];
        valueViewID = new int[to.length];
        mainActivity = m;
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, valueViewID, 0, to.length);
    }


    // 回傳這個 List 有幾個 item
    public int getCount() {
        return mAppList.size();
    }

    public Object getItem(int position) {
        return mAppList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    //我們要客製化的每一列,必須在此方法中描繪我們需要的內容,並返回一個處理好的View
    public View getView( int position, View convertView, ViewGroup parent)
    {
        if (convertView != null)
        {
            myView = (MyView) convertView.getTag();
        }
        else
        {
            convertView = mInflater.inflate(R.layout.sop_list_items, null);
            myView = new MyView();
            myView.title = (TextView)convertView.findViewById(valueViewID[0]);
            myView.delete = (ImageButton)convertView.findViewById(valueViewID[1]);
            myView.edit = (ImageButton)convertView.findViewById(valueViewID[2]);
            myView.cpy = (ImageButton)convertView.findViewById(valueViewID[3]);
            convertView.setTag(myView);
        }


        Map<String, Object> appInfo = mAppList.get(position);
        if (appInfo != null)
        {
            String title = (String) appInfo.get(keyString[0]);
            int did = (Integer)appInfo.get(keyString[1]);
            int eid = (Integer)appInfo.get(keyString[2]);
            int cid = (Integer)appInfo.get(keyString[3]);
            myView.title.setText(title);

            myView.delete.setImageDrawable(myView.delete.getResources().getDrawable(did));
            myView.edit.setImageDrawable(myView.edit.getResources().getDrawable(eid));
            myView.cpy.setImageDrawable(myView.cpy.getResources().getDrawable(cid));

            myView.delete.setOnClickListener(new ButtonDelete_Click(position));
            myView.edit.setOnClickListener(new ButtonEdit_Click(position));
            myView.cpy.setOnClickListener(new ButtonCopy_Click(position));

        }

        return convertView;

    }


    //delete被按的處理
    class ButtonDelete_Click implements View.OnClickListener
    {
        private int position;

        ButtonDelete_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v)
        {
            int vid=v.getId();
            if (vid == myView.delete.getId())
            {

                if(mainActivity.getACCESS_TOKEN()=="")
                {
                    Toast.makeText(mContext,R.string.oauth_delete_certificate,Toast.LENGTH_LONG).show();
                    //Log.v("delete happened", "you click delete button");
                }
                else
                {
                    final String[] listFromResource = mContext.getResources().getStringArray(R.array.sopproject);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setMessage("確定要刪除此專案?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    adapter = new MyAdapter(mContext,mAppList,R.layout.sop_list_items,new String[] {"txtView","delete","edit","copy"}, new int[] {R.id.txtView,R.id.delete,R.id.edit,R.id.copy},mainActivity);
                                    mAppList.remove(position);
                                    mainActivity.getListView().setAdapter(adapter);
                                }
                            }
                    );

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        }
    }

    //edit被按的處理
    class ButtonEdit_Click implements View.OnClickListener
    {
        private int position;

        ButtonEdit_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v)
        {
            int vid=v.getId();
            if (vid == myView.edit.getId())
            {
                if(mainActivity.getACCESS_TOKEN()=="")
                {
                    Toast.makeText(mContext,R.string.oauth_edit_step_certificate,Toast.LENGTH_LONG).show();
                    //Log.v("delete happened", "you click delete button");
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle("編輯專案名稱");
                    builder.setView(R.layout.edit_project_dialog);
                    builder.setCancelable(false);

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    editProjectName = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtProjectName);
                                    Map<String,Object> newItem = new HashMap();

                                    Log.v("successful edit project", "新的專案名稱為:" + editProjectName.getText().toString());

                                    newItem.put("txtView",editProjectName.getText().toString());
                                    newItem.put("delete",R.drawable.delete);
                                    newItem.put("edit",R.drawable.edit);
                                    newItem.put("copy", R.drawable.copy);

                                    adapter = new MyAdapter(mContext, mAppList, R.layout.sop_list_items, new String[]{"txtView", "delete", "edit", "copy"}, new int[]{R.id.txtView, R.id.delete, R.id.edit, R.id.copy}, mainActivity);
                                    mAppList.set(position,newItem);
                                    mainActivity.getListView().setAdapter(adapter);
                                }
                            }
                    );

                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                //Log.v("edit happened","you click edit button");
            }
        }
    }


    //copy被按的處理
    class ButtonCopy_Click implements View.OnClickListener
    {
        private int position;

        ButtonCopy_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            if (vid == myView.cpy.getId())
            {
                //Log.v("copy happened", "you click copy button");
                if(mainActivity.getACCESS_TOKEN()=="")
                {
                    Toast.makeText(mContext,R.string.oauth_edit_step_copy,Toast.LENGTH_LONG).show();
                    //Log.v("delete happened", "you click delete button");
                }
                else
                {




                }
            }

        }
    }



}