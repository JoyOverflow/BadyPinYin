package ouyj.hyena.com.badypinyin;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ouyj.hyena.com.badypinyin.data.AlphabetAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    //字母网格视图
    private GridView gridView;
    //字母适配器对象
    private AlphabetAdapter adapter;
    //字母表数组
    private String[] alphabetList;
    private String alphabets = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,ü,w,x,y,z,¯,ˊ,ˇ,ˋ";

    /**
     * 构造方法
     */
    public MainFragment() {
        alphabetList = alphabets.split(",");
    }

    /**
     * 片段创建视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //片段的视图
        final View v = inflater.inflate(R.layout.fragment_main, container, false);


        //查找网格视图的引用
        gridView =  v.findViewById(R.id.gridView);
        //创建自定义适配器
        adapter = new AlphabetAdapter(getContext(),alphabetList);
        //为网格设置适配器
        gridView.setAdapter(adapter);
        //设置网格的项点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前被点击的字母
                String content = alphabetList[position];
                String tmp=String.format("当前被点击的字母：%s", content);
                Log.d(MainActivity.TAG, tmp);
            }
        });



        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
