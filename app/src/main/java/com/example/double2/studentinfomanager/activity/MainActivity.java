package com.example.double2.studentinfomanager.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.double2.studentinfomanager.R;
import com.example.double2.studentinfomanager.adapter.ShowAdapter;
import com.example.double2.studentinfomanager.db.DateBaseHelper;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    //控件
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RecyclerView rvMain;
    private FloatingActionButton fabtnAdd;
    //数据存储
    private DateBaseHelper mDateBaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mDateBaseHelper = new DateBaseHelper(this, "CompanyInfo.db", null, 1);
        mSQLiteDatabase = mDateBaseHelper.getReadableDatabase();
        mSharedPreferences = this.getSharedPreferences("student", MODE_PRIVATE);

        setTestData();
        initView();
    }


    private void setTestData() {
        Boolean isFirstStart = mSharedPreferences.getBoolean("is_first_start", true);
        if (true) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("is_first_start", false);
            mEditor.commit();

            ArrayList<String> number = new ArrayList<>();
            ArrayList<String> name = new ArrayList<>();
            String gender = "女";
            String nativePlace = "云南昆明";
            String specialty = "计算机";
            String grade = "字节Swing科技有限公司";
            String birth = "1999年11月9日";

            number.add("153-3393-5536");
            name.add("中国石油天然气集团公司");
            number.add("141-5112-0001");
            name.add("中国石油化工集团公司");
            number.add("151-1200-0333");
            name.add("中国海洋石油总公司");
            number.add("198-3533-0405");
            name.add("中国中信集团有限公司");
            number.add("153-3393-5536");
            name.add("中国中化集团公司");
            number.add("141-5112-0001");
            name.add("中国远洋运输（集团）总公司");
            number.add("151-1200-0333");
            name.add("中国铝业公司");
            number.add("198-3533-0405");
            name.add("中国五矿集团公司");;


            for (int i = 0; i < number.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("number", number.get(i));
                values.put("name", name.get(i));
                values.put("gender", gender);
                values.put("native_place", nativePlace);
                values.put("specialty", specialty);
                values.put("grade", grade);
                values.put("birth", birth);
                mSQLiteDatabase.insert("company", null, values);
            }
        }
    }


    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //将状态栏颜色设置为与toolbar一致
            getWindow().setStatusBarColor(getResources().getColor(R.color.normal_blue));
        }
        setToolBar();
        setNavigationView();

        rvMain = (RecyclerView) findViewById(R.id.rv_main);
        rvMain.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        fabtnAdd = (FloatingActionButton) findViewById(R.id.fabtn_main_add);
        fabtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("type", EditActivity.TYPE_ADD);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        super.onStart();
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        ArrayList<String> number = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> gender = new ArrayList<>();

        Cursor mCursor = mSQLiteDatabase.query("company", null, null, null, null, null, null);

        int size = mCursor.getCount() < ShowAdapter.maxSize ? mCursor.getCount() : ShowAdapter.maxSize;

        while (true) {
            if (size-- == 0)
                break;
            mCursor.moveToNext();
            number.add(mCursor.getString(mCursor.getColumnIndex("number")));
            name.add(mCursor.getString(mCursor.getColumnIndex("name")));
            gender.add(mCursor.getString(mCursor.getColumnIndex("gender")));
        }
        mCursor.close();


        rvMain.setAdapter(new ShowAdapter(MainActivity.this, number, name, gender));
    }

    private void setNavigationView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
        mNavigationView = (NavigationView) findViewById(R.id.nv_main_menu);
        setupDrawerContent(mNavigationView);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("企业信息");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchAction();
                return false;
            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_my_info:
                                intent=new Intent(MainActivity.this,MyInfoActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_password:
                                changePasswordDialog();
                                break;
                            case R.id.nav_search:
                                searchAction();
                                break;
                            case R.id.nav_add:
                                intent = new Intent(MainActivity.this, EditActivity.class);
                                intent.putExtra("type", EditActivity.TYPE_ADD);
                                startActivity(intent);
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void changePasswordDialog() {
        final TableLayout tlPassword = (TableLayout) getLayoutInflater().inflate(R.layout.dialog_main_password, null);
        final EditText etOldPassword = (EditText) tlPassword.findViewById(R.id.et_main_old_password);
        final EditText etNewPassword = (EditText) tlPassword.findViewById(R.id.et_main_new_password);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("修改登录密码")
                .setView(tlPassword)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPassword = mSharedPreferences.getString("password", "1");
                        if (oldPassword.equals(etOldPassword.getText().toString())) {
                            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                            mEditor.putString("password", etNewPassword.getText().toString());
                            mEditor.commit();
                            Toast.makeText(MainActivity.this, "修改密码成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "原密码错误！", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //由于“取消”的button没有设置点击效果，直接设为null就可以了
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void searchAction() {
        final String[] arrayGender = new String[]{"企业电话", "企业名称"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("搜索类型")
                .setItems(arrayGender, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        switch (which) {
                            case 0:
                                intent.putExtra("search_type", SearchActivity.TYPE_SEARCH_NUMBER);
                                break;
                            case 1:
                                intent.putExtra("search_type", SearchActivity.TYPE_SEARCH_NAME);
                                break;
                        }
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}