package ouyj.hyena.com.badypinyin;

import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ouyj.hyena.com.badypinyin.data.AlphabetAdapter;
import ouyj.hyena.com.badypinyin.model.CharacterInfo;

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
    //汉字上方的进度栏
    private LinearLayout linearProgress;
    //存放汉字数组
    CharacterInfo[] characterList;
    //当前汉字（汉字数组的索引）
    private int index = 0;
    //加载器ID
    static final int PINYIN_LOADER = 0;

    private static Typeface tf;
    private final MediaPlayer media;
    private TextView txtCharacter,txtPinyin;
    private ImageView imgDelete;

    /**
     * 构造方法
     */
    public MainFragment() {
        alphabetList = alphabets.split(",");
        media  = new MediaPlayer();
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
        linearProgress = v.findViewById(R.id.btnProgress);


        //为文本视图（汉卡）设置字体
        tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/simkai.ttf");
        txtCharacter = v.findViewById(R.id.txtCharacter);
        txtCharacter.setTypeface(tf);


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
        //字母删除按钮
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //删除一个字母并重新设置视图文本
                String content = txtPinyin.getText().toString();
                txtPinyin.setText(content.substring(0, content.length() - 1));

                //设置图像按钮的可见性
                if (txtPinyin.getText().toString().length() == 0) {
                    imgDelete.setVisibility(View.GONE);
                }
            }
        });
        txtCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharacterInfo c=characterList[index];
                readCharacter(c.sound);
            }
        });
        //开始播放音频
        media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        return v;
    }
    /**
     * 播放汉字卡片的发音
     * @param sound
     */
    private void readCharacter(String sound){
        //播放Assets目录内的音频（汉字发音）
        try {
            media.reset();
            AssetFileDescriptor file = getActivity().getAssets().openFd(
                    "characters/" + sound + ".mp3"
            );
            media.setDataSource(
                    file.getFileDescriptor(),
                    file.getStartOffset(),
                    file.getLength()
            );
            file.close();
            media.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * 在不带音调的拼音串内返回它音调可能所在的字符
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
    public void onResume()
    {
        super.onResume();

        //创建指定大小的对象数组
        characterList = new CharacterInfo[5];
        //实例化数组元素
        characterList[0] = new CharacterInfo();
        characterList[0].character = "大";
        characterList[0].id = 32;
        characterList[0].sound = "da4";
        characterList[0].pinyin = convertToTone("da","4");

        characterList[1] = new CharacterInfo();
        characterList[1].character = "小";
        characterList[1].id = 37;
        characterList[1].sound = "xiao3";
        characterList[1].pinyin = convertToTone("xiao","3");

        characterList[2] = new CharacterInfo();
        characterList[2].character = "飞";
        characterList[2].id = 69;
        characterList[2].sound = "fei1";
        characterList[2].pinyin = convertToTone("fei","1");

        characterList[3] = new CharacterInfo();
        characterList[3].character = "马";
        characterList[3].id = 72;
        characterList[3].sound = "ma3";
        characterList[3].pinyin = convertToTone("ma","3");

        characterList[4] = new CharacterInfo();
        characterList[4].character = "云";
        characterList[4].id = 82;
        characterList[4].sound = "yun2";
        characterList[4].pinyin = convertToTone("yun","2");


        //设置当前的汉字
        index = 0;
        txtCharacter.setText(characterList[index].character);

        //移除所有子视图
        linearProgress.removeAllViews();
        for(int i=0;i< characterList.length;i++) {
            //加入与数组元素相同数目的白方块
            ImageView img = new ImageView(getActivity());
            img.setImageResource(R.drawable.white);
            img.setTag(i);
            img.setPadding(2, 2, 2, 2);
            //加入父视图中
            linearProgress.addView(img);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        media.release();
    }


    /**
     * 父活动创建完成后的回调
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化加载管理器（如果指定ID的加载器不存在，则触发onCreateLoader执行）
        getLoaderManager().initLoader(
                PINYIN_LOADER,
                null,
                this
        );
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(MainActivity.TAG, "onCreateLoader！");
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(MainActivity.TAG, "onLoadFinished！");
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(MainActivity.TAG, "onLoaderReset！");
    }
}
