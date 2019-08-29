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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    //音调数组
    String [][]toneList = {{"¯a","ā"},{"ˊa","á"},{"ˇa","ǎ"},{"ˋa","à"},
            {"¯o","ō"},{"ˊo","ó"},{"ˇo","ǒ"},{"ˋo","ò"},
            {"¯e","ē"},{"ˊe","é"},{"ˇe","ě"},{"ˋe","è"},
            {"¯i","ī"},{"ˊi","í"},{"ˇi","ǐ"},{"ˋi","ì"},
            {"¯u","ū"},{"ˊu","ú"},{"ˇu","ǔ"},{"ˋu","ù"},
            {"¯ü","ǖ"},{"ˊü","ǘ"},{"ˇü","ǚ"},{"ˋü","ǜ"},
    };



    private TextView txtPinyin;
    private ImageView imgDelete;

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


        txtPinyin = v.findViewById(R.id.txtPinYin);
        imgDelete = v.findViewById(R.id.imgDelete);





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

                //目前已有的拼音文本（已输入）
                String havePinyin = txtPinyin.getText().toString();

                //当前被点击的字母
                String input = alphabetList[position];
                String tmp=String.format("目前已有拼音：%s；当前被点击字母：%s", havePinyin, input);
                Log.d(MainActivity.TAG, tmp);

                //当ü遇到j,q,x,y时要去掉它两点
                if (input.equals("ü") &&
                        (havePinyin.contains("j") ||
                                havePinyin.contains("q") ||
                                havePinyin.contains("x") ||
                                havePinyin.contains("y"))) {
                    input = "u";
                }
                //判断content字符是否为音标
                if (position < 26){
                    //当输入a-z间的字母时（非音标）
                    havePinyin = havePinyin + input;
                } else {
                    //当输入音标时（转换为带正常音标显示的拼音）
                    havePinyin = convertToTone(havePinyin, input);
                }
                //存储输入的拼音文本
                txtPinyin.setText(havePinyin);
                //至少输入一个字符后才会显示删除图像
                if (havePinyin.length() == 1) {
                    imgDelete.setVisibility(View.VISIBLE);
                }







            }
        });



        return v;
    }


    /**
     * 将拼音里的音调值改回为正常音调
     * @param pinYin 例：bi
     * @param tone 例：4或 ¯
     * @return
     */
    private String convertToTone(String pinYin,String tone) {

        //返回音调所在的拼音字母
        String toneLetter = getToneAlphabet(pinYin);

        //转为正确的音调
        tone=tone.trim();
        switch (tone){
            case "1":
                tone = "¯";
                break;
            case "2":
                tone = "ˊ";
                break;
            case "3":
                tone = "ˇ";
                break;
            case "4":
                tone = "ˋ";
                break;
            case "":
            case "0":
                tone = "";
                break;
            default:
                System.out.println("tone本身就是音调");
                break;
        }

        //返回加上音调的拼音串
        String alphabetTone="";
        if (tone.equals("")) {
            //此拼音串无需音调，可直接返回
            alphabetTone=pinYin;
        }
        else{
            //音调加上拼音字母（将类似"¯a"替换为"ā"）
            String flag = tone + toneLetter;
            for (int i = 0; i < toneList.length; i++) {
                //判断字串是否相同
                if (flag.equals(toneList[i][0])) {
                    //执行替换
                    alphabetTone = pinYin.replaceFirst(toneList[i][0].substring(1), toneList[i][1]);
                    break;
                }
            }
        }
        return alphabetTone;
    }
    /**
     * 输入不带音调的拼音串，返回它音调所在的字母
     * @param pinyinString
     * @return
     */
    private String getToneAlphabet(String pinyinString) {

        //设定正则表达式（这些字母上可能出现音调）
        final String format = "[aoeiuü]";
        Pattern pattern = Pattern.compile(format);

        //查找拼音字串中出现过的属于音调数组的字母
        String letters = "";
        Matcher matcher = pattern.matcher(pinyinString);
        while (matcher.find()) {
            letters = letters + matcher.group();
        }
        //System.out.println(String.format("拼音字串中出现了：%s",letters));

        String toneLetter="";
        if(letters.length() == 1) {
            //如果只出现过一个音调字母，那么音调肯定是标在其上
            //System.out.println(String.format("查找结果串长度：%d",letters.length()));
            toneLetter = letters;
        }
        else{
            if (letters.length() == 2 && (letters.contains("ui") || letters.contains("iu"))){
                //System.out.println(String.format("查找结果串长度[ui或iu]：%d",letters.length()));
                //音调会出现在第二个字符上
                toneLetter = letters.substring(1,2);
            }
            else{
                //System.out.println(String.format("查找结果串长度：%d",letters.length()));
                //音调出现的规则
                if (letters.contains("a"))
                    toneLetter = "a";
                else if (letters.contains("o"))
                    toneLetter = "o";
                else if (letters.contains("e"))
                    toneLetter = "e";
                else if (letters.contains("i"))
                    toneLetter = "i";
                else if (letters.contains("u"))
                    toneLetter = "u";
                else if (letters.contains("ü"))
                    toneLetter = "ü";
            }
        }
        //System.out.println(String.format("此字母上出现音调：%s",toneLetter));
        return toneLetter;
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
