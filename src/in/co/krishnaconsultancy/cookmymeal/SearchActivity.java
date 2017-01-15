package in.co.krishnaconsultancy.cookmymeal;

import in.co.krishnaconsultancy.R;
import in.co.krishnaconsultancy.cookmymeal.utility.CustomTokenizer;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

public class SearchActivity extends Activity {

	private static ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		adapter.addAll("Sample 1","Sample 2","Sample 3","Sample 4","Sample 5","Sample 6","Sample 7","Sample 8","Sample 9","Sample 10");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_search, container, false);
			final MultiAutoCompleteTextView autoCompleteTextView = (MultiAutoCompleteTextView)rootView.findViewById(R.id.ingredient_criteria);
			autoCompleteTextView.setTokenizer(new CustomTokenizer(' '));
			autoCompleteTextView.setAdapter(adapter);
			
			Button btn = (Button)rootView.findViewById(R.id.btnSearchRecipe);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(getActivity(), MainActivity.class);
					i.putExtra("ingredents", autoCompleteTextView.getText().toString());
					startActivity(i);
				}
			});
			autoCompleteTextView.getText().toString();
			return rootView;
		}
	}
}
