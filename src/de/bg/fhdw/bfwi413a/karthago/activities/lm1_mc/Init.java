//Leonie
package de.bg.fhdw.bfwi413a.karthago.activities.lm1_mc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

//author: Leonie







import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import de.bg.fhdw.bfwi413a.karthago.Navigation;
import de.bg.fhdw.bfwi413a.karthago.R;
import de.bg.fhdw.bfwi413a.karthago.SessionManagement;
import de.bg.fhdw.bfwi413a.karthago.db.DatabaseHandler;
import de.bg.fhdw.bfwi413a.karthago.xml.Results;
import de.bg.fhdw.bfwi413a.karthago.xml.XMLDomParserAndHandler;

public class Init extends Activity{
	
	private Data mData;
	private Gui mGui;
	private ApplicationLogic mApplicationLogic;
	
	//@author Patrick
	XMLDomParserAndHandler xmlhandler;
	ArrayList<String> QuestionAndAnswers;
	ArrayList<String> CorrectAnswers;
	Results result = new Results();
	TextView question;
	TextView leveltext;
	CheckBox answer1;
	CheckBox answer2;
	CheckBox answer3;
	CheckBox answer4;
	String questionID;
	Button confirm;
	private de.bg.fhdw.bfwi413a.karthago.activities.selection.ApplicationLogic ApplicationLogicSelection;
	private DatabaseHandler dbhandler;
	ArrayList<String> UserAnswers;
	
	
	
	public void onCreate(Bundle savedInstanceState) {
		//@author Leonie
        super.onCreate(savedInstanceState);
        questionID = getIntent().getExtras().getString("currentQuestionId");
		initData(savedInstanceState);
		initGui();
		initApplicationLogic();
		initEventToListenerMapping();
		//end @author Leonie
		
        setContentView(R.layout.activity_lm1_mc);
        xmlhandler = new XMLDomParserAndHandler(getApplicationContext());
        QuestionAndAnswers = new ArrayList<String>();
        result = xmlhandler.getRequiredQuestionAnswersAndCorrectAnswers(questionID);
        QuestionAndAnswers = result.get_list_Question_and_Answers();
        CorrectAnswers = result.get_list_correct_answers();
        dbhandler = new DatabaseHandler(getApplicationContext());
        ApplicationLogicSelection = new de.bg.fhdw.bfwi413a.karthago.activities.selection.ApplicationLogic();
        SessionManagement session = new SessionManagement(getApplicationContext());
        final String user = session.getUserDetails();
        final String cardfile = session.getCardfileID();
        UserAnswers = new ArrayList<String>();
        
        question = (TextView) findViewById(R.id.question);
        leveltext = (TextView) findViewById(R.id.textview_level_mc);
		answer1 = (CheckBox) findViewById(R.id.answer1);
		answer2 = (CheckBox) findViewById(R.id.answer2);
		answer3 = (CheckBox) findViewById(R.id.answer3);
		answer4 = (CheckBox) findViewById(R.id.answer4);
		confirm = (Button) findViewById(R.id.confirm);
		
		question.setText(QuestionAndAnswers.get(0).toString());
		String textForLevel = new String();
        textForLevel = "Level: " + dbhandler.getCurrentLevelForQuestionId(questionID);
        leveltext.setText(textForLevel);
		answer1.setText(QuestionAndAnswers.get(1).toString());
		answer2.setText(QuestionAndAnswers.get(2).toString());
		answer3.setText(QuestionAndAnswers.get(3).toString());
		answer4.setText(QuestionAndAnswers.get(4).toString());
		
		
		confirm.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean rightORwrong = true;
				String event_name = "correct";
				
				if(answer1.isChecked()){
					UserAnswers.add(answer1.getText().toString());
				}
				
				if(answer2.isChecked()){
					UserAnswers.add(answer2.getText().toString());
				}
				
				if(answer3.isChecked()){
					UserAnswers.add(answer3.getText().toString());
				}
				
				if(answer4.isChecked()){
					UserAnswers.add(answer4.getText().toString());
				}
				
				if(UserAnswers.size() == CorrectAnswers.size()){
					for(int i = 0; i < CorrectAnswers.size(); i++){
						if(UserAnswers.get(i).toString().equals(CorrectAnswers.get(i).toString())){
							
						}else{
							rightORwrong = false;
							event_name = "incorrect";
							break;
						}
					}
				}else{
					rightORwrong = false;
					event_name = "incorrect";
				}
				
				if(rightORwrong == true){
					Toast toast = Toast.makeText(getApplicationContext(), "Die Antwort war richtig!", Toast.LENGTH_LONG);
					toast.show();
				}else{
					String corAns = new String();
					for(int i = 0; i < CorrectAnswers.size(); i++){
						corAns = corAns + ", " + (CorrectAnswers.get(i).toString());
					}
					StringBuilder sb = new StringBuilder(corAns);
					sb.deleteCharAt(0);
					String corAnsClean = sb.toString();
					Toast toast = Toast.makeText(getApplicationContext(), "Die Antwort war falsch!\nRichtige Antworten: " + corAnsClean, Toast.LENGTH_LONG);
					toast.show();
				}
				
				
				Timestamp tstamp = new Timestamp(new Date().getTime());
				dbhandler.IncreaseOrDecreaseLevelAndSetNewTimestamp(rightORwrong, questionID, tstamp.getTime());
				dbhandler.insertEvent(event_name, tstamp.getTime(), user, cardfile);
				finish();
				ApplicationLogicSelection.startSingleQuestion(Init.this);
			}
		});
		
		
	}	
	// ---- END @author Patrick ----
	
	
	//@author Leonie 
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void initData(Bundle savedInstanceState) {
		mData = new Data(this, savedInstanceState);
	}
	
	private void initGui() {
		mGui = new Gui(this);
	}
	
	private void initApplicationLogic() {
		mApplicationLogic = new ApplicationLogic(mGui, mData, questionID, getApplicationContext());
	}
	
	private void initEventToListenerMapping() {
		new EventToListenerMapping(mGui, mApplicationLogic);
	}
	
	public boolean onKeyDown(int keycode, KeyEvent event){
		//ABFANGEN DES "ZURÜCK-TASTE" EVENTS
		  if(keycode==KeyEvent.KEYCODE_BACK){
			  //RUFE MENÜ-ACTIVITY AUF
		   Navigation.startActivityMenu(mData.getmActivity());
		  }
		 return false;
		 }
	
	//@end author Leonie

}
