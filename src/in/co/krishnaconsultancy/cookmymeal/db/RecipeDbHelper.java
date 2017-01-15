package in.co.krishnaconsultancy.cookmymeal.db;

import in.co.krishnaconsultancy.cookmymeal.RecipeFilter;
import in.co.krishnaconsultancy.cookmymeal.db.dao.Recipe;
import in.co.krishnaconsultancy.cookmymeal.utility.DbBitmapUtility;

import java.io.IOException;
import java.util.ArrayList;
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

public class RecipeDbHelper  extends SQLiteOpenHelper {
	
	public static final String RECIPE_TABLE_NAME = "cook_my_meal";
	public static final String RECIPE_COL_ID = "id";
	public static final String RECIPE_COL_NAME = "name";
	public static final String RECIPE_COL_DESCRIPTION = "description";
	public static final String RECIPE_COL_RECIPEURL = "recipeUrl";
	public static final String RECIPE_COL_THUMBNAILURL = "thumbnailUrl";
	public static final String RECIPE_COL_THUMBNAIL = "thumbnail";
	public static final String RECIPE_COL_IMGURL = "imgUrl";
	public static final String RECIPE_COL_IMG = "img";
	public static final String RECIPE_COL_PREPTIME = "prepTime";
	public static final String RECIPE_COL_COOKTIME = "cookTime";
	public static final String RECIPE_COL_YIELD = "yield";
	public static final String RECIPE_COL_CATEGORY = "category";
	public static final String RECIPE_COL_INGREDIENT = "ingredient";
	public static final String RECIPE_COL_METHOD = "method";
	public static final String RECIPE_COL_NUTRIENTS = "nutrients";
	public static final String RECIPE_COL_ACCOMPANIMENTS = "accompaniments";

	public static final int RECIPE_COL_INDEX_ID = 0;
	public static final int RECIPE_COL_INDEX_NAME = 1;
	public static final int RECIPE_COL_INDEX_DESCRIPTION = 2;
	public static final int RECIPE_COL_INDEX_RECIPEURL = 3;
	public static final int RECIPE_COL_INDEX_THUMBNAILURL = 4;
	public static final int RECIPE_COL_INDEX_THUMBNAIL = 5;
	public static final int RECIPE_COL_INDEX_IMGURL = 6;
	public static final int RECIPE_COL_INDEX_IMG = 7;
	public static final int RECIPE_COL_INDEX_PREPTIME = 8;
	public static final int RECIPE_COL_INDEX_COOKTIME = 9;
	public static final int RECIPE_COL_INDEX_YIELD = 10;
	public static final int RECIPE_COL_INDEX_CATEGORY = 11;
	public static final int RECIPE_COL_INDEX_INGREDIENT = 12;
	public static final int RECIPE_COL_INDEX_METHOD = 13;
	public static final int RECIPE_COL_INDEX_NUTRIENTS = 14;
	public static final int RECIPE_COL_INDEX_ACCOMPANIMENTS = 15;
	private static final String DATABASE_NAME = "cook_my_meal.db";
	private static final int DATABASE_VERSION = 1;
		
	private String DATABASE_CREATE = "create table IF NOT EXISTS " + RECIPE_TABLE_NAME + "(" +
        RECIPE_COL_ID + " integer  primary key autoincrement, " +
        RECIPE_COL_NAME + " text not null, " +
        RECIPE_COL_DESCRIPTION + " text not null, " +
        RECIPE_COL_RECIPEURL + " text not null, " +
        RECIPE_COL_THUMBNAILURL + " text not null, " +
        RECIPE_COL_THUMBNAIL + " blob not null, " +
        RECIPE_COL_IMGURL + " text not null, " +
        RECIPE_COL_IMG + " blob not null, " +
        RECIPE_COL_PREPTIME + " text not null, " +
        RECIPE_COL_COOKTIME + " text not null, " +
        RECIPE_COL_YIELD + " text not null, " +
        RECIPE_COL_CATEGORY + " text not null, " +
        RECIPE_COL_INGREDIENT + " text not null, " +
        RECIPE_COL_METHOD + " text not null, " +
        RECIPE_COL_NUTRIENTS + " text not null, " +
        RECIPE_COL_ACCOMPANIMENTS + " text not null" + 
    ");";

	public RecipeDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public Recipe getRecipe(int id){
		Cursor res =  getReadableDatabase().rawQuery("select * from "+RECIPE_TABLE_NAME+" WHERE id = "+id, null );
		res.moveToFirst();
		return fromCursor(res);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE );		
	}
	
	public void updateImg(int id, Bitmap img, boolean isThumbnail){
		if(img == null){
			return;
		}
	    ContentValues cv = new ContentValues();
	    cv.put(isThumbnail?RECIPE_COL_THUMBNAIL:RECIPE_COL_IMG, DbBitmapUtility.getBytes(img));
	    getWritableDatabase().update(RECIPE_TABLE_NAME, cv, "id=?", new String[]{String.valueOf(id)});
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(RecipeDbHelper.class.getName(),
            "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE_NAME);
        onCreate(db);		
	}
	
	public List<Recipe> getRecipes(int startFrom) {
		String selection;
		String[] criteria;
		/*if(RecipeFilter.getIngrendentsCriteria().equals("")){
			selection = "id > ?";
			criteria = new String[]{ String.valueOf(startFrom) };
		}else{*/
			selection = "id > ? AND "+RECIPE_COL_INGREDIENT+" LIKE ?";
			criteria = new String[]{ String.valueOf(startFrom), "%methi%" };
		//}
		Cursor res =  getReadableDatabase().query(RECIPE_TABLE_NAME, new String[]{"*"}, 
				selection, criteria, null, null, "name", "20");
		List<Recipe> result = new ArrayList<Recipe>();
		if(res.getCount() == 0){
	        /*String url = "http://cookmymeal.krishnaconsultancy.co.in/api.php";
	        
	        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
	        nameValuePair.add(new BasicNameValuePair("request", "fetchList"));
	        nameValuePair.add(new BasicNameValuePair("from", String.valueOf(startFrom)));
	        HttpPost httpPost = new HttpPost(url);
	        
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        try {
		        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	            HttpResponse response = httpClient.execute(httpPost);
	            String responseString = EntityUtils.toString(response.getEntity());
	            System.out.println("responce :- "+responseString);
	            try {
					JSONArray array = new JSONArray(responseString);
					JSONObject obj;
					Recipe recipe;
					for(int i = 0; i < 20; i++){
						obj = array.getJSONObject(i);
						recipe = fromJSONObject(obj);
						getWritableDatabase().insert(RECIPE_TABLE_NAME, null, recipe.getContentValues());
						result.add(recipe);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        } catch (IOException e) {
	            e.printStackTrace();
	        }*/
		} else {
			res.moveToFirst();
			while(res.isAfterLast() == false){
				result.add(fromCursor(res));
				res.moveToNext();
			}
		}
		return result;
	}
	
	public void update(Recipe recipe){
		getWritableDatabase().update(RECIPE_TABLE_NAME, recipe.getContentValues(), "id=?", 
				new String[]{String.valueOf(recipe.getId())});
	}
	
    // Create a Recipe object from a cursor
    public static Recipe fromCursor(Cursor curRecipe) {
    	Recipe recipe = new Recipe();
    	recipe.setAccompaniments(curRecipe.getString(RECIPE_COL_INDEX_ACCOMPANIMENTS));
    	recipe.setCategory(curRecipe.getString(RECIPE_COL_INDEX_CATEGORY));
    	recipe.setCooktime(curRecipe.getString(RECIPE_COL_INDEX_COOKTIME));
    	recipe.setDescription(curRecipe.getString(RECIPE_COL_INDEX_DESCRIPTION));
    	recipe.setId(curRecipe.getInt(RECIPE_COL_INDEX_ID));
    	recipe.setImgUrl(curRecipe.getString(RECIPE_COL_INDEX_IMGURL));
    	recipe.setImg(curRecipe.getBlob(RECIPE_COL_INDEX_IMG));
    	recipe.setIngredient(curRecipe.getString(RECIPE_COL_INDEX_INGREDIENT));
    	recipe.setMethod(curRecipe.getString(RECIPE_COL_INDEX_METHOD));
    	recipe.setName(curRecipe.getString(RECIPE_COL_INDEX_NAME));
    	recipe.setNutrients(curRecipe.getString(RECIPE_COL_INDEX_NUTRIENTS));
    	recipe.setPrepTime(curRecipe.getString(RECIPE_COL_INDEX_PREPTIME));
    	recipe.setRecipeUrl(curRecipe.getString(RECIPE_COL_INDEX_RECIPEURL));
    	recipe.setThumbnailUrl(curRecipe.getString(RECIPE_COL_INDEX_THUMBNAILURL));
    	recipe.setThumbnail(curRecipe.getBlob(RECIPE_COL_INDEX_THUMBNAIL));
    	recipe.setYield(curRecipe.getString(RECIPE_COL_INDEX_YIELD));
        return recipe;
    }

    public static Recipe fromJSONObject(JSONObject curRecipe) throws JSONException {
    	Recipe recipe = new Recipe();
    	recipe.setCategory(curRecipe.getString(RECIPE_COL_CATEGORY));
    	recipe.setCooktime(curRecipe.getString(RECIPE_COL_COOKTIME));
    	recipe.setDescription(curRecipe.getString(RECIPE_COL_DESCRIPTION));
    	recipe.setId(curRecipe.getInt(RECIPE_COL_ID));
    	recipe.setImgUrl(curRecipe.getString(RECIPE_COL_IMGURL));
    	recipe.setIngredient(curRecipe.getString(RECIPE_COL_INGREDIENT));
    	recipe.setName(curRecipe.getString(RECIPE_COL_NAME));
    	recipe.setPrepTime(curRecipe.getString(RECIPE_COL_PREPTIME));
    	recipe.setRecipeUrl(curRecipe.getString(RECIPE_COL_RECIPEURL));
    	recipe.setThumbnailUrl(curRecipe.getString(RECIPE_COL_THUMBNAILURL));
    	recipe.setYield(curRecipe.getString(RECIPE_COL_YIELD));
        return recipe;
    }

    public static Recipe fromJSONObject(Recipe recipe, JSONObject curRecipe) throws JSONException {
    	recipe.setMethod(curRecipe.getString(RECIPE_COL_METHOD));
    	recipe.setNutrients(curRecipe.getString(RECIPE_COL_NUTRIENTS));
    	recipe.setAccompaniments(curRecipe.getString(RECIPE_COL_ACCOMPANIMENTS));
        return recipe;
    }
}
