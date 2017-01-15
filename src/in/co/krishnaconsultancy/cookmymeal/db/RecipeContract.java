package in.co.krishnaconsultancy.cookmymeal.db;

import android.net.Uri;

public class RecipeContract {
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.udinic.tvshow";
    public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.udinic.tvshow";

    public static final String AUTHORITY = "co.in.krishnaconsultancy.cookmymeal.recipe.provider";
    // content://<authority>/<path to type>
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/tvshows");

}
