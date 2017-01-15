package in.co.krishnaconsultancy.cookmymeal.syncadapter;

import in.co.krishnaconsultancy.authentication.AccountGeneral;
import in.co.krishnaconsultancy.cookmymeal.db.RecipeContract;
import in.co.krishnaconsultancy.cookmymeal.db.RecipeDbHelper;
import in.co.krishnaconsultancy.cookmymeal.db.dao.Recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class RecipeSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "RecipeSyncAdapter";

    private final AccountManager mAccountManager;

    public RecipeSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {

        // Building a print of the extras we got
        StringBuilder sb = new StringBuilder();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key + "[" + extras.get(key) + "] ");
            }
        }

        Log.d("udinic", TAG + "> onPerformSync for account[" + account.name + "]. Extras: "+sb.toString());

        try {
            // Get the auth token for the current account and
            // the userObjectId, needed for creating items on Parse.com account
            String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
            String userObjectId = mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_OBJ_ID);

            ParseComServerAccessor parseComService = new ParseComServerAccessor();

            Log.d("udinic", TAG + "> Get remote TV Shows");
            // Get shows from remote
            List<Recipe> remoteTvShows = parseComService.getRecipe(authToken);

            Log.d("udinic", TAG + "> Get local TV Shows");
            // Get shows from local
            ArrayList<Recipe> localRecipe = new ArrayList<Recipe>();
            Cursor curRecipe = provider.query(RecipeContract.CONTENT_URI, null, null, null, null);
            if (curRecipe != null) {
                while (curRecipe.moveToNext()) {
                    localRecipe.add(RecipeDbHelper.fromCursor(curRecipe));
                }
                curRecipe.close();
            }

            // See what Local shows are missing on Remote
            ArrayList<Recipe> recipeToRemote = new ArrayList<Recipe>();
            for (Recipe recipe : localRecipe) {
                if (!remoteTvShows.contains(recipe)) recipeToRemote.add(recipe);
            }

            // See what Remote shows are missing on Local
            ArrayList<Recipe> recipeToLocal = new ArrayList<Recipe>();
            for (Recipe remoteRecipe : remoteTvShows) {
                if (!localRecipe.contains(remoteRecipe))
                    recipeToLocal.add(remoteRecipe);
            }

            if (recipeToRemote.size() == 0) {
                Log.d("udinic", TAG + "> No local changes to update server");
            } else {
                Log.d("udinic", TAG + "> Updating remote server with local changes");
                // Updating remote recipe
                for (Recipe remoteRecipe : recipeToRemote) {
                    Log.d("udinic", TAG + "> Local -> Remote [" + remoteRecipe.getName() + "]");
                    parseComService.putRecipe(authToken, userObjectId, remoteRecipe);
                }
            }

            if (recipeToLocal.size() == 0) {
                Log.d("udinic", TAG + "> No server changes to update local database");
            } else {
                Log.d("udinic", TAG + "> Updating local database with remote changes");

                // Updating local tv shows
                int i = 0;
                ContentValues showsToLocalValues[] = new ContentValues[recipeToLocal.size()];
                for (Recipe recipe : recipeToLocal) {
                    Log.d("udinic", TAG + "> Remote -> Local [" + recipe.getName() + "]");
                    showsToLocalValues[i++] = recipe.getContentValues();
                }
                provider.bulkInsert(RecipeContract.CONTENT_URI, showsToLocalValues);
            }
            Log.d("udinic", TAG + "> Finished.");
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            syncResult.stats.numIoExceptions++;
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            syncResult.stats.numAuthExceptions++;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

