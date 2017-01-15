package in.co.krishnaconsultancy.cookmymeal;

import in.co.krishnaconsultancy.R;
import in.co.krishnaconsultancy.cookmymeal.adapter.RecipeAdapter;
import in.co.krishnaconsultancy.cookmymeal.db.RecipeDbHelper;
import in.co.krishnaconsultancy.cookmymeal.db.dao.Recipe;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	private static RecipeDbHelper recipeDbHelper;
	private boolean loading = false;
    private RecipeAdapter recipeAdapter;
	private ListView listview;
    
	public static RecipeDbHelper getRecipeDbHelper() {
		return recipeDbHelper;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
    		startActivity(new Intent(getApplicationContext(), SearchActivity.class));
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recipeDbHelper = new RecipeDbHelper(this);
		recipeAdapter = new RecipeAdapter(this, R.layout.list_items, new ArrayList<Recipe>());
		listview = (ListView) findViewById(R.id.listview);
	    listview.setAdapter(recipeAdapter);
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
	    		if(position == -1)return;
	    		Intent i = new Intent(getApplicationContext(), ViewRecipe.class);
	    		i.putExtra("recipeId", recipeAdapter.getItem(position).getId());
	    		startActivity(i);
	    	}
	    });
	    listview.setOnScrollListener(new OnScrollListener() {
	    	private int visibleThreshold = 5;
		    private int previousTotal = 0;

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				
			}
			
			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
				if (loading) {
		            if (totalItemCount > previousTotal) {
		                loading = false;
		                previousTotal = totalItemCount;
		            }
		        }
		        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
		        	loadRecipesInListView(totalItemCount);
		            loading = true;
		        }	
			}
		});
	    loadRecipesInListView(0);
	}
	
	private void loadRecipesInListView(final int startFrom){
    	new AsyncTask<Object, Object, List<Recipe>>() {
			@Override
			protected List<Recipe> doInBackground(Object... params) {
				return recipeDbHelper.getRecipes(startFrom);
			}
			
			@Override
			protected void onPostExecute(List<Recipe> recipes) {
				recipeAdapter.addAll(recipes);
				recipeAdapter.notifyDataSetChanged();
				loading = false;
			}
		}.execute(new Object());

	}

} 