package ouyj.hyena.com.badypinyin.data;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ouyj.hyena.com.badypinyin.R;

/**
 * 自定义适配器类
 */
public class AlphabetAdapter extends BaseAdapter {

    private Context context;
    private String[] array;

    /**
     * 构造方法（传入上下文和数据源）
     * @param context
     * @param array
     */
    public AlphabetAdapter(Context context, String[] array) {
        super();
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.length;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 确定每一列表项的具体视图
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //每项布局就是一个文本视图
        TextView txtView;
        //为null时创建新文本视图（否则重用）
        if (convertView == null)
            txtView = new TextView(context);
        else
            txtView = (TextView)convertView;

        //设置文本视图样式
        txtView.setText(array[position]);
        txtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        txtView.setTypeface(null, Typeface.BOLD);
        txtView.setPadding(2,2,2,2);

        switch (array[position]) {
            case "¯":
            case "ˊ":
            case "ˇ":
            case "ˋ":
                txtView.setTextColor(context.getResources().getColor(R.color.darkred));
                break;
            default:
                txtView.setTextColor(context.getResources().getColor(R.color.violet));
        }
        txtView.setBackgroundResource(R.drawable.grid_item_border);
        txtView.setGravity(0x11);
        return txtView;
    }
}
