package in.co.krishnaconsultancy.cookmymeal.adapter;

import in.co.krishnaconsultancy.R;
import in.co.krishnaconsultancy.cookmymeal.MainActivity;
import in.co.krishnaconsultancy.cookmymeal.db.dao.Recipe;
import in.co.krishnaconsultancy.cookmymeal.utility.DbBitmapUtility;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context context;
    private List<Recipe> values;

    public RecipeAdapter(Context context, int textViewResourceId, List<Recipe> objects) {
      super(context, textViewResourceId, objects);
      this.context = context;
      this.values = objects;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_items, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.firstLine);
			holder.description = (TextView) convertView.findViewById(R.id.secondLine);
			holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
			holder.cookTime = (TextView) convertView.findViewById(R.id.recipeCookTime);
			holder.source = (TextView) convertView.findViewById(R.id.recipeSource);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		Recipe r = values.get(position);
		holder.position = position;
		holder.name.setText(r.getName());
		holder.description.setText(r.getDescription());
		holder.cookTime.setText(r.getCooktime());
		holder.source.setText("Tarla Dalal");
		if(r.getThumbnail().length == 0){
			holder.thumbnailUrl = r.getThumbnailUrl();
			new ThumbnailTask(position, holder, r.getId()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		}else{
            holder.thumbnail.setImageBitmap(DbBitmapUtility.getImage(r.getThumbnail()));
		}
		return convertView;
    }

    private static class ThumbnailTask extends AsyncTask<URL, Integer, Bitmap> {
        private int mPosition;
        private ViewHolder mHolder;
        private int mId;

        public ThumbnailTask(int position, ViewHolder holder, int id) {
            mPosition = position;
            mHolder = holder;
            mId = id;
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
        	System.out.println("Requestion image "+mHolder.thumbnailUrl);
            Bitmap bmp = null;
            try{
                URL ulrn = new URL(mHolder.thumbnailUrl);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
            }catch(Exception e){}
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mHolder.position == mPosition) {
            	MainActivity.getRecipeDbHelper().updateImg(mId, bitmap, true);
                mHolder.thumbnail.setImageBitmap(bitmap);
            }
        }
    }
    
    private static class ViewHolder {
    	public int position;
        public TextView name;
        public TextView description;
        public TextView cookTime;
        public TextView source;
        public ImageView thumbnail;
        public String thumbnailUrl;
    }
    
  }
