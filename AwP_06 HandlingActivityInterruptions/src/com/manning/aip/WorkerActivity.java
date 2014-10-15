package com.manning.aip;

import android.app.Activity;
import android.os.Bundle;

public class WorkerActivity extends Activity {

   private Worker worker;

   @Override
   public void onCreate(Bundle savedInstanceState) { 
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      //Przez wywolanie metody  getLastNonConfigurationInstance() sprawdza czy dostepny jest obiekt roboczy
      //z wczesniejszego egzemplarza danej klasy aktywnosci. Pobiera wartosc (obiekt roboczy) zwrocona przez onRetainNonConfigurationInstance() 
      //Jesli ta wartosc r—wna sie null to wiadomo ze obiekt roboczy nie zosta¸ wczesniej zapisany
      worker = (Worker) getLastNonConfigurationInstance(); //1
      if (worker == null) {
         worker = new Worker();  //nalezy utworzyc nowy obiekt roboczy
         worker.execute();      //i uruchomic zadanie
      }
      //Jezaleznie czy obiekt zosta¸ odtworzony czy utworzono gp po raz pierwszy nalezy wywolac metode
      //Informuje ona obiekt roboczy ze dany egzempaz aktywnosci jest aktualny
      worker.connectContext(this);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();  
      worker.disconnectContext();  //informacja ze dany egzeplarz aktywnosci zostanie usniety
   }

   @Override
   public Object onRetainNonConfigurationInstance() {
      return worker;
   }
}