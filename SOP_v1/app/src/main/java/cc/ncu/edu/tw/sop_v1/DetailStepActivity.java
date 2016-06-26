package cc.ncu.edu.tw.sop_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DetailStepActivity extends AppCompatActivity
{
    private Bundle bundle;
    private Step step;
    EditText stepExamine,unit,person,place;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        stepExamine = (EditText)findViewById(R.id.editStepExamine);
        unit = (EditText)findViewById(R.id.editUnit);
        person = (EditText)findViewById(R.id.editPeople);
        place = (EditText)findViewById(R.id.editPlace);

        //取出從add_new_one.java中Intent所附帶的Step資料
        step = (Step)getIntent().getSerializableExtra("Step");
        stepExamine.setText(step.getItem());
        unit.setText(step.getUnit());
        person.setText(step.getPerson());
        place.setText(step.getPlace());

    }


    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem)
        {
            String msg = "";

            switch (menuItem.getItemId())
            {
                case R.id.action_edit:
                    msg += "Click edit";
                    stepExamine.setEnabled(true);
                    unit.setEnabled(true);
                    person.setEnabled(true);
                    place.setEnabled(true);
                    break;

                case R.id .action_upload:
                    msg += "Click upload";
                    stepExamine.setEnabled(false);
                    unit.setEnabled(false);
                    person.setEnabled(false);
                    place.setEnabled(false);

                case R.id.action_settings:
                    msg += "Click setting";
                    break;
            }

            if(!msg.equals(""))
            {
                Toast.makeText(DetailStepActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_ballot, menu);
        return true;
    }

}
