
/* W projekcie DealDroid na potrzeby testowania nalezy zmienić widocznosc klasy
 * W projekcie DealDroidExport jest */

package com.manning.aip.dealdroid.test;

import java.util.List;

import com.manning.aip.dealdroid.DealList;
import com.manning.aip.dealdroid.DealDetails;
import com.manning.aip.dealdroid.DealList.ParseFeedTask;
import com.manning.aip.dealdroid.model.Section;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;

public class DealListTest extends ActivityInstrumentationTestCase2<DealList> {

	   public DealListTest() {
	      super("com.manning.aip.dealdroid", DealList.class);
	   }

	   public void testDealListToDetailsUserFlow() throws Exception {
	      Instrumentation instr = getInstrumentation();
	      DealList dealList = getActivity();

	      ParseFeedTask task = dealList.getParseFeedTask();
	      assertNotNull("task should not be null", task);

	      List<Section> taskResult = task.waitAndUpdate();
	      boolean noSections = taskResult == null || taskResult.isEmpty();
	      assertFalse("task did not return any sections", noSections);

	      instr.waitForIdleSync();

	      String dealDetails = DealDetails.class.getCanonicalName();
	      ActivityMonitor monitor = instr.addMonitor(dealDetails, null, false);

	      View firstItem = dealList.getListView().getChildAt(0);
	      TouchUtils.clickView(this, firstItem);

	      assertTrue(instr.checkMonitorHit(monitor, 1));

	      instr.removeMonitor(monitor);
	   }
	}
