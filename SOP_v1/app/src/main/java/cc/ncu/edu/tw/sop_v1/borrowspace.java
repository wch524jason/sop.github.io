package cc.ncu.edu.tw.sop_v1;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class borrowspace extends ExpandableListActivity {
    private static final String ITEM_NAME = "Item Name";
    private static final String ITEM_SUBNAME = "Item Subname";
    private String[] projectItem= {"登記申請","繳交費用","場地復原與歸還"};
    private String[][] subItem={{"抽籤","排隊"},{},{}};
    private ExpandableListAdapter mExpaListAdap;
    private ExpandableListView mExpaListView;

    //建造AppCompat class中的取的actionbar的方法,為了解決繼承ExpandableListActivity不能使用setSupportActionbar()
    private AppCompatDelegate mDelegate;

    private Dialog addDlg;
    List<Map<String,String>> groupList = new ArrayList<>();
    List<List<Map<String,String>>> childList2D = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_borrowspace);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_borrowspace);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionbar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        );

        //繼承ExpandableListView後id為固定android:list所以用getExpandableListView取得id
        mExpaListView = getExpandableListView();
        //註冊能夠接收Context Menu事件的元件
        registerForContextMenu(mExpaListView);

        //載入ExpandableListView選單的文字
        for(int i=0;i<projectItem.length;i++)
        {
            Map<String,String> group = new HashMap<>();
            group.put(ITEM_NAME, projectItem[i]);
            groupList.add(group);

            List<Map<String,String>> childList = new ArrayList<>();
            for(int j=0;j<subItem[i].length;j++)
            {
                if(subItem[i].length==0)
                {
                    break;
                }

                Map<String,String> child = new HashMap<>();
                    child.put(ITEM_SUBNAME,subItem[i][j]);
                    childList.add(child);

            }
            childList2D.add(childList);
        }


        //設定ExpandableListAdapter
        mExpaListAdap = new SimpleExpandableListAdapter(this,groupList,android.R.layout.simple_expandable_list_item_2,new String[]{ITEM_NAME}
                         ,new int[] {android.R.id.text1},childList2D,android.R.layout.simple_expandable_list_item_2,new String[] {ITEM_SUBNAME},
                         new int[] {android.R.id.text1}) ;

        mExpaListView.setAdapter(mExpaListAdap);
    }

    //判斷選到ExpandableListView選單中哪個選項
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // TODO Auto-generated method stub

        switch(groupPosition) {
            case 0:
                switch(childPosition) {
                    case 0:
                        Intent it = new Intent();
                        it.setClass(this, ballot.class);
                        startActivity(it);
                        break;
                    /*
                    case 1:
                        Intent it2 = new Intent();
                        it2.setClass(MainActivity.this, drawer.class);
                        startActivity(it2);
                        break;
                    */
                }
                break;
        }
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    //發生"長按"情況,系統呼叫此方法建立Context Menu,並顯示在畫面上
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v==mExpaListView){
            if(menu.size()==0){
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu_expandable_listview,menu);
            }
        }
    }

    //判斷使用者點選Context Menu中的項目,執行對應的工作
    public boolean onContextItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id){
            case R.id.contextMenuItemAdd:
                addDlg = new Dialog(this);
                addDlg.setTitle("建立新步驟");
                addDlg.setCancelable(false);
                addDlg.setContentView(R.layout.add_step_dialog);
                Button addBtnOK = (Button)addDlg.findViewById(R.id.btnOK);
                Button addBtnCancel = (Button)addDlg.findViewById(R.id.btnCancel);
                addBtnOK.setOnClickListener(addDlgBtnOKOnClick);
                addBtnCancel.setOnClickListener(addDlgBtnCancelOnClick);
                addDlg.show();
                break;

            case R.id.contextMenuItemDelete:


                break;

            case R.id.contextMenuItemEdit:


                break;

            case R.id.contextMenuItemCopy:


                break;
        }
        return super.onContextItemSelected(item);

    }

    //按下新增步驟dialog中"確定新增"按鈕的處理
    private View.OnClickListener addDlgBtnOKOnClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            addDlg.cancel();
        }
    };


    //按下新增步驟dialog中"取消新增"按鈕的處理
    private View.OnClickListener addDlgBtnCancelOnClick = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            addDlg.cancel();
        }
    };






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


}
