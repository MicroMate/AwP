package com.manning.aip.dealdroid.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

import com.manning.aip.dealdroid.DealDetails;
import com.manning.aip.dealdroid.DealDroidApp;
import com.manning.aip.dealdroid.R;
import com.manning.aip.dealdroid.model.Item;

public class DealDetailsTest extends ActivityUnitTestCase<DealDetails> {

	   private Item testItem;

	   public DealDetailsTest() {
	      super(DealDetails.class);
	   }

	   //metoda setUp() służy do inicjowania testów
	   @Override
	   protected void setUp() throws Exception {  
	      super.setUp();
	      
	      //deklaracja fikcyjnych danych
	      testItem = new Item();
	      testItem.setItemId(1);
	      testItem.setTitle("Test Item");
	      testItem.setConvertedCurrentPrice("1");
	      testItem.setLocation("USA");
	      testItem.setDealUrl("http://example.com");

	      DealDroidApp application = new DealDroidApp();
	      application.setCurrentItem(testItem);
	      setApplication(application);	//dołączenie egzemplarza aplikacji
	   }

	   //Metody testowe:
	   
	   //metoda wymaga aby widoki były poprawne
	   public void testPreConditions() {
		  //metoda odzwierciedla uruchomienie aktywności DealDetails, bez rzeczywistego jej uruchomienia
		  //nastepuje posrednie wywołanie metody onCreate, ale bez wywołań innych uchwytów cyklu życia związanych z pełnym uruchomieniem aktywności
	      startActivity(new Intent(getInstrumentation().getTargetContext(),
	               DealDetails.class), null, null);

	      Activity activity = getActivity();
	      assertNotNull(activity.findViewById(R.id.details_price));
	      assertNotNull(activity.findViewById(R.id.details_title));
	      assertNotNull(activity.findViewById(R.id.details_location));
	   }

	   //metoda sprawdza czy ustawione widoki wyswietlaja poprawne dane
	   public void testThatAllFieldsAreSetCorrectly() {
	      startActivity(new Intent(getInstrumentation().getTargetContext(),
	               DealDetails.class), null, null);

	      assertEquals("$" + testItem.getConvertedCurrentPrice(),
	               getViewText(R.id.details_price));
	      assertEquals(testItem.getTitle(), getViewText(R.id.details_title));
	      assertEquals(testItem.getLocation(), getViewText(R.id.details_location));
	   }
	   
	   //sprawdza czy wcisniecie przycisku menu o indrntyfikatorze MENU_BROWSE 
	   //powoduje wyswietlenie intencji wyswietlającej oferte na podstawie jej adresu URL
	   public void testThatItemCanBeDisplayedInBrowser() {
	      startActivity(new Intent(getInstrumentation().getTargetContext(),
	               DealDetails.class), null, null);
	      
	      //
	      getInstrumentation().invokeMenuActionSync(getActivity(),
	               DealDetails.MENU_BROWSE, 0);

	      Intent browserIntent = getStartedActivityIntent();
	      assertEquals(Intent.ACTION_VIEW, browserIntent.getAction());
	      assertEquals(testItem.getDealUrl(), browserIntent.getDataString());
	   }

	   
	   private String getViewText(int textViewId) {
	      return ((TextView) getActivity().findViewById(textViewId)).getText()
	               .toString();
	   }
	}
