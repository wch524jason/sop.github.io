package cc.ncu.edu.tw.sop_v1;

/**
 * Created by jason on 2016/5/29.
 */
public class Step
{
    //存放步驟顯示位置的資料
    private boolean parent;  //是否為父層
    private int layer=0;        //為第幾層
    private int sequence;    //在層中的順序
    private String content;
    private boolean exist;   //若為true表示存在  ;  false表示被刪除了
/*=================================================================*/
    //存放步驟內容的資料
    private String stepExamine;
    private String unit;
    private String person;
    private String place;


    public Step(boolean p,int l,int s,String c)
    {
        parent = p;
        layer = l;
        sequence = s;
        content = c;
        exist = true;
    }

    public int getSequence() {return sequence;}
    public void setSequence(int i){sequence+=i;}

    public int getLayer() {return layer;}
    public void setLayer(int j){layer+=j;}

    public String getContent() {return content;}
    public void setContent(String examine,String unit,String person,String place)
    {
        stepExamine = examine;
        this.unit = unit;
        this.person = person;
        this.place = place;
    }

    public boolean getExist(){return exist;}
    public void setExist(){exist=false;}
}