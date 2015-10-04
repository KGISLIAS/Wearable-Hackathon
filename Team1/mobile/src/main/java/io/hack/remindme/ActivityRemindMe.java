package io.hack.remindme;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.hack.remindme.database.DatabaseHandler;
import io.hack.remindme.database.Todo;


public class ActivityRemindMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_remind_me);

        RecyclerView rv=(RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        DatabaseHandler db=new DatabaseHandler(this);
        List<Todo> todos=db.getAllTodos();
        RVAdapter adapter = new RVAdapter(todos);
        rv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_remind_me, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_create_todo:
                //  add new page
                Intent i=new Intent(this,ActivityCreateTodo.class);
                startActivity(i);
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }



    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TodoViewHolder>{
        List<Todo> todos;
        public RVAdapter(List todos)
        {
            this.todos=todos;
        }
        @Override
        public int getItemCount() {
            return todos.size();
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_todo, viewGroup, false);
            TodoViewHolder evh = new TodoViewHolder(v);
            return evh;
        }
        String months[]= new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        @Override
        public void onBindViewHolder(TodoViewHolder todoViewHolder, int i) {
            todoViewHolder.TodoDescription.setText(todos.get(i).getContent());
            todoViewHolder.todoDoe.setText(todos.get(i).getWdate().substring(0, 2));
            todoViewHolder.todoDoem.setText(months[Integer.parseInt(todos.get(i).getWdate().substring(3, 5)) - 1] + " " + todos.get(i).getWdate().substring(6));

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


        public class TodoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CardView cv;
            TextView TodoDescription;
            TextView todoDoe,todoDoem;
            ImageView close;
            TodoViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv);
                TodoDescription = (TextView)itemView.findViewById(R.id.todo_name);
                todoDoe = (TextView)itemView.findViewById(R.id.event_doe);
                todoDoem=(TextView)itemView.findViewById(R.id.event_doem);
                close=(ImageView)itemView.findViewById(R.id.delete_todo);
                close.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                DatabaseHandler db= new DatabaseHandler(ActivityRemindMe.this);
                int k=this.getAdapterPosition();
                db.deleteTodo(todos.get(k).getId());
                RVAdapter.this.todos.remove(this.getAdapterPosition());
                RVAdapter.this.notifyItemRemoved(this.getAdapterPosition());
                RVAdapter.this.notifyItemRangeChanged(k,getItemCount());
            }
        }

    }



}
