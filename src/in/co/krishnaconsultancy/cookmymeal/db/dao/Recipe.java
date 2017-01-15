package in.co.krishnaconsultancy.cookmymeal.db.dao;

import in.co.krishnaconsultancy.cookmymeal.db.RecipeDbHelper;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class Recipe implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private String recipeUrl;
	private String thumbnailUrl;
	private byte[] thumbnail;
	private String imgUrl;
	private byte[] img;
	private String prepTime;
	private String cooktime;
	private String yield;
	private String category;
	private String ingredient;
	private String method = "";
	private String nutrients = "";
	private String accompaniments = "";
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecipeUrl() {
		return recipeUrl;
	}

	public void setRecipeUrl(String recipeUrl) {
		this.recipeUrl = recipeUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(String prepTime) {
		this.prepTime = prepTime;
	}

	public String getCooktime() {
		return cooktime;
	}

	public void setCooktime(String cooktime) {
		this.cooktime = cooktime;
	}

	public String getYield() {
		return yield;
	}

	public void setYield(String yield) {
		this.yield = yield;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getNutrients() {
		return nutrients;
	}

	public void setNutrients(String nutrients) {
		this.nutrients = nutrients;
	}

	public String getAccompaniments() {
		return accompaniments;
	}

	public void setAccompaniments(String accompaniments) {
		this.accompaniments = accompaniments;
	}

	public byte[] getThumbnail() {
		return thumbnail==null?new byte[]{}: thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public byte[] getImg() {
		return img==null? new byte[]{}: img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	/**
     * Convenient method to get the objects data members in ContentValues object.
     * This will be useful for Content Provider operations,
     * which use ContentValues object to represent the data.
     *
     * @return
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(RecipeDbHelper.RECIPE_COL_ACCOMPANIMENTS, getAccompaniments());
        values.put(RecipeDbHelper.RECIPE_COL_CATEGORY, getCategory());
        values.put(RecipeDbHelper.RECIPE_COL_COOKTIME, getCooktime());
        values.put(RecipeDbHelper.RECIPE_COL_DESCRIPTION, getDescription());
        values.put(RecipeDbHelper.RECIPE_COL_IMGURL, getImgUrl());
        values.put(RecipeDbHelper.RECIPE_COL_IMG, getImg());
        values.put(RecipeDbHelper.RECIPE_COL_INGREDIENT, getIngredient());
        values.put(RecipeDbHelper.RECIPE_COL_METHOD, getMethod());
        values.put(RecipeDbHelper.RECIPE_COL_NAME, getName());
        values.put(RecipeDbHelper.RECIPE_COL_NUTRIENTS, getNutrients());
        values.put(RecipeDbHelper.RECIPE_COL_PREPTIME, getPrepTime());
        values.put(RecipeDbHelper.RECIPE_COL_RECIPEURL, getRecipeUrl());
        values.put(RecipeDbHelper.RECIPE_COL_THUMBNAILURL, getThumbnailUrl());
        values.put(RecipeDbHelper.RECIPE_COL_THUMBNAIL, getThumbnail());
        values.put(RecipeDbHelper.RECIPE_COL_YIELD, getYield());
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe tvShow = (Recipe) o;
        if (id != tvShow.getId()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + id;
        return result;
    }

	public String getIngredientString() {
		if(ingredient.equals("[]")){
			return "";
		}
		StringBuilder builder = new StringBuilder();
		try {
			JSONArray array = new JSONArray(ingredient);
			JSONObject obj;
			int len = array.length();
			for(int i = 0; i < len; i++){
				obj = array.getJSONObject(i);
				builder.append(String.format("%d) %s %s\n", i+1, obj.getString("amount"), obj.getString("name")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public CharSequence getMethodString() {
		if(method.length() < 3){
			return "";
		}
		StringBuilder builder = new StringBuilder();
		try {
			JSONArray array = new JSONArray(method);
			int len = array.length();
			for(int i = 0; i < len; i++){
				builder.append(String.format("%d) %s\n", i+1, array.getString(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public JSONObject getNutrientsTable() {
		JSONObject result = new JSONObject();
		if(nutrients.length() < 3) return result;
		try {
			result = new JSONObject(nutrients);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public CharSequence getAccompanimentsString() {
		if(accompaniments.length() < 3){
			return "";
		}
		StringBuilder builder = new StringBuilder();
		try {
			JSONArray array = new JSONArray(accompaniments);
			int len = array.length();
			for(int i = 0; i < len; i++){
				builder.append(String.format("%d) %s\n", i+1, array.getString(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}
