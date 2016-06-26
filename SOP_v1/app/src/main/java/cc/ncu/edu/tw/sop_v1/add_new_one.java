package cc.ncu.edu.tw.sop_v1;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
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

public class add_new_one extends ExpandableListActivity
{
    //建造AppCompat class中的取的actionbar的方法,為了解決繼承ExpandableListActivity不能使用setSupportActionbar()
    private AppCompatDelegate mDelegate;

    //增加ExpandableListView的參數key值
    private static final String ITEM_NAME = "Item Name";
    private static final String ITEM_SUBNAME = "Item Subname";
    private static final String ITEM_LOGO = "Item Logo";


    private Step[] e = new Step[30];

    private ExpandableListAdapter mExpaListAdap;
    private ExpandableListView mExpaListView;
    private Context mContext;

    //執行時存入的list
    private  List<Map<String,Object>> groupList = new ArrayList<>();
    private  List<List<Map<String,String>>> childList2D = new ArrayList<>();

    private Bundle bundle;

    //紀錄  長按按到的groupPosition、childPosition
    private int groupPosition;
    private int childPosition;

    RequestQueue mQueue;

    //紀錄步驟是屬於第幾個project的
    private int Flow_id;
    private String ACCESS_TOKEN;

    //紀錄該專案中有多少步驟、及索引值
    private int StepCount=0;//為實體化e的個數
    private int index=0;//目前被實體化Step的索引
    private int parentCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //初始化groupList  child2DList
        groupList.clear();
        childList2D.clear();

        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_add_new_one);
        super.onCreate(savedInstanceState);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionbar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_new_one);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final View dialogLayout = LayoutInflater.from(add_new_one.this).inflate(R.layout.add_step_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(add_new_one.this);
                builder.setTitle("新增步驟");
                builder.setView(dialogLayout);
                builder.setCancelable(false);

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    EditText mEdtStepName;
                    EditText mEdtStepExam;
                    EditText mEdtUnit;
                    EditText mEdtPeople;
                    EditText mEdtPlace;

                    public void onClick(DialogInterface dialog, int id)
                    {
                        mEdtStepName = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepName);
                        mEdtStepExam = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepExam);
                        mEdtUnit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtUnit);
                        mEdtPeople = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPeople);
                        mEdtPlace = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPlace);

                        //Toast.makeText(add_new_one.this,"成功新增了步驟", Toast.LENGTH_LONG).show();
                        StepCount++;


                        e[index] = new Step(groupList.size() + 1, 0, mEdtStepName.getText().toString(),Flow_id,0);
                        e[index].setContent(mEdtStepExam.getText().toString(), mEdtUnit.getText().toString(), mEdtPeople.getText().toString(), mEdtPlace.getText().toString());
                        index++;

                        Map<String, Object> group = new HashMap<>();
                        group.put(ITEM_NAME, mEdtStepName.getText().toString());
                        groupList.add(group);
                        parentCount ++;

                        List<Map<String, String>> childList = new ArrayList<>();

                        childList2D.add(childList);

                        //設定ExpandableListAdapter
                        mExpaListAdap = new SimpleExpandableListAdapter(add_new_one.this, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                                , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME},
                                new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);


                        //新增專案到後端(post)
                        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                        {
                            //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/",new JSONObject(jsonObjectRequest.getParams()), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("Successful", response);
                            }
                        }, new Response.ErrorListener()
                        {
                            public void onErrorResponse(VolleyError error)
                            {
                                Log.e("ErrorHappen", error.getMessage(), error);
                            }

                        })
                        {
                            public Map<String, String> getHeaders() throws AuthFailureError
                            {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                return map;
                            }


                            public Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("action", mEdtStepName.getText().toString());
                                map.put("items", mEdtStepExam.getText().toString());
                                map.put("prev", Integer.toString(parentCount));
                                map.put("next", Integer.toString(0));
                                map.put("Flow_id", Integer.toString(Flow_id));
                                map.put("PersonId", "0");
                                map.put("UnitId", mEdtUnit.getText().toString());
                                map.put("PlaceId", "3");
                                return map;
                            }
                        };

                        mQueue.add(postRequest);

                        //新增完後設定新增 step 在後端中的id
                        StringRequest apiRequest = new StringRequest("http://140.115.3.188:3000/sop/v1/steps/", new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("TAG", response);

                                try
                                {
                                    JSONArray array = new JSONArray(response);

                                    e[index-1].setId(Integer.parseInt(array.getJSONObject(index-1).getString("id")));

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
        });


        //繼承ExpandableListView後id為固定android:list所以用getExpandableListView取得id
        mExpaListView = getExpandableListView();

        //初始化mQueue
        mQueue = Volley.newRequestQueue(add_new_one.this);

        //取出從MainActivity中Intent所附帶的Flow_id資料
        Intent it = getIntent();
        bundle = it.getExtras();
        Flow_id = bundle.getInt("Flow_id");
        ACCESS_TOKEN = bundle.getString("Access_token");


        //從後端載入(get)資料
        StringRequest apiRequest = new StringRequest(" http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d("API_response", response);
                int k=0;
                int MaxLayer =0;

                try
                {
                    JSONArray array = new JSONArray(response);
                    for(int i=0;i<array.length();i++)
                    {
                        if(Integer.parseInt(array.getJSONObject(i).getString("flow_id")) == Flow_id)
                        {
                            e[index] = new Step(Integer.parseInt(array.getJSONObject(i).getString("prev")),Integer.parseInt(array.getJSONObject(i).getString("next")),array.getJSONObject(i).getString("action"),Integer.parseInt(array.getJSONObject(i).getString("flow_id")),Integer.parseInt(array.getJSONObject(i).getString("id")));
                            //設定MaxLayer
                            if(e[index].getLayer() > MaxLayer)
                            {
                                MaxLayer = e[index].getLayer();
                            }

                            StepCount++;
                            index++;



                        }
                        //Log.v("This is step",e[index].getContent());
                    }

                    //載入ExpandableListView選單的文字
                    while(k<StepCount)
                    {
                            Map<String, Object> group = new HashMap<>();
                            for(int i=1;i<=MaxLayer;i++)
                            {
                                List<Map<String, String>> childList = new ArrayList<>();
                                childList2D.add(childList);
                            }

                            //ExpandableListView第一層
                            if (e[k].getSequence() == 0)
                            {
                                group.put(ITEM_NAME, e[k].getContent());
                                groupList.add(group);
                                parentCount++;
                                k++;
                            }
                            else
                            {
                                //ExpandableListView第二層
                                Map<String, String> child = new HashMap<>();

                                child.put(ITEM_SUBNAME, e[k].getContent());

                                //childList2D.get(e[k].getLayer()-1).add(child);
                                childList2D.get(e[k].getLayer()-1).add(child);
                                k++;
                            }

                    }

                    //調整ChildList 、 GroupList 中的順序
                    int n=0;
                    while(n<StepCount)
                    {
                        //調整GroupList 內的順序
                        if(e[n].getSequence()==0)
                        {
                            Map<String, Object> group = new HashMap<>();
                            group.put(ITEM_NAME,e[n].getContent());
                            groupList.set(e[n].getLayer()-1,group);
                        }
                        //調整ChildList內的順序
                        else
                        {
                            //ExpandableListView第二層
                            Map<String, String> child = new HashMap<>();
                            child.put(ITEM_SUBNAME, e[n].getContent());
                            childList2D.get(e[n].getLayer()-1).set(e[n].getSequence()-1,child);

                        }
                        n++;
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                //設定ExpandableListAdapter
                mExpaListAdap = new SimpleExpandableListAdapter(add_new_one.this, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                        , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME},
                        new int[]{android.R.id.text1});

                mExpaListView.setAdapter(mExpaListAdap);

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

        //註冊能夠接收Context Menu事件的元件
        registerForContextMenu(mExpaListView);
        mContext = add_new_one.this;

        //設判斷長按父層或子層的監聽器
        mExpaListView.setOnItemLongClickListener(expandListViewOnItemLongClick);
    }
//========================================================  OnCreate 結束  =======================================================================//


    //判斷選到ExpandableListView選單中哪個選項(短按)
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id)
    {
        // TODO Auto-generated method stub

        switch(groupPosition)
        {
            default:
                switch(childPosition)
                {

                    default:
                        Intent it = new Intent();
                        it.setClass(add_new_one.this, DetailStepActivity.class);
                        int temp=0;
                        //找出所按位置step的 e[i]
                        for(int i=0;i<StepCount;i++)
                        {
                            if(e[i].getLayer()==groupPosition && e[i].getSequence()==childPosition)
                            {
                                temp =i;
                            }
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Step", e[temp]);
                        it.putExtras(bundle);
                        startActivity(it);
                        break;
                }
                break;
        }
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }



    //發生"長按"情況,系統呼叫此方法建立Context Menu,並顯示在畫面上
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v==mExpaListView)
        {
            if(menu.size()==0)
            {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu_expandable_listview,menu);
            }
        }
    }



    //判斷使用者點選Context Menu中的項目,執行對應的工作
    public boolean onContextItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.contextMenuItemLook:
                Intent it = new Intent();
                it.setClass(add_new_one.this, DetailStepActivity.class);
                startActivity(it);
                break;

            //新增單一步驟
            case R.id.contextMenuItemAddCommon:
                final View addCommonStepLayout = LayoutInflater.from(add_new_one.this).inflate(R.layout.add_step_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(add_new_one.this);
                builder.setTitle("新增單一步驟");
                builder.setView(addCommonStepLayout);
                builder.setCancelable(false);

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    EditText mEdtStepName;
                    EditText mEdtStepExam;
                    EditText mEdtUnit;
                    EditText mEdtPeople;
                    EditText mEdtPlace;

                    //暫時紀錄第幾個step的順序要改變
                    int temp;


                    public void onClick(DialogInterface dialog, int id)
                    {
                        mEdtStepName = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepName);
                        mEdtStepExam = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepExam);
                        mEdtUnit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtUnit);
                        mEdtPeople = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPeople);
                        mEdtPlace = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPlace);


                        e[index] = new Step(groupPosition, childPosition + 1, mEdtStepName.getText().toString(),Flow_id,0);
                        e[index].setContent(mEdtStepExam.getText().toString(),mEdtUnit.getText().toString(),mEdtPeople.getText().toString(),mEdtPlace.getText().toString());
                        index++;

                        //插入新步驟後其他item做的調整  (後端)
                        for (int i = 0; i < StepCount ; i++)
                        {
                            if (e[i].getLayer() == groupPosition && e[i].getSequence() >= childPosition + 1)
                            {
                                e[i].setSequence(1);
                                temp =i;
                                //做完調整後上傳的動作 (put)
                                StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://140.115.3.188:3000/sop/v1/steps/"+Integer.toString(e[temp].getId()), new Response.Listener<String>()
                                {
                                    //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/",new JSONObject(jsonObjectRequest.getParams()), new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        Log.d("Successful", response);
                                    }
                                }, new Response.ErrorListener()
                                {
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        Log.e("ErrorHappen", error.getMessage(), error);
                                    }

                                })
                                {
                                    public Map<String, String> getHeaders() throws AuthFailureError
                                    {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                        return map;
                                    }


                                    public Map<String, String> getParams() throws AuthFailureError
                                    {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("next", Integer.toString(e[temp].getSequence()));

                                        return map;
                                    }
                                };

                                mQueue.add(putRequest);
                            }
                        }

                        StepCount++;

                        //新增一般步驟後post到後端的動作
                        StringRequest postRequest = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                        {
                            //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/",new JSONObject(jsonObjectRequest.getParams()), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("Successful", response);
                            }
                        }, new Response.ErrorListener()
                        {
                            public void onErrorResponse(VolleyError error)
                            {
                                Log.e("ErrorHappen", error.getMessage(), error);
                            }

                        })
                        {
                            public Map<String, String> getHeaders() throws AuthFailureError
                            {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                return map;
                            }


                            public Map<String, String> getParams() throws AuthFailureError
                            {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("action", mEdtStepName.getText().toString());
                                map.put("items", mEdtStepExam.getText().toString());
                                map.put("prev", Integer.toString(groupPosition));
                                map.put("next", Integer.toString(childPosition + 1));
                                map.put("Flow_id", Integer.toString(Flow_id));
                                map.put("PersonId", "1");
                                map.put("UnitId", "2");
                                map.put("PlaceId", "3");
                                return map;
                            }
                        };

                        mQueue.add(postRequest);

                        //新增完後設定新增 step 在後端中的id
                        StringRequest apiRequest = new StringRequest("http://140.115.3.188:3000/sop/v1/steps/", new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("TAG", response);
                                try
                                {
                                    JSONArray array = new JSONArray(response);

                                    e[index-1].setId(Integer.parseInt(array.getJSONObject(index-1).getString("id")));

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



                        //List<Map<String, String>> childList = new ArrayList<>();
                        Map<String, String> child = new HashMap<>();
                        child.put(ITEM_SUBNAME, mEdtStepName.getText().toString());
                        //childList.add(child);
                        childList2D.get(groupPosition - 1).add(childPosition, child);

                        //設定Adapter
                        mExpaListAdap = new SimpleExpandableListAdapter(mContext, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                                , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME}, new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;


            //新增平行步驟
            case R.id.contextMenuItemAddParallel:
                final View addParallelStepLayout = LayoutInflater.from(add_new_one.this).inflate(R.layout.add_parallel_step_dialog, null);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(add_new_one.this);
                builder2.setTitle("新增平行步驟");
                builder2.setView(addParallelStepLayout);
                builder2.setCancelable(false);

                builder2.setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    int insertItem = 0;//計算加入多少的步驟
                    int temp;
                    EditText mEdtParallelStep1Name;
                    EditText mEdtParallelStep2Name;
                    EditText mEdtParallelStep3Name;
                    EditText mEdtParallelStep4Name;
                    EditText mEdtParallelStep5Name;
                    String[] ParaName = new String[5];

                    public void onClick(DialogInterface dialog, int id)
                    {
                        mEdtParallelStep1Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep1Name);
                        mEdtParallelStep2Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep2Name);
                        mEdtParallelStep3Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep3Name);
                        mEdtParallelStep4Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep4Name);
                        mEdtParallelStep5Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep5Name);

                        ParaName[0] = mEdtParallelStep1Name.getText().toString();
                        ParaName[1] = mEdtParallelStep2Name.getText().toString();
                        ParaName[2] = mEdtParallelStep3Name.getText().toString();
                        ParaName[3] = mEdtParallelStep4Name.getText().toString();
                        ParaName[4] = mEdtParallelStep5Name.getText().toString();

                        for (int i = 0; i < 5; i++)
                        {
                            //第一個加入的是父層
                            if (i == 0)
                            {
                                parentCount ++;
                                StepCount++;
                                e[index] = new Step(groupPosition + 1, i, ParaName[i],Flow_id,0);
                                index++;
                                insertItem ++;
                            }
                            else if (ParaName[i].length() != 0 && i!=0)
                            {
                                //parentCount ++;
                                StepCount++;
                                e[index] = new Step(groupPosition + 1, i, ParaName[i],Flow_id,0);
                                index++;
                                insertItem ++;
                            }
                            else if (ParaName[i].length() == 0)
                            {
                                break;
                            }
                        }

                        //加入平行步驟後其他Step類別做的調整
                        for (int j = 0; j < StepCount - insertItem; j++)
                        {
                            if (e[j].getLayer() >= groupPosition + 1)
                            {
                                e[j].setLayer(1);
                                temp =j;

                                //做完調整後上傳的動作 (put)
                                StringRequest putRequest = new StringRequest(Request.Method.PUT, "http://140.115.3.188:3000/sop/v1/steps/"+Integer.toString(e[temp].getId()), new Response.Listener<String>()
                                {
                                    //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/",new JSONObject(jsonObjectRequest.getParams()), new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        Log.d("Successful", response);
                                    }
                                }, new Response.ErrorListener()
                                {
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        Log.e("ErrorHappen", error.getMessage(), error);
                                    }

                                })
                                {
                                    public Map<String, String> getHeaders() throws AuthFailureError
                                    {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                        return map;
                                    }


                                    public Map<String, String> getParams() throws AuthFailureError
                                    {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("prev", Integer.toString(e[temp].getLayer()));

                                        return map;
                                    }
                                };

                                mQueue.add(putRequest);




                            }
                        }

                        //ExpandableListView第一層的加入動作
                        Map<String, Object> group = new HashMap<>();
                        //Map<String,Object> paraIcon = new HashMap<>();
                        group.put(ITEM_NAME, mEdtParallelStep1Name.getText().toString());
                        //group.put(ITEM_LOGO, R.drawable.parallel);
                        groupList.add(groupPosition, group);


                        //ExpandableListView第二層的加入動作
                        List<Map<String, String>> childList = new ArrayList<>();

                        for (int i = 1; i < ParaName.length; i++)
                        {
                            Map<String, String> child = new HashMap<>();
                            if (ParaName[i].length() == 0)
                            {
                                break;
                            }

                            child.put(ITEM_SUBNAME, ParaName[i]);
                            childList.add(child);
                        }
                        childList2D.add(groupPosition, childList);



                        //新增平行步驟post到後端的動作
                        if(mEdtParallelStep1Name.getText().toString().length()!=0)
                        {
                            StringRequest stringRequest0 = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d("Successful", response);
                                }
                            }, new Response.ErrorListener()
                            {
                                public void onErrorResponse(VolleyError error)
                                {
                                    Log.e("ErrorHappen", error.getMessage(), error);
                                }

                            })
                            {
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Authorization","Bearer"+" "+ACCESS_TOKEN);
                                    return map;
                                }

                                public Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();

                                    map.put("action", mEdtParallelStep1Name.getText().toString());
                                    map.put("items","");
                                    map.put("prev",Integer.toString(groupPosition+1));
                                    map.put("next","0");
                                    map.put("Flow_id",Integer.toString(Flow_id));
                                    map.put("PersonId","1");
                                    map.put("UnitId","1");
                                    map.put("PlaceId","1");

                                    return map;
                                }
                            };
                            mQueue.add(stringRequest0);
                        }

                        if(mEdtParallelStep2Name.getText().toString().length()!=0)
                        {
                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d("Successful", response);
                                }
                            }, new Response.ErrorListener()
                            {
                                public void onErrorResponse(VolleyError error)
                                {
                                    Log.e("ErrorHappen", error.getMessage(), error);
                                }

                            })
                            {
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Authorization","Bearer"+" "+ACCESS_TOKEN);
                                    return map;
                                }

                                public Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();

                                    map.put("action", mEdtParallelStep2Name.getText().toString());
                                    map.put("items","");
                                    map.put("prev",Integer.toString(groupPosition+1));
                                    map.put("next","1");
                                    map.put("Flow_id",Integer.toString(Flow_id));
                                    map.put("PersonId","1");
                                    map.put("UnitId","1");
                                    map.put("PlaceId","1");

                                    return map;
                                }
                            };
                            mQueue.add(stringRequest1);
                        }

                        if(mEdtParallelStep3Name.getText().toString().length()!=0)
                        {
                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d("Successful", response);
                                }
                            }, new Response.ErrorListener()
                            {
                                public void onErrorResponse(VolleyError error)
                                {
                                    Log.e("ErrorHappen", error.getMessage(), error);
                                }

                            })
                            {
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Authorization","Bearer"+" "+ACCESS_TOKEN);
                                    return map;
                                }

                                public Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();

                                    map.put("action", mEdtParallelStep3Name.getText().toString());
                                    map.put("items","");
                                    map.put("prev",Integer.toString(groupPosition+1));
                                    map.put("next","2");
                                    map.put("Flow_id",Integer.toString(Flow_id));
                                    map.put("PersonId","1");
                                    map.put("UnitId","1");
                                    map.put("PlaceId","1");

                                    return map;
                                }
                            };
                            mQueue.add(stringRequest2);
                        }

                        if(mEdtParallelStep4Name.getText().toString().length()!=0)
                        {
                            StringRequest stringRequest3 = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d("Successful", response);
                                }
                            }, new Response.ErrorListener()
                            {
                                public void onErrorResponse(VolleyError error)
                                {
                                    Log.e("ErrorHappen", error.getMessage(), error);
                                }

                            })
                            {
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Authorization","Bearer"+" "+ACCESS_TOKEN);
                                    return map;
                                }

                                public Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();

                                    map.put("action", mEdtParallelStep4Name.getText().toString());
                                    map.put("items","");
                                    map.put("prev",Integer.toString(groupPosition+1));
                                    map.put("next","3");
                                    map.put("Flow_id",Integer.toString(Flow_id));
                                    map.put("PersonId","1");
                                    map.put("UnitId","1");
                                    map.put("PlaceId","1");

                                    return map;
                                }
                            };
                            mQueue.add(stringRequest3);
                        }

                        if(mEdtParallelStep5Name.getText().toString().length()!=0)
                        {
                            StringRequest stringRequest4 = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/steps", new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response)
                                {
                                    Log.d("Successful", response);
                                }
                            }, new Response.ErrorListener()
                            {
                                public void onErrorResponse(VolleyError error)
                                {
                                    Log.e("ErrorHappen", error.getMessage(), error);
                                }

                            })
                            {
                                public Map<String, String> getHeaders() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("Authorization","Bearer"+" "+ACCESS_TOKEN);
                                    return map;
                                }

                                public Map<String, String> getParams() throws AuthFailureError
                                {
                                    Map<String, String> map = new HashMap<>();

                                    map.put("action", mEdtParallelStep5Name.getText().toString());
                                    map.put("items","");
                                    map.put("prev",Integer.toString(groupPosition+1));
                                    map.put("next","4");
                                    map.put("Flow_id",Integer.toString(Flow_id));
                                    map.put("PersonId","1");
                                    map.put("UnitId","1");
                                    map.put("PlaceId","1");

                                    return map;
                                }
                            };
                            mQueue.add(stringRequest4);
                        }


                        //新增完後設定新增 step 在後端中的id
                        StringRequest apiRequest = new StringRequest("http://140.115.3.188:3000/sop/v1/steps/", new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d("TAG", response);

                                try
                                {
                                    JSONArray array = new JSONArray(response);
                                    for(int i=StepCount-insertItem;i<StepCount;i++)
                                    {
                                        e[i].setId(Integer.parseInt(array.getJSONObject(i).getString("id")));
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



                        //設定Adapter
                        mExpaListAdap = new SimpleExpandableListAdapter(mContext, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME, ITEM_LOGO}
                                , new int[]{android.R.id.text1, R.drawable.parallel}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME}, new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);
                    }
                });

                AlertDialog alert2 = builder2.create();
                alert2.show();

                //Log.v("id", String.valueOf(id));
                break;

            case R.id.contextMenuItemDelete:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(add_new_one.this);

                //點擊到父層
                if(childPosition == 0)
                {
                    builder3.setMessage("確定要刪除整個平行步驟?");
                }
                else
                {
                    builder3.setMessage("確定要刪除此步驟?");
                }
                builder3.setCancelable(false);

                builder3.setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    StringRequest deleteRequest[] = new StringRequest[5];
                    StringRequest changeRequest[] = new StringRequest[StepCount];
                    StringRequest changeRequest2[] =new StringRequest[StepCount];
                    int deletedIndex = 0;      //紀錄要被刪除的步驟在e[]中的index
                    int changeIndex =0;       //紀錄要被調整的步驟在e[]中的index
                    int counter=0;
                    int counter2=0;
                    int counter3=0;

                    public void onClick(DialogInterface dialog, int id)
                    {
                        if (childPosition == 0)    //刪除平行步驟
                        {
                            for(int i = 0;i<StepCount ;i++)
                            {
                                //刪掉父層在groupList中做的調整
                                if (e[i].getLayer() == groupPosition && e[i].getSequence() == 0)
                                {
                                    deletedIndex = i;
                                    groupList.remove(groupPosition - 1);

                                    //刪除平行步驟 後端所做的處理
                                    deleteRequest[counter] = new StringRequest(Request.Method.DELETE, "http://140.115.3.188:3000/sop/v1/steps/" + Integer.toString(e[deletedIndex].getId()), new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);
                                        }
                                    }, new Response.ErrorListener()
                                    {
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }
                                    })
                                    {
                                        public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer" + " " + ACCESS_TOKEN);
                                            return map;
                                        }
                                    };
                                    mQueue.add(deleteRequest[counter]);
                                    counter++;

                                }
                                //刪掉子層在childList中做的調整
                                else if (e[i].getLayer() == groupPosition && e[i].getSequence() != 0)
                                {
                                    deletedIndex = i;
                                    childList2D.get(groupPosition - 1).remove(0);

                                    //刪除平行步驟 後端所做的處理
                                    deleteRequest[counter] = new StringRequest(Request.Method.DELETE, "http://140.115.3.188:3000/sop/v1/steps/" + Integer.toString(e[deletedIndex].getId()), new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);
                                        }
                                    }, new Response.ErrorListener()
                                    {
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }
                                    })
                                    {
                                        public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer" + " " + ACCESS_TOKEN);
                                            return map;
                                        }
                                    };
                                    mQueue.add(deleteRequest[counter]);
                                    counter++;

                                }



                                //刪除掉平行步驟後端所做的調整
                                if(e[i].getLayer() > groupPosition )
                                {
                                    changeIndex =i;
                                    e[changeIndex].setLayer(-1);


                                    //做完調整後上傳的動作 (put)
                                    changeRequest[counter2] = new StringRequest(Request.Method.PUT, "http://140.115.3.188:3000/sop/v1/steps/"+Integer.toString(e[changeIndex].getId()), new Response.Listener<String>()
                                    {
                                        //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/",new JSONObject(jsonObjectRequest.getParams()), new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);
                                        }
                                    }, new Response.ErrorListener()
                                    {
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }

                                    })
                                    {
                                        public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                            return map;
                                        }


                                        public Map<String, String> getParams() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("prev", Integer.toString(e[changeIndex].getLayer()));

                                            return map;
                                        }
                                    };

                                    mQueue.add(changeRequest[counter2]);
                                    counter2++;
                                }

                            }
                            //做完後要把空的childList刪除掉
                            childList2D.remove(groupPosition - 1);

                        }
                        else                   //刪除一般步驟
                        {
                            //刪除被點擊的步驟
                            for (int i = 0; i < StepCount; i++)
                            {
                                if (e[i].getLayer() == groupPosition && e[i].getSequence() == childPosition)
                                {
                                    deletedIndex = i;

                                    //刪除一般步驟 後端所做的處理
                                    StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "http://140.115.3.188:3000/sop/v1/steps/"+Integer.toString(e[deletedIndex].getId()), new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);
                                        }
                                    }, new Response.ErrorListener(){
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }

                                    })
                                    {
                                        public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                            return map;
                                        }
                                    };

                                    mQueue.add(stringRequest);
                                    break;
                                }
                            }

                            childList2D.get(groupPosition - 1).remove(e[deletedIndex].getSequence()-1);

                            //刪除步驟後其他步驟做的調整
                            //調整如果刪掉的是步驟間的步驟    如果刪掉的是最後一個步驟不用調整
                            for(int j=0;j<StepCount;j++)
                            {
                                if(e[j].getLayer() == groupPosition && e[j].getSequence()>childPosition)
                                {
                                    changeIndex =j;
                                    e[changeIndex].setSequence(-1);


                                    //做完調整後上傳的動作 (put)
                                    changeRequest2[counter3] = new StringRequest(Request.Method.PUT, "http://140.115.3.188:3000/sop/v1/steps/"+Integer.toString(e[changeIndex].getId()), new Response.Listener<String>()
                                    {
                                        //JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/",new JSONObject(jsonObjectRequest.getParams()), new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            Log.d("Successful", response);
                                        }
                                    }, new Response.ErrorListener()
                                    {
                                        public void onErrorResponse(VolleyError error)
                                        {
                                            Log.e("ErrorHappen", error.getMessage(), error);
                                        }

                                    })
                                    {
                                        public Map<String, String> getHeaders() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("Authorization", "Bearer"+" "+ACCESS_TOKEN);
                                            return map;
                                        }


                                        public Map<String, String> getParams() throws AuthFailureError
                                        {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("next", Integer.toString(e[changeIndex].getSequence()));

                                            return map;
                                        }
                                    };
                                    mQueue.add(changeRequest2[counter3]);
                                    counter3++;
                                }

                            }

                        }


                        //設定Adapter
                        mExpaListAdap = new SimpleExpandableListAdapter(mContext, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME, ITEM_LOGO}
                                , new int[]{android.R.id.text1, R.drawable.parallel}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME}, new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);
                    }

                });


                builder3.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }

                });

                builder3.show();
                break;

            case R.id.contextMenuItemEdit:
                AlertDialog.Builder builder4 = new AlertDialog.Builder(add_new_one.this);
                builder4.setTitle("編輯步驟名稱");
                builder4.setView(R.layout.edit_step_dialog);
                builder4.setCancelable(false);

                builder4.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editStepName = (EditText) ((AlertDialog) dialog).findViewById(R.id.editStepName);
                        //Log.v("successful edit step", "新的步驟名稱為:" + editStepName.getText().toString());

                        if (childPosition == 0) {
                            Map<String, Object> newEditItem = new HashMap<String, Object>();
                            newEditItem.put(ITEM_NAME, editStepName.getText().toString());
                            groupList.set(groupPosition - 1, newEditItem);
                        } else {
                            Map<String, String> newEditSubItem = new HashMap<String, String>();
                            newEditSubItem.put(ITEM_SUBNAME, editStepName.getText().toString());
                            childList2D.get(groupPosition - 1).set(childPosition - 1, newEditSubItem);
                        }

                        //設定Adapter
                        mExpaListAdap = new SimpleExpandableListAdapter(mContext, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME, ITEM_LOGO}
                                , new int[]{android.R.id.text1, R.drawable.parallel}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME}, new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);


                    }

                });


                builder4.setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {


                    }

                });
                AlertDialog alert4 = builder4.create();
                alert4.show();
                break;




        }
        return super.onContextItemSelected(item);

    }






    //為了解決繼承ExpandableListActivity不能使用setSupportActionbar()
    private AppCompatDelegate getDelegate()
    {
        if (mDelegate == null)
        {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    public void setSupportActionbar(Toolbar toolbar)
    {
        getDelegate().setSupportActionBar(toolbar);
    }



    //設定長按父層或子層的監聽事件
    private  AdapterView.OnItemLongClickListener expandListViewOnItemLongClick =new AdapterView.OnItemLongClickListener()
    {

        public boolean onItemLongClick(AdapterView<?> parent, View childView, int flatPos, long id)
        {
            long packedPosition = ((ExpandableListView) parent).getExpandableListPosition(flatPos);
            int groupposition = ExpandableListView.getPackedPositionGroup(packedPosition)+1;
            int childposition = ExpandableListView.getPackedPositionChild(packedPosition)+1;
            groupPosition = groupposition;
            childPosition = childposition;

            if (childPosition ==0) //選到父層
            {



            }
            else                    //選到子層
            {



            }
            //Log.v("測試", packedPosition+" ");
            Log.v("測試", " 長按的组群位置：" + groupPosition);
            Log.v("測試", "長按的子項位置:" + childPosition);
            return false;
        }


    };


}
