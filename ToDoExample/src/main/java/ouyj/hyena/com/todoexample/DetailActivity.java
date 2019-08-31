package ouyj.hyena.com.todoexample;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ouyj.hyena.com.todoexample.data.TodoTable;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.category)
    Spinner mCategory;
    @Bind(R.id.todo_edit_summary)
    EditText mTitleText;
    @Bind(R.id.todo_edit_description)
    EditText mBodyText;
    @Bind(R.id.fab)

    FloatingActionButton fab;
    private Uri todoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        //设置工具栏替换掉动作栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //返回到父活动
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getBundleData();
    }
    /**
     * 从意图中获取数据
     */
    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            todoUri = bundle.getParcelable(ToDoProvider.CONTENT_ITEM_TYPE);
            fillData(todoUri);
        }
    }
    private void fillData(Uri uri) {
        String[] projection = {
                TodoTable.COLUMN_SUMMARY,
                TodoTable.COLUMN_DESCRIPTION,
                TodoTable.COLUMN_CATEGORY
        };
        //查询记录（创建出ContentResolver来调用ContentProvider抽象类）
        Cursor cursor = getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );
        if (cursor != null) {
            cursor.moveToFirst();
            String category = cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_CATEGORY));
            //列表框选中当前项
            for (int i = 0; i < mCategory.getCount(); i++) {
                String s = (String) mCategory.getItemAtPosition(i);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }
            //设置文本视图的内容
            mTitleText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
            mBodyText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));
            //关闭游标
            cursor.close();
        }
    }

    @OnClick(R.id.fab)
    void addToDo() {
        saveState();
        if (TextUtils.isEmpty(mTitleText.getText().toString())) {
            Toast.makeText(DetailActivity.this, R.string.enter_summary, Toast.LENGTH_LONG).show();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }
    private void saveState() {
        //获取视图内容
        String category = (String) mCategory.getSelectedItem();
        String summary = mTitleText.getText().toString();
        String description = mBodyText.getText().toString();

        //内容为空则退出
        if (description.length() == 0 && summary.length() == 0) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(TodoTable.COLUMN_CATEGORY, category);
        values.put(TodoTable.COLUMN_SUMMARY, summary);
        values.put(TodoTable.COLUMN_DESCRIPTION, description);

        //判断是新增加还是修改记录
        if (todoUri == null)
            todoUri = getContentResolver().insert(
                    ToDoProvider.CONTENT_URI,
                    values
            );
        else
            getContentResolver().update(
                    todoUri,
                    values,
                    null,
                    null
            );
    }
}
