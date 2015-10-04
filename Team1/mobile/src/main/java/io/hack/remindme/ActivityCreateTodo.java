package io.hack.remindme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.hack.remindme.database.DatabaseHandler;
import io.hack.remindme.database.Todo;

/**
 * Created by iQube_2 on 10/2/2015.
 */
public class ActivityCreateTodo extends AppCompatActivity implements View.OnClickListener{

    EditText what,when;
    Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_todo);
        what=(EditText)findViewById(R.id.todo_desc);
        when=(EditText)findViewById(R.id.todo_when);
        create=(Button)findViewById(R.id.create);
        create.setOnClickListener(this);
        when.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment datePickerFragment = new DatePickerFragment() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            Log.d("asd", "onDateSet");
                            SimpleDateFormat df=new SimpleDateFormat("dd-MM-yyyy");
                            Calendar c = Calendar.getInstance();
                            c.set(year, month, day);
                            when.setText(df.format(c.getTime()));
                            create.requestFocus(); //moves the focus to something else after dialog is closed
                        }
                    };
                    datePickerFragment.show(ActivityCreateTodo.this.getSupportFragmentManager(), "datePicker");
                }
            }
        });

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //blah
        }
    }

    @Override
    public void onClick(View view) {

        if(view==create)
        {
            String description=what.getText().toString();
            String Wdate=when.getText().toString();
            Todo todo=new Todo(description,Wdate);
            DatabaseHandler db= new DatabaseHandler(this);
            db.createTodo(todo);


            //asd
            Intent i=new Intent(this,ActivityRemindMe.class);
            startActivity(i);
        }
    }

    public void createNot()
    {
        int notificationId = 001;
// Build intent for notification content
        Intent viewIntent = new Intent(this, ActivityRemindMe.class);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Finish it")
                        .setSmallIcon(R.drawable.powered_by_google_light)
                        .setContentText("Description")
                        .setContentIntent(viewPendingIntent);

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
