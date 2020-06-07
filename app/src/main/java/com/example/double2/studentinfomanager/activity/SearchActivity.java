package com.example.double2.studentinfomanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.adapter.SearchAdapter;
import com.example.double2.studentinfomanager.adapter.ShowAdapter;
import com.example.double2.studentinfomanager.db.DateBaseHelper;

import java.util.ArrayList;



public class SearchActivity extends Activity {
    //控件
    private Button btnFront;
    private EditText etSearch;
    private RecyclerView rvSearch;
    //数据存储
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    //变量与常量
    public static final int TYPE_SEARCH_NUMBER = 11;
    public static final int TYPE_SEARCH_NAME = 22;
    private int currentSearchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search);

        receiveSearchType();
        initView();
    }

    private void receiveSearchType() {
        Intent intent = this.getIntent();
        currentSearchType = intent.getIntExtra("search_type", TYPE_SEARCH_NUMBER);
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //将状态栏颜色设置为与toolbar一致
            getWindow().setStatusBarColor(getResources().getColor(R.color.normal_blue));
        }

        btnFront = (Button) findViewById(R.id.btn_search_front);
        btnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etSearch = (EditText) findViewById(R.id.et_search);
        switch (currentSearchType) {
            case TYPE_SEARCH_NUMBER:
                etSearch.setHint("请输入你要搜索的企业电话号");
                break;
            case TYPE_SEARCH_NAME:
                etSearch.setHint("请输入你要搜索的企业名称");
                break;
        }

        rvSearch = (RecyclerView) findViewById(R.id.rv_search);
        rvSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshRecyclerView(s + "");
            }
        });
    }

    private void refreshRecyclerView(String s) {
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();

        mDateBaseHelper = new DateBaseHelper(this, "CompanyInfo.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getReadableDatabase();
        Cursor mCursor;
        switch (currentSearchType) {
            case TYPE_SEARCH_NUMBER:
                mCursor = mSQLiteDatabase.query("company", null, "number like ?", new String[]{"%" + s + "%"}, null, null, null);
                break;
            case TYPE_SEARCH_NAME:
                mCursor = mSQLiteDatabase.query("company", null, "name like ?", new String[]{"%" + s + "%"}, null, null, null);
                break;
            default:
                mCursor = mSQLiteDatabase.query("company", null, "number like ?", new String[]{"%" + s + "%"}, null, null, null);
        }

        int size = mCursor.getCount() < ShowAdapter.maxSize ? mCursor.getCount() : ShowAdapter.maxSize;

        while (true) {
            if (size-- == 0)
                break;
            mCursor.moveToNext();
            number.add(mCursor.getString(mCursor.getColumnIndex("number")));
            name.add(mCursor.getString(mCursor.getColumnIndex("name")));
        }
        mCursor.close();

        rvSearch.setAdapter(new SearchAdapter(SearchActivity.this, number, name));
    }

}
