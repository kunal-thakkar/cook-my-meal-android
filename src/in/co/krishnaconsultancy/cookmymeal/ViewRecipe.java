package in.co.krishnaconsultancy.cookmymeal;

import in.co.krishnaconsultancy.R;
import in.co.krishnaconsultancy.cookmymeal.db.RecipeDbHelper;
import in.co.krishnaconsultancy.cookmymeal.db.dao.Recipe;
import in.co.krishnaconsultancy.cookmymeal.utility.DbBitmapUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewRecipe extends ActionBarActivity {

	private static Recipe recipe;
	private static RecipeDbHelper recipeDbHelper;
	private static ViewRecipe viewRecipe;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_recipe);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		recipeDbHelper = new RecipeDbHelper(this);
		viewRecipe = this;
		Intent i = getIntent();
		recipe = recipeDbHelper.getRecipe(i.getIntExtra("recipeId", 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_recipe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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

		private TextView method;
		private TextView accompaniments;
		private TableRow nutrientsHeader;
		private TableRow nutrientsValues;
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_view_recipe, container, false);
			if(recipe.getThumbnail().length != 0)
				((ImageView)rootView.findViewById(R.id.recipeImage)).setImageBitmap(DbBitmapUtility.getImage(recipe.getThumbnail()));
			((TextView)rootView.findViewById(R.id.recipeName)).setText(recipe.getName());
			((TextView)rootView.findViewById(R.id.recipeDescription)).setText(recipe.getDescription());
			((TextView)rootView.findViewById(R.id.recipeIngredents)).setText(recipe.getIngredientString());
			method = (TextView)rootView.findViewById(R.id.recipeMethod);
			accompaniments = (TextView)rootView.findViewById(R.id.recipeAccompaniments);
			nutrientsHeader = (TableRow)rootView.findViewById(R.id.tableRow1);
			nutrientsValues = (TableRow)rootView.findViewById(R.id.tableRow2);
			if(recipe.getMethodString().equals("")){
				new AsyncTask<Recipe, Integer, Recipe>() {
					@Override
					protected Recipe doInBackground(Recipe... params) {
						Recipe recipe = params[0];
				        String url = "http://cookmymeal.krishnaconsultancy.co.in/api.php";

				        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
				        nameValuePair.add(new BasicNameValuePair("request", "recipe"));
				        nameValuePair.add(new BasicNameValuePair("id", String.valueOf(recipe.getId())));
				        
				        HttpPost httpPost = new HttpPost(url);
				        DefaultHttpClient httpClient = new DefaultHttpClient();
				        try {
					        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
				            HttpResponse response = httpClient.execute(httpPost);
				            String responseString = EntityUtils.toString(response.getEntity());
				            try {
								JSONArray array = new JSONArray(responseString);
								recipe = RecipeDbHelper.fromJSONObject(recipe, array.getJSONObject(0));
								recipeDbHelper.update(recipe);
							} catch (JSONException e) {
								e.printStackTrace();
							}
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
						return recipe;
					}
					@Override
					protected void onPostExecute(Recipe recipe) {
						populateRecipe(recipe);
					}
				}.execute(recipe);
			}else{
				populateRecipe(recipe);
			}
			return rootView;
		}
		
		private void populateRecipe(Recipe recipe){
			method.setText(recipe.getMethodString());
			accompaniments.setText(recipe.getAccompanimentsString());
	        JSONObject obj = recipe.getNutrientsTable();
			Iterator<?> keys = obj.keys();
			String key;
			int i = 0;
			while(keys.hasNext()){
				try {
					TextView header = new TextView(viewRecipe);
					TextView value = new TextView(viewRecipe);
					header.setTextAppearance(getActivity(), R.style.nutrientsHeader);
					value.setTextAppearance(getActivity(), R.style.nutrientsValue);
					header.setText(key = String.valueOf(keys.next()));
					value.setText(String.valueOf(obj.get(key)));
					nutrientsHeader.addView(header, i);
					nutrientsValues.addView(value, i);
					i++;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
