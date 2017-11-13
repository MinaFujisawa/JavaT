package com.example.aki.javaq.Domain.Entity;




/**
 * Created by AKI on 2017-06-06.
 */

public class Quiz {

    private int mQuizSectionID;
    private String mQuestionText;
    private String mFirstChoice;
    private String mSecondChoice;
    private String mThirdChoice;
    private String mFourthChoice;
    private int mAnswerIndex;

    public Quiz(int sectionID, String QuestionText, String FirstChoice, String SecondChoice, String ThirdChoice,String FourthChoice, int AnswerIndex) {
        mQuizSectionID = sectionID;
        mQuestionText = QuestionText;
        mFirstChoice = FirstChoice;
        mSecondChoice = SecondChoice;
        mThirdChoice = ThirdChoice;
        mFourthChoice = FourthChoice;
        mAnswerIndex = AnswerIndex;
    }

    public int getmQuizSectionID() {
        return mQuizSectionID;
    }

    public String getmQuestionText() {
        return mQuestionText;
    }

    public String getmFirstChoice() {
        return mFirstChoice;
    }

    public String getmSecondChoice() {
        return mSecondChoice;
    }

    public String getmThirdChoice() {
        return mThirdChoice;
    }

    public String getmFourthChoice() {return mFourthChoice;}

    public int getmAnswerIndex() {
        return mAnswerIndex;
    }

    public void setmQuizNumber(int mQuizNumber) {
        this.mQuizSectionID = mQuizNumber;
    }


    public void setmQuestionText(String mQuestionText) {
        this.mQuestionText = mQuestionText;
    }

    public void setmFirstChoice(String mFirstChoice) {
        this.mFirstChoice = mFirstChoice;
    }

    public void setmSecondChoice(String mSecondChoice) {
        this.mSecondChoice = mSecondChoice;
    }

    public void setmThirsChoice(String mThirsChoice) {
        this.mThirdChoice = mThirsChoice;
    }

    public void setmFourthChoice(String mFourthChoice) {this.mFourthChoice = mFourthChoice;}

    public void setmAnswerIndex(int mAnswerIndex) {
        this.mAnswerIndex = mAnswerIndex;
    }

}
