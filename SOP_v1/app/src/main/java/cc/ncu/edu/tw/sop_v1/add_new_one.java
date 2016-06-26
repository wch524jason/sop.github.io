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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_new_one extends ExpandableListActivity
{
    //建造AppCompat class中的取的actionbar的方法,為了解決繼承ExpandableListActivity不能使用setSupportActionbar()
    private AppCompatDelegate mDelegate;

    private MainActivity mainActivity = new MainActivity();

    //增加ExpandableListView的參數key值
    private static final String ITEM_NAME = "Item Name";
    private static final String ITEM_SUBNAME = "Item Subname";
    private static final String ITEM_LOGO = "Item Logo";

    private int parentCount=0;
    private int StepCount=0;//個數為實體化e的個數
    private int index=0;//目前被實體化Step的索引

    private Step[] e = new Step[30];

    private ExpandableListAdapter mExpaListAdap;
    private ExpandableListView mExpaListView;
    private Context mContext;

    //執行時存入的list
    private  List<Map<String,Object>> groupList = new ArrayList<>();
    private  List<List<Map<String,String>>> childList2D = new ArrayList<>();

    private static Bundle bundle;

    //紀錄  長按按到的groupPosition、childPosition
    private int groupPosition;
    private int childPosition;


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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View dialogLayout = LayoutInflater.from(add_new_one.this).inflate(R.layout.add_step_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(add_new_one.this);
                builder.setTitle("新增步驟");
                builder.setView(dialogLayout);
                builder.setCancelable(false);

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText mEdtStepName = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepName);
                        EditText mEdtStepExam = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepExam);
                        EditText mEdtUnit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtUnit);
                        EditText mEdtPeople = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPeople);
                        EditText mEdtPlace = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPlace);

                        //Toast.makeText(add_new_one.this,"成功新增了步驟", Toast.LENGTH_LONG).show();
                        StepCount++;
                        parentCount ++;

                        e[index] = new Step(true, groupList.size() + 1, 0, mEdtStepName.getText().toString());
                        e[index].setContent(mEdtStepExam.getText().toString(), mEdtUnit.getText().toString(), mEdtPeople.getText().toString(), mEdtPlace.getText().toString());
                        index++;

                        Map<String, Object> group = new HashMap<>();
                        group.put(ITEM_NAME, mEdtStepName.getText().toString());
                        groupList.add(group);

                        List<Map<String, String>> childList = new ArrayList<>();

                        childList2D.add(childList);

                        //設定ExpandableListAdapter
                        mExpaListAdap = new SimpleExpandableListAdapter(add_new_one.this, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                                , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME},
                                new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });



        //繼承ExpandableListView後id為固定android:list所以用getExpandableListView取得id
        mExpaListView = getExpandableListView();
        //註冊能夠接收Context Menu事件的元件
        registerForContextMenu(mExpaListView);


        //設定ExpandableListAdapter
        mExpaListAdap = new SimpleExpandableListAdapter(add_new_one.this, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME},
                new int[]{android.R.id.text1});

        mExpaListView.setAdapter(mExpaListAdap);
        mContext = add_new_one.this;

        //設判斷長按父層或子層的監聽器
        mExpaListView.setOnItemLongClickListener(expandListViewOnItemLongClick);

    }

/*
    //紀錄編輯到一半的步驟內容
    protected void onPause()
    {
        Bundle outState = new Bundle();
        super.onPause();
        ArrayList bundlegrouplist = new ArrayList();
        ArrayList bundlechildList2D = new ArrayList();

        bundlegrouplist.add(groupList);
        bundlechildList2D.add(childList2D);

        //儲存UI狀態到bundle中
        outState.putParcelableArrayList("GROUPLIST", bundlegrouplist);
        outState.putParcelableArrayList("CHILDLIST2D", bundlechildList2D);

        setBundle(outState);

        Log.v("這是在測試onPause", "成功了~~~");

    }

    protected  void setBundle(Bundle b)
    {
        bundle =b;
    }

    protected  Bundle getBundle()
    {
        return bundle;
    }

*/
/*
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        ArrayList bundlegrouplist = new ArrayList();
        ArrayList bundlechildList2D = new ArrayList();

        bundlegrouplist.add(groupList);
        bundlechildList2D.add(childList2D);

        //儲存UI狀態到bundle中
        outState.putParcelableArrayList("GROUPLIST", bundlegrouplist);
        outState.putParcelableArrayList("CHILDLIST2D", bundlechildList2D);

        Log.v("onSaveInstanceState被執行了","成功了~~~");
    }
    */

    /*
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList bundlegrouplist = savedInstanceState.getParcelableArrayList("GROUPLIST");
        ArrayList bundlechildList2D = savedInstanceState.getParcelableArrayList("CHILDLIST2D");

        List<Map<String, Object>> groupList= (List<Map<String, Object>>)bundlegrouplist.get(0);
        List<List<Map<String,String>>> childList2D =(List<List<Map<String,String>>>)bundlechildList2D.get(0);

        //設定ExpandableListAdapter
        mExpaListAdap = new SimpleExpandableListAdapter(add_new_one.this, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME},
                new int[]{android.R.id.text1});

        mExpaListView.setAdapter(mExpaListAdap);

        Log.v("onRestoreInstance被執行了", "成功了~~~");
    }
*/


/*
    //重啟Activity時載入介面
    protected void onStart()
    {
        Bundle savedInstanceState = getBundle();
        super.onStart();
        ArrayList bundlegrouplist = savedInstanceState.getParcelableArrayList("GROUPLIST");
        ArrayList bundlechildList2D = savedInstanceState.getParcelableArrayList("CHILDLIST2D");

        List<Map<String, Object>> groupList= (List<Map<String, Object>>)bundlegrouplist;
        List<List<Map<String,String>>> childList2D =(List<List<Map<String,String>>>)bundlechildList2D;


        //設定ExpandableListAdapter
        mExpaListAdap = new SimpleExpandableListAdapter(add_new_one.this, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME}
                , new int[]{android.R.id.text1}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME},
                new int[]{android.R.id.text1});
        mExpaListView.setAdapter(mExpaListAdap);

        Log.v("這是在測試onStart","成功了~~~");
    }
*/

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
                    /*
                                         case 0:
                                        Intent it = new Intent();
                                         it.setClass(this, ballot.class);
                                        startActivity(it);
                                        break;
                                        */
                    default:
                        Intent it = new Intent();
                        it.setClass(add_new_one.this,DetailStepActivity.class);
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
            case R.id.contextMenuItemAddCommon:
                final View addCommonStepLayout = LayoutInflater.from(add_new_one.this).inflate(R.layout.add_step_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(add_new_one.this);
                builder.setTitle("新增一般步驟");
                builder.setView(addCommonStepLayout);
                builder.setCancelable(false);

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText mEdtStepName = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepName);
                        EditText mEdtStepExam = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtStepExam);
                        EditText mEdtUnit = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtUnit);
                        EditText mEdtPeople = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPeople);
                        EditText mEdtPlace = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtPlace);


                        e[index] = new Step(false, groupPosition, childPosition + 1, mEdtStepName.getText().toString());
                        e[index].setContent(mEdtStepExam.getText().toString(),mEdtUnit.getText().toString(),mEdtPeople.getText().toString(),mEdtPlace.getText().toString());
                        index++;

                        //插入新步驟後其他item做的調整
                        for (int i = 0; i < StepCount ; i++)
                        {
                            if (e[i].getLayer() == groupPosition && e[i].getSequence() >= childPosition + 1)
                            {
                                e[i].setSequence(1);
                            }
                        }
                        StepCount++;


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



            case R.id.contextMenuItemAddParallel:
                final View addParallelStepLayout = LayoutInflater.from(add_new_one.this).inflate(R.layout.add_parallel_step_dialog, null);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(add_new_one.this);
                builder2.setTitle("新增平行步驟");
                builder2.setView(addParallelStepLayout);
                builder2.setCancelable(false);


                builder2.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    int insertItem = 0;//計算加入多少的步驟
                    public void onClick(DialogInterface dialog, int id)
                    {
                        EditText mEdtParallelStep1Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep1Name);
                        EditText mEdtParallelStep2Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep2Name);
                        EditText mEdtParallelStep3Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep3Name);
                        EditText mEdtParallelStep4Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep4Name);
                        EditText mEdtParallelStep5Name = (EditText) ((AlertDialog) dialog).findViewById(R.id.edtParallelStep5Name);
                        String[] ParaName = new String[5];
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
                                e[index] = new Step(true, groupPosition + 1, i, ParaName[i]);
                                index++;
                                insertItem ++;
                            }
                            else if (ParaName[i].length() != 0 && i!=0)
                            {
                                //parentCount ++;
                                StepCount++;
                                e[index] = new Step(false, groupPosition + 1, i, ParaName[i]);
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
                    public void onClick(DialogInterface dialog, int id)
                    {
                        int deletedIndex = 0;      //紀錄被刪除的步驟在e[]中的index

                        int deleteNum = 0;        //紀錄被刪掉的Item個數
                        if (childPosition == 0)    //刪除平行步驟
                        {

                            for(int i = 0;i<StepCount ;i++)
                            {
                                if(e[i].getExist())
                                {
                                    //刪掉父層
                                    if(e[i].getLayer() == groupPosition && e[i].getSequence() == 0)
                                    {
                                        //Map<String, Object> group = new HashMap<>();
                                        //Map<String,Object> paraIcon = new HashMap<>();
                                        e[i].setExist();
                                        groupList.remove(groupPosition-1);

                                    }
                                    //刪掉子層
                                    if(e[i].getLayer() == groupPosition && e[i].getSequence()!=0)
                                    {
                                        e[i].setExist();
                                        childList2D.get(groupPosition - 1).remove(0);
                                        deleteNum ++ ;
                                    }

                                    //刪除掉平行步驟後所做的調整
                                    if(e[i].getLayer() > groupPosition )
                                    {
                                        e[i].setLayer(-1);
                                    }
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
                                    e[i].setExist();
                                    deletedIndex = i;
                                    break;
                                }
                            }

                            childList2D.get(groupPosition - 1).remove(e[deletedIndex].getSequence()-1);

                            //刪除步驟後其他步驟做的調整

                            //調整如果刪掉的是步驟間的步驟    如果刪掉的是最後一個步驟不用調整
                            for(int j=0;j<StepCount;j++)
                            {
                                if(e[j].getLayer() == groupPosition && e[j].getExist() && e[j].getSequence()>childPosition)
                                {
                                    e[j].setSequence(-1);
                                }

                            }

                        }


                        //設定Adapter
                        mExpaListAdap = new SimpleExpandableListAdapter(mContext, groupList, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_NAME, ITEM_LOGO}
                                , new int[]{android.R.id.text1, R.drawable.parallel}, childList2D, android.R.layout.simple_expandable_list_item_2, new String[]{ITEM_SUBNAME}, new int[]{android.R.id.text1});
                        mExpaListView.setAdapter(mExpaListAdap);
                    }

                });


                builder3.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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


                builder4.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }

                });
                AlertDialog alert4 = builder4.create();
                alert4.show();
                break;

            case R.id.contextMenuItemCopy:


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
