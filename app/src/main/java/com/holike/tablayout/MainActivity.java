package com.holike.tablayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.galloped.tablayout.tab.Tab;
import com.galloped.tablayout.tab.TabEntity;
import com.galloped.tablayout.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        List<Tab> tabList = new ArrayList<>();
        tabList.add(new TabEntity("首页", ContextCompat.getDrawable(this, R.mipmap.tab_home_sel), ContextCompat.getDrawable(this, R.mipmap.tab_home_nor)));
        tabList.add(new TabEntity("客户", ContextCompat.getDrawable(this, R.mipmap.tab_customer_sel), ContextCompat.getDrawable(this, R.mipmap.tab_customer_nor)));
        tabList.add(new TabEntity("订单"));
        tabList.add(new TabEntity("我的"));
        tabLayout.setupTab(tabList);
    }
}
