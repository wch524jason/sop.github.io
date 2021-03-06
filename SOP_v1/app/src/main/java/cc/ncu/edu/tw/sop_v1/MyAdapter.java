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

import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Created by jason on 2016/4/30.
 */
public class MyAdapter extends BaseAdapter implements Filterable
{
    private ArrayList<Map<String,Object>> mAppList;
    private ArrayList<Map<String,Object>> mOriginalValues;
    private MyFilter filter;


    private Project[] p = new Project[100];
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private MyView myView;
    private MainActivity mainActivity = new MainActivity();
    private MyAdapter adapter;

    private LayoutInflater mInflater;


    private ArrayList<Map<String,Object>> afterFilterList;

    RequestQueue mQueue;



    //複製專案所存放的Step
    private Step[] step =new Step[30];
    private int stepCount=0;
    private int stepIndex=0;
    private int getIdIndex;  //存放新複製的專案ID

    //編輯專案的元件初始化
    private EditText editProjectName;

    //private int counter=0;;


    //建構式
    public MyAdapter(Context c, ArrayList<Map<String, Object>> appList, int resource, String[] from, int[] to,MainActivity m,Project[] p)
    {
        mContext = c;
        mAppList = appList;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        keyString = new String[from.length];
        valueViewID = new int[to.length];

        mainActivity = m;
        this.p = p;


        mQueue = Volley.newRequestQueue(mContext);

        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, valueViewID, 0, to.length);
    }


    // 回傳這個 List 有幾個 item
    public int getCount()
    {
        return mAppList.size();
    }

    public Object getItem(int position) {
        return mAppList.get(position);
    }

    public long getItemId(int position) {return position;}


    //我們要客製化的每一列,必須在此方法中描繪我們需要的內容,並返回一個處理好的View
    public View getView( int position, View convertView, ViewGroup parent)
    {
        myView =new MyView();
        if (convertView != null)
        {
            myView = (MyView) convertView.getTag();
        }
        else
        {
            convertView = mInflater.inflate(R.layout.sop_list_items, null);
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

    @Override
    public android.widget.Filter getFilter()
    {
        if (filter == null)
        {
            filter  = new MyFilter();
        }
        return filter;
    }

    private class MyFilter extends android.widget.Filter
    {
        //performFiltering是實際需要做篩選的code 在不同的thread進行
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            constraint = constraint.toString();
            FilterResults result = new FilterResults();
            if (mOriginalValues == null)
            {
                synchronized (this)
                {
                    mOriginalValues = new ArrayList<>(mAppList);
                }
            }

            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Map<String,Object>> filteredItems = new ArrayList<>();
                for(int i = 0, l = mOriginalValues.size(); i < l; i++)
                {
                    Map<String,Object> item = mOriginalValues.get(i);
                    if(item.get("txtView").toString().contains(constraint))
                    {
                        filteredItems.add(item);
                    }
                }

                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    ArrayList<Map<String,Object>> list = new ArrayList<>(mOriginalValues);
                    result.values = list;
                    result.count = list.size();
                }
            }
            return result;
        }


        //publishResults則是用來把篩選結果publish出去的函式
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            mAppList= (ArrayList<Map<String,Object>>)results.values;
            afterFilterList =mAppList;
            if(results.count>0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated();
            }
        }


    }


    //判斷篩選過後專案的Flow_id
    public ArrayList<Map<String,Object>> getAfterFilterList()
    {
        return afterFilterList;
    }

    //============================================================//
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setMessage("確定要刪除此專案?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    adapter = new MyAdapter(mContext, mAppList, R.layout.sop_list_items, new String[]{"txtView", "delete", "edit", "copy"}, new int[]{R.id.txtView, R.id.delete, R.id.edit, R.id.copy}, mainActivity,p);
                                    mAppList.remove(position);
                                    mainActivity.getListView().setAdapter(adapter);

                                    int ID =p[position].getProjectId();

                                    //刪減後project[]做的調整
                                    mainActivity.setProjectNum();

                                    //delete後端所做的變更
                                    StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "http://140.115.3.188:3000/sop/v1/processes/"+String.valueOf(ID), new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);

                                        }
                                    }, new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }

                                    }) {
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer"+ " " +mainActivity.getACCESS_TOKEN());
                                            return map;
                                        }
                                    };

                                    mQueue.add(stringRequest);
                                }
                            }
                    ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        }
    }

    //=======================================================================//
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

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {

                                    editProjectName = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtProjectName);
                                    Map<String,Object> newItem = new HashMap();

                                    //Log.v("successful edit project", "更改後的專案名稱:" + editProjectName.getText().toString());

                                    newItem.put("txtView",editProjectName.getText().toString());
                                    newItem.put("delete",R.drawable.delete);
                                    newItem.put("edit",R.drawable.edit);
                                    newItem.put("copy", R.drawable.copy);

                                    adapter = new MyAdapter(mContext, mAppList, R.layout.sop_list_items, new String[]{"txtView", "delete", "edit", "copy"}, new int[]{R.id.txtView, R.id.delete, R.id.edit, R.id.copy}, mainActivity,p);
                                    mAppList.set(position, newItem);
                                    mainActivity.getListView().setAdapter(adapter);

                                    int ID =p[position].getProjectId();
                                    p[position].setContent(editProjectName.getText().toString());

                                    //編輯專案名稱後,後端坐的處理
                                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, "http://140.115.3.188:3000/sop/v1/processes/"+String.valueOf(ID), new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);

                                        }
                                    }, new Response.ErrorListener(){
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }

                                    })
                                    { public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer"+" "+mainActivity.getACCESS_TOKEN());
                                            return map;
                                        }


                                        public Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("name", editProjectName.getText().toString());
                                            return map;
                                        }
                                    };
                                    mQueue.add(stringRequest);


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

//====================================================================================================//

    //copy被按的處理
    class ButtonCopy_Click implements View.OnClickListener
    {
        private int position;

        private int newProjectFlowId;
        ButtonCopy_Click(int pos)
        {
            position = pos;
        }

        @Override
        public void onClick(View v)
        {
            int vid=v.getId();

            if (vid == myView.cpy.getId())
            {
                //驗證有沒有取得 access token
                if(mainActivity.getACCESS_TOKEN()=="")
                {
                    Toast.makeText(mContext,R.string.oauth_edit_step_copy,Toast.LENGTH_LONG).show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("確定要複製此步驟?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Map<String, Object> copyItem = new HashMap<>();
                            copyItem.put("txtView", p[position].getProjectContent());
                            copyItem.put("delete", R.drawable.delete);
                            copyItem.put("edit", R.drawable.edit);
                            copyItem.put("copy", R.drawable.copy);

                            p[mAppList.size()] = new Project(mAppList.size(), 0, p[position].getProjectContent());
                            getIdIndex =mAppList.size();
                            mAppList.add(copyItem);

                            adapter = new MyAdapter(mContext, mAppList, R.layout.sop_list_items, new String[]{"txtView", "delete", "edit", "copy"}, new int[]{R.id.txtView, R.id.delete, R.id.edit, R.id.copy}, mainActivity, p);
                            mainActivity.getListView().setAdapter(adapter);


                            //將複製專案的結果post到後端
                            StringRequest postProjectRequest = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    try
                                    {
                                        Log.d("postProjectRequestSucce", response);
                                        JSONObject object =new JSONObject(response);
                                        p[getIdIndex].setProjectId(Integer.parseInt(object.getString("id")));

                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError error)
                                {
                                    Log.e("postPrjRequestErrHappen", error.getMessage(), error);
                                }

                            })
                            {
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("Authorization", "Bearer" + " " + mainActivity.getACCESS_TOKEN());
                                    return map;
                                }


                                public Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("name", p[position].getProjectContent());
                                    return map;
                                }
                            };

                            mQueue.add(postProjectRequest);

//===================================================================================================//
                            //找尋被複製的專案裡面有哪些步驟
                            StringRequest getStepRequest = new StringRequest("http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d("getStepRequestSucces", response);

                                    try
                                    {
                                        JSONArray array = new JSONArray(response);
                                        for (int i = 0; i < array.length(); i++)
                                        {
                                            if (p[position].getProjectId() == Integer.parseInt(array.getJSONObject(i).getString("flow_id")))
                                            {
                                                step[stepIndex] = new Step(Integer.parseInt(array.getJSONObject(i).getString("prev")), Integer.parseInt(array.getJSONObject(i).getString("next")), array.getJSONObject(i).getString("action"), p[getIdIndex].getProjectId(), Integer.parseInt(array.getJSONObject(i).getString("id")));
                                                step[stepIndex].setItem(array.getJSONObject(i).getString("items"));

                                                Log.v("prevOut",array.getJSONObject(i).getString("prev"));
                                                Log.v("nextOut",array.getJSONObject(i).getString("next"));
                                                Log.v("Flow_idOut", Integer.toString(p[getIdIndex].getProjectId()));


                                                step[stepIndex].setContent(array.getJSONObject(i).getString("items"), array.getJSONObject(i).getString("UnitId"), array.getJSONObject(i).getString("PersonId"), array.getJSONObject(i).getString("PlaceId"));
                                                Log.v("被複製的步驟內容", step[stepIndex].getContent());
                                                Log.v("被複製的步驟物品", step[stepIndex].getItem());
                                                Log.v("被複製的步驟單位", step[stepIndex].getUnit());
                                                Log.v("被複製的步驟人員", step[stepIndex].getPerson());
                                                Log.v("被複製的步驟地點", step[stepIndex].getPlace());

                                                HttpClient httpClient = new HttpClient(mQueue,mainActivity.getACCESS_TOKEN());
                                                httpClient.upLoadCopySteps(step[stepIndex]);

                                                stepCount++;
                                                stepIndex++;

                                            }
                                        }

                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("getStepRequestErrHappen", error.getMessage(), error);
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("X-Ncu-Api-Token", "e763cac7e011b72f1e5d8668cb661070bd130f2109c920a76ca4adb3e540018fcf69115961abae35b0c23a4d27dd7782acce7b75c9dd066053eb0408cb4575b9");
                                    return map;
                                }
                            };

                            mQueue.add(getStepRequest);


                        }
                    });


                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                    {
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


}



/*
            //在每次做刪除、編輯、複製等操作前要先確認所有project 的 在後端中的id
            StringRequest apiRequest = new StringRequest("http://140.115.3.188:3000/sop/v1/processes/", new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Log.d("TAG", response);

                    try
                    {
                        JSONArray array = new JSONArray(response);

                        for(int i=0;i<array.length();i++)
                        {
                            //初始化project相關的資訊
                            p[i] = new Project(i,Integer.parseInt(array.getJSONObject(i).getString("id")), array.getJSONObject(i).getString("name"));
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("TAG", error.getMessage(), error);
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("X-Ncu-Api-Token", "e763cac7e011b72f1e5d8668cb661070bd130f2109c920a76ca4adb3e540018fcf69115961abae35b0c23a4d27dd7782acce7b75c9dd066053eb0408cb4575b9");
                    return map;
                }
            };

            mQueue.add(apiRequest);
*/