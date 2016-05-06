package cc.ncu.edu.tw.sop_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
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
    //private MainActivity mActivity;
    private Dialog addStepDialog;

    //建構式
    public MyAdapter(Context c, ArrayList<Map<String, Object>> appList, int resource, String[] from, int[] to) {

        mContext = c;
        mAppList = appList;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        keyString = new String[from.length];
        valueViewID = new int[to.length];
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
    public View getView( int position, View convertView, ViewGroup parent) {

        if (convertView != null) {
            myView = (MyView) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.sop_list_items, null);
            myView = new MyView();
            myView.title = (TextView)convertView.findViewById(valueViewID[0]);
            myView.add = (ImageButton)convertView.findViewById(valueViewID[1]);
            myView.delete = (ImageButton)convertView.findViewById(valueViewID[2]);
            myView.edit = (ImageButton)convertView.findViewById(valueViewID[3]);
            myView.cpy = (ImageButton)convertView.findViewById(valueViewID[4]);
            convertView.setTag(myView);
        }


        Map<String, Object> appInfo = mAppList.get(position);
        if (appInfo != null) {


            String title = (String) appInfo.get(keyString[0]);
            int aid = (Integer)appInfo.get(keyString[1]);
            int did = (Integer)appInfo.get(keyString[2]);
            int eid = (Integer)appInfo.get(keyString[3]);
            int cid = (Integer)appInfo.get(keyString[4]);
            myView.title.setText(title);
            myView.add.setImageDrawable(myView.add.getResources().getDrawable(aid));
            myView.delete.setImageDrawable(myView.delete.getResources().getDrawable(did));
            myView.edit.setImageDrawable(myView.edit.getResources().getDrawable(eid));
            myView.cpy.setImageDrawable(myView.cpy.getResources().getDrawable(cid));

            myView.add.setOnClickListener(new ButtonAdd_Click(position));
            myView.delete.setOnClickListener(new ButtonDelete_Click(position));
            myView.edit.setOnClickListener(new ButtonEdit_Click(position));
            myView.cpy.setOnClickListener(new ButtonCopy_Click(position));

        }

        return convertView;

    }
    //add被按的事件處理
    class ButtonAdd_Click implements View.OnClickListener {
        private int position;

        ButtonAdd_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            if (vid == myView.add.getId())
                Log.v("add happened", "you click add button");


            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage("新增專案");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }

    }

    //按下新增步驟dialog中"確定新增"按鈕的處理
    private View.OnClickListener addStepDialogBtnOKOnClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
           // addStepDialog.cancel();
        }
    };


    //按下新增步驟dialog中"取消新增"按鈕的處理
    private View.OnClickListener addStepDialogBtnCancelOnClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            //addStepDialog.cancel();
        }
    };




    //delete被按的處理
    class ButtonDelete_Click implements View.OnClickListener {
        private int position;

        ButtonDelete_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            if (vid == myView.add.getId())
                Log.v("delete happened", "you click delete button");
        }
    }

    //edit被按的處理
    class ButtonEdit_Click implements View.OnClickListener {
        private int position;

        ButtonEdit_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            if (vid == myView.add.getId())
                Log.v("edit happened","you click edit button");
        }
    }

    //copy被按的處理
    class ButtonCopy_Click implements View.OnClickListener {
        private int position;

        ButtonCopy_Click(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            int vid=v.getId();
            if (vid == myView.add.getId())
                Log.v("copy happened", "you click copy button");
        }
    }



}