package cc.ncu.edu.tw.sop;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity {
    private TextView mTxtResult;
    List<Map<String,Object>> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mTxtResult=(TextView)findViewById(R.id.txtResult);
        mList=new ArrayList<Map<String,Object>>();
        String[] listFromProject = getResources().getStringArray(R.array.project);
        String[] listFromNote =getResources().getStringArray(R.array.note);
        for(int i=0;i<listFromProject.length;i++)
        {
            Map<String,Object> item = new HashMap<String,Object>();
            item.put("imgView",android.R.drawable.ic_menu_my_calendar);
            item.put("txtProject",listFromProject[i]);
            item.put("txtNote",listFromNote[i]);
            mList.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,mList,R.layout.list_item,new String[] {"imgView","txtProject","txtNote"},
                                                   new int[] {R.id.imgView,R.id.txtProject,R.id.txtNote});
        setListAdapter(adapter);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ListView listview=getListView();
        listview.setOnItemClickListener(listViewOnItemClick);
    }



    private AdapterView.OnItemClickListener listViewOnItemClick =new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView parent, View view,int position, long id){
          String s=((TextView)view.findViewById(R.id.txtProject)).getText().toString();

          mTxtResult.setText(s);
        }

    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


}
