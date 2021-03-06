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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ListView.OnItemClickListener{
    private ArrayList<Map<String,Object>> mList = new ArrayList<Map<String,Object>>(); //儲存每個sop project items中的各個物件型態(TextView、三個ImageButton)
    private CookieManager cookieManager;


    private ListView listView;
    //private SearchView searchView;
    private MyAdapter adapter;

    private Context context;
    private OAuthManager oAuthManager;
    private String ACCESS_TOKEN = "";


    private Project[] project = new Project[100];

    private int projectNum = 0;

    private EditText editText;
    private EditText searchEdt;

    private MainActivity mainActivity;

    RequestQueue mQueue;

    //紀錄點進去專案的Flow_id
    private int Flow_id;
    private boolean searchFlag=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        cookieManager = CookieManager.getInstance();

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
                                private String newProjectName;
                                public void onClick(DialogInterface dialog, int id)
                                {

                                    editText =(EditText) ((AlertDialog) dialog).findViewById(R.id.edtProjectName);
                                    newProjectName = editText.getText().toString();
                                    if(!editText.getText().toString().equals(""))
                                    {
                                        Map<String,Object> item = new HashMap<String,Object>();
                                        item.put("txtView",editText.getText().toString());
                                        item.put("delete",R.drawable.delete);
                                        item.put("edit",R.drawable.edit);
                                        item.put("copy", R.drawable.copy);
                                        mList.add(item);
                                        adapter = new MyAdapter(MainActivity.this,mList,R.layout.sop_list_items,new String[] {"txtView","delete","edit","copy"}, new int[] {R.id.txtView,R.id.delete,R.id.edit,R.id.copy},mainActivity,project);
                                        listView.setAdapter(adapter);
                                        Toast.makeText(getApplicationContext(), "新增了專案"+editText.getText().toString(), Toast.LENGTH_SHORT).show();
                                        project[projectNum] = new Project(projectNum,0,newProjectName);


                                    }

                                    //post新增的內容到後端
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://140.115.3.188:3000/sop/v1/processes/", new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response)
                                    {
                                        try
                                        {
                                            Log.d("Successful", response);
                                            JSONObject object = new JSONObject(response);
                                            project[projectNum].setProjectId(Integer.parseInt(object.getString("id")));
                                            projectNum+=1;
                                        }
                                        catch(JSONException e)
                                        {
                                            e.printStackTrace();
                                        }


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

                                        public Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("name", newProjectName);
                                            return map;
                                        }
                                    };

                                    mQueue.add(stringRequest);



                                }
                            }
                    );

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
        listView.setTextFilterEnabled(true);
        searchEdt = (EditText) findViewById(R.id.search_box);

        //初始化mQueue
        mQueue = Volley.newRequestQueue(context);


        //從後端取得專案(get)
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
                        project[i] = new Project(i,Integer.parseInt(array.getJSONObject(i).getString("id")), array.getJSONObject(i).getString("name"));

                        Map<String,Object> item = new HashMap<>();
                        item.put("txtView", project[i].getProjectContent());
                        item.put("delete", R.drawable.delete);
                        item.put("edit",R.drawable.edit);
                        item.put("copy", R.drawable.copy);
                        mList.add(item);

                        //project_list.add(project[i]);
                        projectNum++;
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                //用自定義的MyAdapter把上面建立好的選單陣列存入此物件,再顯示
                adapter = new MyAdapter(MainActivity.this,mList,R.layout.sop_list_items,new String[] {"txtView","delete","edit","copy"}, new int[] {R.id.txtView,R.id.delete,R.id.edit,R.id.copy},mainActivity,project);
                listView.setAdapter(adapter);

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




        listView.setOnItemClickListener(listViewOnItemClick);

        searchEdt.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3){}

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){}

            @Override
            public void afterTextChanged(Editable arg0)
            {
                MainActivity.this.adapter.getFilter().filter(arg0);
                searchFlag =true;
                Log.v("afterTextChanged","there is text change");
            }
        });

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
/*
                case R.id.menuItemSearch:

                    Toast.makeText(MainActivity.this,"成功新增了步驟", Toast.LENGTH_LONG).show();

                    break;
*/
                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }
            return true;
        }
    };


    private class AuthTask extends AsyncTask<Void, Void, Void>
    {
        private boolean authSuccess = true;
        private String accessToken=null;

        @Override
        protected Void doInBackground(Void... params) {
            cookieManager.setCookie("portal.ncu.edu.tw", "JSESSIONID=");
            try
            {
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
        //記錄點到的project id(flow_id)是多少

        public void onItemClick(AdapterView parent,View view,int position,long id)
        {
            ArrayList<Map<String,Object>> list;
            //產生與專案個數相同的Activity
            if(!searchFlag)
            {
                Flow_id = project[position].getProjectId();
            }
            else
            {
                list = MainActivity.this.adapter.getAfterFilterList();
                for(int i=0;i<projectNum;i++)
                {
                    if(list.get(position).get("txtView").toString().equals(project[i].getProjectContent()))
                    {
                        Flow_id = project[i].getProjectId();
                        break;
                    }
                }
            }


           switch(position)
           {
               default:
                   Intent intent = new Intent();
                   intent.setClass(MainActivity.this, add_new_one.class);
                   Bundle bundle = new Bundle();
                   bundle.putInt("Flow_id",Flow_id);
                   bundle.putString("Access_token",ACCESS_TOKEN);
                   intent.putExtras(bundle);
                   startActivity(intent);

                   break;
           }



        }
    };


    @Override
    public void onBackPressed()
    {
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
/*
        MenuItem menuSearchItem = menu.findItem(R.id.menuItemSearch);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menuSearchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("請輸入專案名稱");
        //searchView.setOnQueryTextListener(queryListener);
*/

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
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout)
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

    //
    public int getProjectNum()
    {
        return projectNum;
    }

    //在MyAdapter中delete後projectNum減少
    public void setProjectNum()
    {
        projectNum--;
    }




}







