package com.akshat.fireapp;

public class Upload {
    public String mName;
    public String mTextFileUrl;
    public String mDate;
    public Upload()
    {
        //empty constructor needed
    }
    public Upload(String name,String textFileUrl,String date)
    {
        if(name.trim().equals("")){
            name = "No Name";
        }

        mName = name;
        mTextFileUrl = textFileUrl;
        mDate = date;
    }

    public String getmDate() {
        return mDate;
    }


    public String getName(){
        return(mName);
    }


    public String getmTextFileUrl()
    {
        return mTextFileUrl;
    }
}
