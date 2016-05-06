package cc.ncu.edu.tw.sop_v1;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ListView.OnItemClickListener{
    //private String[] sopProjectList={"借用場地","網路繳費","宿舍申請"};
    ArrayList<Map<String,Object>> mList = new ArrayList<Map<String,Object>>(); //儲存每個sop project items中的各個物件型態(Image、TextView、三個ImageButton)
    //MainActivity mMainActivity = new MainActivity();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //DrawerLayout的相關設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mMainActivity = this;


        //在sop project items中加入許多物件(Image、TextView、三個ImageButton)
        listView = (ListView)findViewById(R.id.listView);
        String[] listFromResource =getResources().getStringArray(R.array.sopproject);
        for(int i=0;i<listFromResource.length;i++)
        {
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("txtView",listFromResource[i]);
            item.put("add",R.drawable.plus);
            item.put("delete",R.drawable.delete);
            item.put("edit",R.drawable.edit);
            item.put("copy",R.drawable.copy);
            mList.add(item);
        }

        //用自定義的MyAdapter把上面建立好的選單陣列存入此物件,再顯示
        MyAdapter adapter = new MyAdapter(MainActivity.this,mList,R.layout.sop_list_items,new String[] {"txtView","add","delete","edit","copy"}, new int[] {R.id.txtView,R.id.add,R.id.delete,R.id.edit,R.id.copy});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listViewOnItemClick);
    }





    //監聽ListView中哪個選項被選到
    private AdapterView.OnItemClickListener listViewOnItemClick = new  AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent,View view,int position,long id){

           switch(position)
           {
               case 0:
                   Intent it0 = new Intent();
                   it0.setClass(MainActivity.this, borrowspace.class);
                   startActivity(it0);
                   break;
               case 1:
                   Intent it1 =new Intent();
                   it1.setClass(MainActivity.this,networkfee.class);
                   startActivity(it1);
                   break;
               case 2:
                   Intent it2 =new Intent();
                   it2.setClass(MainActivity.this,dormsign.class);
                   startActivity(it2);
                   break;

           }
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    //Navigation Item被點擊的監聽器
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage)
        {

        }
        else if (id == R.id.nav_share)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}







