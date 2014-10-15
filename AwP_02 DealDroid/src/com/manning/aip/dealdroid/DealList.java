package com.manning.aip.dealdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.manning.aip.dealdroid.model.Item;
import com.manning.aip.dealdroid.model.Section;

public class DealList extends ListActivity {

   private static final int MENU_REPARSE = 0;

   private DealDroidApp app;  //klasa DealroidApp - ...
   private List<Item> items;   //standardowa kolekcja javy  List i klasa Item - ...
   private DealsAdapter dealsAdapter; //DealAdapter - wlasny adapter
   private ArrayAdapter<Section> spinnerAdapter;   //klasa Section - ...
   private Spinner sectionSpinner;
   private int currentSelectedSection;
   private ProgressDialog progressDialog;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.deallist);  //za pomaca metody setContentView mozna powiazac aktywnosci z ukladem w formacie xml
      
      //Progres przy ladowaniu danych
      progressDialog = new ProgressDialog(this);
      progressDialog.setCancelable(false);
      progressDialog.setMessage(getString(R.string.deal_list_retrieving_data));
   
      // Use Application object for app wide state
      //kod tworzy obiekt klasy Aplication, uzywany p—zniej do zapisywania stanu globalnego i definiowania metod narzedziowych
      app = (DealDroidApp) getApplication();  //!!!!!!!!

      
    //KONFIGUROWANIE ADAPTERA dla obiektu klasy ListView  (WLASNY ADAPTER zdefinowany dalej)
      // construct Adapter with empty items collection to start
      items = new ArrayList<Item>();
      dealsAdapter = new DealsAdapter(items);   //klasa Adapter udostepnia danych dla listy
      //DealsAdapter to niestandardowa klasa (wlasny adapter) bedaca rozszerzeniem klasy Adapter
      //S¸uzy ona do udostepniania ofert widocznych na liscie - ListView
      //Adaptery sluza do udostepniania danych
      
      // ListView adapter (this class extends ListActivity)
      setListAdapter(dealsAdapter);   //metoda wiaze obiekty klasy Adapter z ListView
  
      
    //POBIERANIE DANYCH
      //Sprawdzanie czy fragment listy z aktualnymi ofertami jest juz zape¸niony     
      // get Sections list from application (parsing feed if necessary)
      if (app.getSectionList().isEmpty()) {  //jesli nie to
         
         if (app.connectionPresent()) {  //sprawdzenie czy aplikacja ma dostep do sieci
           
            new ParseFeedTask().execute();    //wywolanie implementacji metody AsyncTask  //TU POBIERANE DANE Z SIECI I PRZETWARZANY KANAL RSS z ofertami eBAy
            //AsyncTask umozliwia wykonywanie d¸ugich aplikacji w watku odrebnym od watku interfejsu uzytkownika
            //Tu aplikacja pobiera dane z sieci i przetwarza kanal RSS z ofertami eBay
         } else {
            
            //jezeli nie mozna uruchomic klasy AsynTask z uwagi na brak polaczenia z siecia nalwzy wyswietlc toast
            Toast.makeText(this, getString(R.string.deal_list_network_unavailable), Toast.LENGTH_LONG).show();
         }
         
      } else {         
         resetListItems(app.getSectionList().get(0).getItems());
      }      

   
    //LISTA SPINNER, kontrolka udostepnia liste opcji    
      // Spinner for choosing a Section
      //w kontrolce spinner zrod¸em danych jest standardowa klasa ArrayAdapter pobieajaca dane z obiektu klasy Application 
      sectionSpinner = (Spinner) findViewById(R.id.section_spinner);
      spinnerAdapter = new ArrayAdapter<Section>(DealList.this, android.R.layout.simple_spinner_item, app.getSectionList());
      spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      sectionSpinner.setAdapter(spinnerAdapter);
     
      //dolacznie odbiornika - umozliwia to odebranie zdarzenia zaznaczenia elementu w kontrolce Spinner
      //w Frameworku Androida istnieje wiele odbiornik—w zdarzeÄ
      //Odbiorniki pelnia funkcje interfejs—w
      sectionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      
         @Override
         public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            if (currentSelectedSection != position) {
               currentSelectedSection = position;
               resetListItems(app.getSectionList().get(position).getItems());
            }
         }

         @Override
         public void onNothingSelected(AdapterView<?> parentView) {
            // do nothing
         }
      });      
   }
   
  //PONOWNE WYSWIETLENIE LISTY
   //ponowne ustawienie adaptera widoku ListView
   private void resetListItems(List<Item> newItems) {
      items.clear();
      items.addAll(newItems);
      dealsAdapter.notifyDataSetChanged();
   }

   
  //OBSLOGA KLIKNIECIA OKRESLONEGO ELEMENTU Z LISTY
   //metoda znajduje sie w klasie ListActivity i jest wywo¸ywana zwrotniew celu obs¸ugi zdarzeia 
   //metoda jest przes¸onieta aby wykonac pozadane operacje 
   @Override
   protected void onListItemClick(ListView listView, View view, int position, long id) {
     // view.setBackgroundColor(android.R.color.background_light);
      app.setCurrentItem(app.getSectionList().get(currentSelectedSection).getItems().get(position));
      Intent dealDetails = new Intent(DealList.this, DealDetails.class); //za pomoca obiektu klasy Intent metoda uruchamia aktywnosc DealDetails 
      startActivity(dealDetails);
   }

   
  //ZAMKNIE OKNA DIALOGOWEGO POSTEPU aby podczas zatrzymania aktywnosci nie bylo wycieku zasob—w, tzn. nie bylo widoczne
   @Override
   public void onPause() {
      if (progressDialog.isShowing()) {
         progressDialog.dismiss();
      }
      super.onPause();
   }
   
 //MENU  
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      menu.add(0, DealList.MENU_REPARSE, 0, R.string.deal_list_reparse_menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case MENU_REPARSE:
            if (app.connectionPresent()) {
               new ParseFeedTask().execute();               
            } else {
               Toast.makeText(this, getString(R.string.deal_list_network_unavailable), Toast.LENGTH_LONG).show();
            }
            return true;
      }
      return super.onOptionsItemSelected(item);
   }   
   

   //ADAPTER
   //Niestandardowa klasa Adapter dla listy
   // Use a custom Adapter to control the layout and views
   private class DealsAdapter extends ArrayAdapter<Item> {      
   //ArrayAdapter jest klasa bazowa  to wazne poniewaz Adapter to interfejs o duzej ilosci metod
   // a tu nalezy przeslonic tylko wyswietlanie obiektu klasy View, pozostale mechanizmy nie wymagaja zmian
      public DealsAdapter(List<Item> items) {
         super(DealList.this,
                  R.layout.list_item, items);
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
         
         if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
         }

         // use ViewHolder here to prevent multiple calls to findViewById (if you have a large collection)
         TextView text = (TextView) convertView.findViewById(R.id.deal_title);
         ImageView image = (ImageView) convertView.findViewById(R.id.deal_img);
         image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ddicon));

         Item item = getItem(position);

         if (item != null) {
            text.setText(item.getTitle());
            Bitmap bitmap = app.getImageCache().get(item.getItemId());
            if (bitmap != null) {
               image.setImageBitmap(bitmap);
            } else {
               // put item ID on image as TAG for use in task
               image.setTag(item.getItemId());
               // separate thread/via task, for retrieving each image
               // (note that this is brittle as is, should stop all threads in onPause)               
               new RetrieveImageTask(image).execute(item.getSmallPicUrl());
            }
         }

         return convertView;
      }
   }
   
   //WATEK
   // Use an AsyncTask<Params, Progress, Result> to easily perform tasks off of the UI Thread
   private class ParseFeedTask extends AsyncTask<Void, Void, List<Section>> {

      @Override
      protected void onPreExecute() {
         progressDialog.show();
      }

      @Override
      protected List<Section> doInBackground(Void... args) {
         List<Section> sections = app.getParser().parse();
         return sections;
      }

      @Override
      protected void onPostExecute(List<Section> taskSectionList) {
         if (progressDialog.isShowing()) {
            progressDialog.dismiss();
         }
         if (!taskSectionList.isEmpty()) {
            app.getSectionList().clear();
            app.getSectionList().addAll(taskSectionList);            
            resetListItems(taskSectionList.get(0).getItems());            
            
            // for the "reparse" button, we also need to reset the spinner data and put selection on 0 (Daily Deals)
            // (and since we don't have a member variable for the spinnerAdapter's data, we just reset one by one)
            sectionSpinner.setSelection(0);
            spinnerAdapter.clear();
            for (Section s : taskSectionList) {
               spinnerAdapter.add(s);
            }            
         } else {
            Toast.makeText(DealList.this, getString(R.string.deal_list_missing_data), Toast.LENGTH_LONG).show();
         }
      }
   }

   //WATEK
   private class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {
      private ImageView imageView;

      public RetrieveImageTask(ImageView imageView) {
         this.imageView = imageView;
      }

      @Override
      protected Bitmap doInBackground(String... args) {
         Bitmap bitmap = app.retrieveBitmap(args[0]);
         return bitmap;
      }

      @Override
      protected void onPostExecute(Bitmap bitmap) {
         if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            app.getImageCache().put((Long) imageView.getTag(), bitmap);
            imageView.setTag(null);
         }
      }
   }
}