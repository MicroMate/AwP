package com.manning.aip.dealdroid.test;

import java.util.List;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;

import com.manning.aip.dealdroid.DealDetails;
import com.manning.aip.dealdroid.DealList;
import com.manning.aip.dealdroid.DealList.ParseFeedTask;
import com.manning.aip.dealdroid.model.Section;

public class DealListTest extends ActivityInstrumentationTestCase2<DealList> {

   public DealListTest() {
      super("com.manning.aip.dealdroid", DealList.class);
   }

   // jeden test z implementacja sceneariusza uzytkownika
   public void testDealListToDetailsUserFlow() throws Exception {
      
      Instrumentation instr = getInstrumentation();   //pobranie referencji do egzemplarza klasy instumentacion
      DealList dealList = getActivity();  //pobranie referencji do aktywnosci DealList

      ParseFeedTask task = dealList.getParseFeedTask();
      assertNotNull("task should not be null", task);

      List<Section> taskResult = task.waitAndUpdate();   //metoda waitAndUpdate() która gwarantuje wywołanie metody onPostExecute.
      boolean noSections = taskResult == null || taskResult.isEmpty();
      assertFalse("task did not return any sections", noSections);

      //upewnienie sie że wątek interfejsu użytkownika jest dostępny 
      //(zakończył przetwarzanie zdarzeń z interfejsu uzytkownika)
      instr.waitForIdleSync(); 
      
      String dealDetails = DealDetails.class.getCanonicalName();
      ActivityMonitor monitor = instr.addMonitor(dealDetails, null, false);  //poinformowanie instrumentalizacji która aktywnośc ma być uruchomiona

      View firstItem = dealList.getListView().getChildAt(0);
      TouchUtils.clickView(this, firstItem);   // klikniecie listy - pierwszego elementu

      assertTrue(instr.checkMonitorHit(monitor, 1));  //oczekujemy że kliknięcie zdarzy sie tylko raz, stąd argument 1

      instr.removeMonitor(monitor);  //każdemu wywołaniu addMonitor musi odpowiadać removeMointor
   }
}
