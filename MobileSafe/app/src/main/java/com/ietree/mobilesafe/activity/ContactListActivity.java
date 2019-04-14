package com.ietree.mobilesafe.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ietree.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 联系人列表界面
 */
public class ContactListActivity extends AppCompatActivity {

    private static final String TAG = "ContactListActivity";
    private ListView lv_contact;
    private List contactList = new ArrayList<HashMap<String, String>>();
    private MyAdapter mAdapter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 填充数据适配器
            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_list);

        initUI();

        initData();
    }

    private void initUI() {
        lv_contact = findViewById(R.id.lv_contact_list);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    // 获取选中的数据
                    HashMap<String, String> item = mAdapter.getItem(position);
                    String phone = item.get("phone");

                    // 将选中的数据传递给另一个Activity
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);
                    finish();
                }
            }
        });
    }

    /**
     * 获取系统联系人方法
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                // 1、获取内容解析器对象
                ContentResolver resolver = getContentResolver();
                // 2、查询系统联系人数据库表
                Cursor cursor = resolver.query(
                        Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,
                        null,
                        null);
                contactList.clear();
                // 3、循环游标，直到没有数据为止
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    Log.i(TAG, "id = " + id);
                    Cursor indexCursor = resolver.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id},
                            null);
                    HashMap<String, String> hashMap = new HashMap<>();
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);
                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                            }
                        } else if (type.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                    }
                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                // 发送一个空消息机制告诉主线程数据已经填充好
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return (HashMap<String, String>) contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_phone = view.findViewById(R.id.tv_phone);

            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return view;
        }
    }
}
