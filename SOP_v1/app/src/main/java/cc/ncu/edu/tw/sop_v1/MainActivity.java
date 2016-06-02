package cc.ncu.edu.tw.sop_v1;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ListView.OnItemClickListener{
    ArrayList<Map<String,Object>> mList = new ArrayList<Map<String,Object>>(); //儲存每個sop project items中的各個物件型態(Image、TextView、三個ImageButton)
    private ListView listView;

    private Context context;
    private OAuthManager oAuthManager;
    private String ACCESS_TOKEN = "";
    private String[] sopProject = {"場地借用","費用繳交","宿舍申請","","","","","","",""};
    //private String[] sopProject= new String[100];
    private int countProjectNum = 3;

    private EditText editText;
    private MainActivity mainActivity;


    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(ACCESS_TOKEN == "")
                {
                    Toast.makeText(MainActivity.this,R.string.oauth_create_certificate,Toast.LENGTH_LONG).show();
                }

                else
                {
                    final View dialogLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_project_dialog, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("新增專案");
                    builder.setView(dialogLayout);
                    builder.setCancelable(false);

                    builder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {

                                    editText =(EditText) ((AlertDialog) dialog).findViewById(R.id.edtProjectName);
                                    if(!editText.getText().toString().equals(""))
                                    {
                                        Map<String,Object> item = new HashMap<String,Object>();
                                        item.put("txtView",editText.getText().toString());
                                        item.put("delete",R.drawable.delete);
                                        item.put("edit",R.drawable.edit);
                                        item.put("copy",R.drawable.copy);
                                        mList.add(item);
                                        MyAdapter adapter = new MyAdapter(MainActivity.this,mList,R.layout.sop_list_items,new String[] {"txtView","delete","edit","copy"}, new int[] {R.id.txtView,R.id.delete,R.id.edit,R.id.copy},mainActivity);
                                        listView.setAdapter(adapter);
                                        Toast.makeText(getApplicationContext(), "新增了專案"+editText.getText().toString(), Toast.LENGTH_SHORT).show();
                                        countProjectNum+=1;
                                        sopProject[countProjectNum] +=editText.getText().toString();
                                    }

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

        });



        //DrawerLayout的相關設定
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //取得MainActivity context內容
        context = this;
        mainActivity = this;

        //在sop project items中加入許多物件(Image、TextView、三個ImageButton)
        listView = (ListView)findViewById(R.id.listView);


        String[] listFromResource =sopProject;
        for(int i=0;i<listFromResource.length;i++)
        {
            if(listFromResource[i]!="")
            {
                Map<String,Object> item = new HashMap<String,Object>();
                item.put("txtView",listFromResource[i]);
                item.put("delete",R.drawable.delete);
                item.put("edit",R.drawable.edit);
                item.put("copy",R.drawable.copy);
                mList.add(item);
            }

        }

        //用自定義的MyAdapter把上面建立好的選單陣列存入此物件,再顯示
        MyAdapter adapter = new MyAdapter(MainActivity.this,mList,R.layout.sop_list_items,new String[] {"txtView","delete","edit","copy"}, new int[] {R.id.txtView,R.id.delete,R.id.edit,R.id.copy},mainActivity);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listViewOnItemClick);



        //設定搜尋得監聽器

    }



    public ListView getListView()
    {
        return listView;
    }

    //Toolbar上Menu Item 被按下後執行對應的動作
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_certificate:
                    //oauth 認證
                    //oauth要用到的參數
                    final String CREDENTIAL_FILE_NAME = "credential.file";

                    final String AUTH_ENDPOINT_PATH  = "http://140.115.3.188/oauth/oauth/authorize";//拿authorization code(grant)的路徑
                    final String TOKEN_ENDPOINT_PATH = "http://140.115.3.188/oauth/oauth/token";//拿access token的路徑(及refresh token)

                    final String CLIENT_ID = "ZDMzNTYzMjQtMDQ0MC00NzNkLWEzN2UtNzIyYTlmZTI0MzNi";
                    final String CLIENT_SECRET = "d0d1f03e89d305eabbb1a76a670818500e931dee9cbd9260727975a1b145bdbca7eb2002e0c40d5d7573b6d22c89d973d673e0383e82cd7eba43da6d90223279";
                    final String CALL_BACK = "https://github.com/NCU-CC";
                    String scope = "user.info.basic.read";

                    //以JSON格式刊登access token到SharedPreferencesCredentialStore(多型:CredentialStore為SharedPreferencesCredentialStore的父類別)
                    CredentialStore credentialStore = new SharedPreferencesCredentialStore( context, CREDENTIAL_FILE_NAME, new JacksonFactory() );

                    //實作類別OAuthManager需要兩個參數
                    AuthorizationFlow authorizationFlow = null;
                    AuthorizationUIController authorizationUIController = null;

                    //實作AuthorizationFlow的程式碼(需要透過Builder)
                    AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                            BearerToken.authorizationHeaderAccessMethod(),
                            AndroidHttp.newCompatibleTransport(),
                            new JacksonFactory(),
                            new GenericUrl(TOKEN_ENDPOINT_PATH),
                            new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                            CLIENT_ID,
                            AUTH_ENDPOINT_PATH);
                    builder.setCredentialStore(credentialStore);
                    builder.setScopes(Arrays.asList(scope));
                    authorizationFlow = builder.build();

                    //實作AuthorizationUIController的程式碼
                    authorizationUIController = new DialogFragmentController( getSupportFragmentManager() ) {
                        @Override
                        public boolean isJavascriptEnabledForWebView() {
                            return true;
                        }
                        @Override
                        public String getRedirectUri() throws IOException {
                            return CALL_BACK;
                        }
                    };

                    oAuthManager = new OAuthManager(authorizationFlow, authorizationUIController);
                    oAuthManager.deleteCredential("user", null, null);

                    new AuthTask().execute();
                    break;

                case R.id.menuItemSearch:

                    Toast.makeText(MainActivity.this,"成功新增了步驟", Toast.LENGTH_LONG).show();

                    break;

                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }
            return true;
        }
    };


    private class AuthTask extends AsyncTask<Void, Void, Void> {
        private boolean authSuccess = true;
        private String accessToken=null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Credential:存放密碼的密碼庫(放access token)
                //每次使用authorizeExplicitly時會自動檢查access token有沒有過期
                Credential authResult = oAuthManager.authorizeExplicitly("user",null,null).getResult();
                accessToken = authResult.getAccessToken();
                //String refreshToken = authResult.getRefreshToken();
                Log.e("debug","access Token: " + accessToken);
                ACCESS_TOKEN = accessToken;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {}
    }





    //監聽ListView中哪個選項被選到
    private AdapterView.OnItemClickListener listViewOnItemClick = new  AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent,View view,int position,long id)
        {
            //產生與專案個數相同的Activity
            Intent[] it =new Intent[countProjectNum];

           switch(position)
           {
               case 0:
                   it[0] = new Intent();
                   it[0].setClass(MainActivity.this, borrowspace.class);
                   startActivity(it[0]);
                   break;
               case 1:
                   it[1] = new Intent();
                   it[1].setClass(MainActivity.this, networkfee.class);
                   startActivity(it[1]);
                   break;
               case 2:
                   it[2] = new Intent();
                   it[2].setClass(MainActivity.this, dormsign.class);
                   startActivity(it[2]);
                   break;

               default:
                   Intent intent = new Intent();
                   intent.setClass(MainActivity.this, add_new_one.class);
                   startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem menuSearchItem = menu.findItem(R.id.menuItemSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(queryListener);

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

    //
    public String getACCESS_TOKEN()
    {
        return ACCESS_TOKEN;
    }



    //實作搜尋widget
    final private android.support.v7.widget.SearchView.OnQueryTextListener queryListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String newText)
        {

            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            Toast.makeText(MainActivity.this,"搜尋了"+query,Toast.LENGTH_SHORT).show();
            return false;
        }
    };



}







