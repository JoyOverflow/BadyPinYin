package ouyj.hyena.com.badypinyin;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    //字母网格视图
    private GridView gridView;
    //字母适配器对象
    private AlphabetAdapter adapter;

    /**
     * 构造方法
     */
    public MainFragment() {


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
        return inflater.inflate(R.layout.fragment_main, container, false);
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
