package in.co.krishnaconsultancy.cookmymeal;

import in.co.krishnaconsultancy.R;
import in.co.krishnaconsultancy.authentication.AccountGeneral;
import in.co.krishnaconsultancy.cookmymeal.db.RecipeContract;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainMenuActivity extends ActionBarActivity {

    private static AccountManager mAccountManager;
    private static String authToken = null;
    private static Account mConnectedAccount;
    private Object handleSyncObserver;

    @Override
    protected void onResume() {
        super.onResume();
        handleSyncObserver = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE |
            ContentResolver.SYNC_OBSERVER_TYPE_PENDING, new SyncStatusObserver() {
                @Override
                public void onStatusChanged(final int which) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //refreshSyncStatus();
                        }
                    });
                }
            }
        );
    }

    @Override
    protected void onPause() {
        if (handleSyncObserver != null)
            ContentResolver.removeStatusChangeListener(handleSyncObserver);
        super.onStop();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
        mAccountManager = AccountManager.get(this);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_connect:
            getTokenForAccountCreateIfNeeded(
            		AccountGeneral.ACCOUNT_TYPE, 
            		AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            return true;
		case R.id.action_sync_now:
            if (mConnectedAccount == null) {
                Toast.makeText(MainMenuActivity.this, "Please connect first", Toast.LENGTH_SHORT).show();
                return true;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
            ContentResolver.requestSync(mConnectedAccount, RecipeContract.AUTHORITY, bundle);
            return true;
		case R.id.action_auto_sync:
            if (mConnectedAccount == null) {
                Toast.makeText(MainMenuActivity.this, "Please connect first", Toast.LENGTH_SHORT).show();
                return true;
            }
            // Setting the autosync state of the sync adapter
            String authority = RecipeContract.AUTHORITY;
            ContentResolver.setSyncAutomatically(mConnectedAccount,authority, item.isChecked());
            ContentResolver.setIsSyncable(mConnectedAccount, authority, item.isChecked() ? 1 : 0);
            return true;
		default:
			break;
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
			View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
	        rootView.findViewById(R.id.btnAddShow).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {

	            }
	        });

	        rootView.findViewById(R.id.btnShowLocalList).setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {

	            }
	        });
	        return rootView;
		}

	}

    /*private void refreshSyncStatus() {
        String status;
        if (ContentResolver.isSyncActive(mConnectedAccount, RecipeContract.AUTHORITY))
            status = "Status: Syncing..";
        else if (ContentResolver.isSyncPending(mConnectedAccount, RecipeContract.AUTHORITY))
            status = "Status: Pending..";
        else
            status = "Status: Idle";
        //((TextView) findViewById(R.id.status)).setText(status);
        Log.d("udinic", "refreshSyncStatus> " + status);
    }

    private void initButtonsAfterConnect() {
        String authority = RecipeContract.AUTHORITY;

        // Get the syncadapter settings and init the checkboxes accordingly
        int isSyncable = ContentResolver.getIsSyncable(mConnectedAccount, authority);
        boolean autSync = ContentResolver.getSyncAutomatically(mConnectedAccount, authority);

        ((CheckBox)findViewById(R.id.cbIsSyncable)).setChecked(isSyncable > 0);
        ((CheckBox)findViewById(R.id.cbAutoSync)).setChecked(autSync);

        findViewById(R.id.cbIsSyncable).setEnabled(true);
        findViewById(R.id.cbAutoSync).setEnabled(true);
        findViewById(R.id.status).setEnabled(true);
        findViewById(R.id.btnShowRemoteList).setEnabled(true);
        findViewById(R.id.btnSync).setEnabled(true);
        findViewById(R.id.btnConnect).setEnabled(false);

        refreshSyncStatus();
    }*/

    /**
     * Get an auth token for the account.
     * If not exist - add it and then return its auth token.
     * If one exist - return its auth token.
     * If more than one exists - show a picker and return the select account's auth token.
     * @param accountType
     * @param authTokenType
     */
    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
            new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    Bundle bnd = null;
                    try {
                        bnd = future.getResult();
                        authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                        if (authToken != null) {
                            String accountName = bnd.getString(AccountManager.KEY_ACCOUNT_NAME);
                            mConnectedAccount = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);
                            //initButtonsAfterConnect();
                        }
                        showMessage(((authToken != null) ? "SUCCESS!\ntoken: " + authToken : "FAIL"));
                        Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("Error occured "+e.getMessage());
                    }
                }
            }
        , null);
    }

    private void showMessage(final String msg) {
        if (msg == null || msg.trim().equals(""))
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Show all the accounts registered on the account manager. Request an auth token upon user select.
     * @param authTokenType
     */
    private void showAccountPicker(final String authTokenType, final boolean invalidate) {

        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(invalidate)
                        invalidateAuthToken(availableAccounts[which], authTokenType);
                    else
                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                }
            }).show();
        }
    }

    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    showMessage("Account was created");
                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }, null);
    }

    /**
     * Get the auth token for an existing account on the AccountManager
     * @param account
     * @param authTokenType
     */
    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    for (String key : bnd.keySet()) {
                        Log.d("udinic", "Bundle[" + key + "] = " + bnd.get(key));
                    }

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
                    Log.d("udinic", "GetToken Bundle is " + bnd);
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Invalidates the auth token for the account
     * @param account
     * @param authTokenType
     */
    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null,null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);
                    showMessage(account.name + " invalidated");
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }
}
