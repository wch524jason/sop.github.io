package cc.ncu.edu.tw.expandablelistview;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatDelegate;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ExpandableListActivity {
    private AppCompatDelegate mDelegate; //建造AppCompat class中的取的actionbar的方法

    private static final String ITEM_NAME = "Item Name";
    private static final String ITEM_SUBNAME = "Item Subname";
    private ExpandableListAdapter mExpaListAdap;

    //影像按鈕元件
    private ImageButton mcreateProject;
    private ImageButton mdeleteProject;
    private ImageButton meditProject;
    private Dialog mDlgCreateProject;
    private TextView mTxtResult;

    private ExpandableListView mExpandableListView; //註冊使用Context Menu的元件


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
        getDelegate().setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionbar(toolbar);


        mcreateProject=(ImageButton)findViewById(R.id.createProject);
        mdeleteProject=(ImageButton)findViewById(R.id.deleteProject);
        meditProject=(ImageButton)findViewById(R.id.editProject);
        mTxtResult=(TextView)findViewById(R.id.txtResult);

        mcreateProject.setOnClickListener(imgbtnCreateProject);
        mdeleteProject.setOnClickListener(imgbtnDeleteProject);
        meditProject.setOnClickListener(imgbtnEditProject);

        //註冊使用Context Menu的元件
        mExpandableListView = getExpandableListView();
        registerForContextMenu(mExpandableListView);


        List<Map<String,String>> groupList = new ArrayList<Map<String,String>>();
        List<List<Map<String,String>>> childList2D = new ArrayList<List<Map<String,String>>>();

        for(int i=1;i<5;i++){
            Map<String,String> group =new HashMap<String,String>();
            group.put(ITEM_NAME,"專案"+i);
            group.put(ITEM_SUBNAME, "說明" + i);
            groupList.add(group);

            List<Map<String,String>> childList = new ArrayList<Map<String, String>>();
            for(int j=1;j<3;j++){
                Map<String,String> child = new HashMap<String,String>();
                child.put(ITEM_NAME,"步驟"+i+j);
                child.put(ITEM_SUBNAME, "說明" + i+j);
                childList.add(child);
            }

            childList2D.add(childList);
        }

        mExpaListAdap = new SimpleExpandableListAdapter(
                this,groupList,android.R.layout.simple_expandable_list_item_2,new String[] {ITEM_NAME,ITEM_SUBNAME},
                new int[] {android.R.id.text1,android.R.id.text2},
                childList2D,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {ITEM_NAME,ITEM_SUBNAME},
                new int[] {android.R.id.text1,android.R.id.text2}
        );
        setListAdapter(mExpaListAdap);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    //
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    //
    public void setSupportActionbar(Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }




    //=============================================================================================//
    @Override
    //發生"長按"情況,系統呼叫此方法建立Context Menu,並顯示在畫面上
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v==mExpandableListView){
            if(menu.size()==0){
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu_expandable_listview,menu);
            }
        }
    }


    //按下Context Menu選單中按鈕執行的事情
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        onOptionsItemSelected(item);
        return super.onContextItemSelected(item);
    }

    @Override

    //按下Context Menu選單按鈕後執行對應的事情
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()){
            case R.id.menuItemEdit:
                String edit="";
                edit+="你按下了編輯步驟";
                mTxtResult.setText(edit);
            break;

            case R.id.menuItemDelete:
                String delete="";
                delete+="你按下了編輯步驟";
                mTxtResult.setText(delete);
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    //=============================================================================================//
    //設定當按下createProject圖示按鈕顯示的Dialog對話框
    private View.OnClickListener imgbtnCreateProject = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mTxtResult.setText("");

            mDlgCreateProject = new Dialog(MainActivity.this);
            mDlgCreateProject.setTitle("新增Project");
            mDlgCreateProject.setCancelable(false);
            mDlgCreateProject.setContentView(R.layout.create_project_dialog);
            Button createBtnOK=(Button)mDlgCreateProject.findViewById(R.id.btnOK);
            Button createBtnCancel=(Button)mDlgCreateProject.findViewById(R.id.btnCancel);
            createBtnOK.setOnClickListener(createDlgBtnOKOnClick);
            createBtnCancel.setOnClickListener(createDlgBtnCancelOnClick);
            mDlgCreateProject.show();
        }
    };
    //createProject圖示按鈕顯示的Dialog按下"確認"按鈕時執行的狀況
    private View.OnClickListener createDlgBtnOKOnClick=new View.OnClickListener(){
      public void onClick(View v){
          EditText edtProjectName =(EditText)mDlgCreateProject.findViewById(R.id.edtProjectName);
          EditText edtProjectTitle=(EditText)mDlgCreateProject.findViewById(R.id.edtProjectTitle);
          EditText edtProjectExamine=(EditText)mDlgCreateProject.findViewById(R.id.edtProjectExamine);
          String display="新增的專案名稱:"+edtProjectName.getText().toString()+
                  "\n專案標題:"+edtProjectTitle.getText().toString()+
                  "\n專案簡述:"+edtProjectExamine.getText().toString();
          mTxtResult.setText(display);
          mDlgCreateProject.dismiss();
      }
    };
    //createProject按鈕顯示的Dialog按下"取消"按鈕時執行的狀況
    private View.OnClickListener createDlgBtnCancelOnClick=new View.OnClickListener(){
        public void onClick(View v){
            mTxtResult.setText("你按下\"取消\"按鈕");
            mDlgCreateProject.dismiss();
        }
    };



    //=============================================================================================//
    //設定當按下deleteProject圖示按鈕顯示的Dialog對話框
    private View.OnClickListener imgbtnDeleteProject = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTxtResult.setText("");
            mDlgCreateProject = new Dialog(MainActivity.this);
            mDlgCreateProject.setTitle("刪除專案");
            mDlgCreateProject.setCancelable(false);
            mDlgCreateProject.setContentView(R.layout.delete_project_dialog);
            Button deleteBtnOK = (Button)mDlgCreateProject.findViewById(R.id.btnOK);
            Button deleteBtnCancel = (Button)mDlgCreateProject.findViewById(R.id.btnCancel);
            deleteBtnOK.setOnClickListener(deleteDlgBtnOKOnClick);
            deleteBtnCancel.setOnClickListener(deleteDlgBtnCancelOnClick);
            mDlgCreateProject.show();
        }
    };
    //deleteProject圖示按鈕顯示的Dialog按下"確認"按鈕時執行的狀況
    private View.OnClickListener deleteDlgBtnOKOnClick=new View.OnClickListener(){
        public void onClick(View v){
            EditText enterDeleteProjectName =(EditText)mDlgCreateProject.findViewById(R.id.enterDeleteProjectName);
            String display="你刪除了" + enterDeleteProjectName.getText().toString();
            mTxtResult.setText(display);
            mDlgCreateProject.dismiss();
        }
    };
    //createProject按鈕顯示的Dialog按下"取消"按鈕時執行的狀況
    private View.OnClickListener deleteDlgBtnCancelOnClick=new View.OnClickListener(){
        public void onClick(View v){
            mTxtResult.setText("你按下\"取消\"按鈕");
            mDlgCreateProject.dismiss();
        }
    };



    //=============================================================================================//
    //設定當按下editProject圖示按鈕顯示的Dialog對話框
    private View.OnClickListener imgbtnEditProject = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mTxtResult.setText("");
            mDlgCreateProject = new Dialog(MainActivity.this);
            mDlgCreateProject.setTitle("修改專案");
            mDlgCreateProject.setCancelable(false);
            mDlgCreateProject.setContentView(R.layout.edit_project_dialog);
            Button editBtnOK = (Button) mDlgCreateProject.findViewById(R.id.btnOK);
            Button editBtnCancel = (Button) mDlgCreateProject.findViewById(R.id.btnCancel);
            editBtnOK.setOnClickListener(editDlgBtnOKOnClick);
            editBtnCancel.setOnClickListener(editDlgBtnCancelOnClick);
            mDlgCreateProject.show();
        }
    };
    //editProject圖示按鈕顯示的Dialog按下"確認"按鈕時執行的狀況
    private View.OnClickListener editDlgBtnOKOnClick=new View.OnClickListener(){
        public void onClick(View v){
            EditText edtReviseProjectName =(EditText)mDlgCreateProject.findViewById(R.id.edtReviseProjectName);
            EditText edtReviseProjectTitle=(EditText)mDlgCreateProject.findViewById(R.id.edtReviseProjectTitle);
            EditText edtReviseProjectExamine=(EditText)mDlgCreateProject.findViewById(R.id.edtReviseProjectExamine);
            String display="修改後的專案名稱:"+edtReviseProjectName.getText().toString()+
                    "\n修改後專案標題:"+edtReviseProjectTitle.getText().toString()+
                    "\n修改後專案簡述:"+edtReviseProjectExamine.getText().toString();
            mTxtResult.setText(display);
            mDlgCreateProject.dismiss();
        }
    };
    //editProject按鈕顯示的Dialog按下"取消"按鈕時執行的狀況
    private View.OnClickListener editDlgBtnCancelOnClick=new View.OnClickListener(){
        public void onClick(View v){
            mTxtResult.setText("你按下\"取消\"按鈕");
            mDlgCreateProject.dismiss();
        }
    };



    //=============================================================================================//
    //判斷選到ExpandableListView選單中哪個選項
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // TODO Auto-generated method stub

        switch(groupPosition) {
            case 0:
                switch(childPosition) {
                    case 0:
                        Intent it = new Intent();
                        it.setClass(MainActivity.this, Project1Step1Activity.class);
                        startActivity(it);
                        break;
                    case 1:
                        Intent it2 = new Intent();
                        it2.setClass(MainActivity.this, Project1Step2Activity.class);
                        startActivity(it2);
                        break;
                }
                break;
        }
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }


    @Override
    //設定Action View(收尋功能)的事件listener
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        SearchView searchView = (SearchView)menu.findItem(R.id.menuItemSearch).getActionView();
        searchView.setOnQueryTextListener(searchViewOnQueryTextLis);

        return true;
    }

    private SearchView.OnQueryTextListener searchViewOnQueryTextLis = new SearchView.OnQueryTextListener(){
      public boolean onQueryTextChange(String newText){
          return false;
      }

      public boolean onQueryTextSubmit(String query){
          Toast.makeText(MainActivity.this,query,Toast.LENGTH_LONG).show();
          return true;
      }
    };



}
