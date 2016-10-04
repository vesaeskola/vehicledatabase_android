package fi.vesaeskola.vehicledatabase;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;

import database.DBEngine;
import database.VehicleContract;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DBEngine mDatabaseEngine;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());

        mDatabaseEngine = new DBEngine(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_add_vehicle:
                Log.d(TAG, "Add new Vechile");
                final EditText vehicleMakeText = new EditText(this);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add new Vehicle")
                        .setMessage("Make of your vehicle ?")
                        .setView(vehicleMakeText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String vehicleMake = String.valueOf(vehicleMakeText.getText());
                                Log.d(TAG, "Vehicle to add: " + vehicleMake);

                                // Open or create database file
                                SQLiteDatabase db = mDatabaseEngine.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(VehicleContract.VehicleEntry.COL_VINCODE, "VinCode");
                                values.put(VehicleContract.VehicleEntry.COL_MAKE, vehicleMake);
                                values.put(VehicleContract.VehicleEntry.COL_MODEL, "Model");
                                values.put(VehicleContract.VehicleEntry.COL_YEAR, 1997);
                                values.put(VehicleContract.VehicleEntry.COL_REGPLATE, "BIM-450");
                                values.put(VehicleContract.VehicleEntry.COL_DESCRIPTION, "Hauska Vito bussi");
                                values.put(VehicleContract.VehicleEntry.COL_FUEL_UNIT_ID, 1);
                                values.put(VehicleContract.VehicleEntry.COL_ODOMETER_UNIT_ID, 1);
                                values.put(VehicleContract.VehicleEntry.COL_IMAGEPATH, "null");

                                db.insertWithOnConflict(VehicleContract.VehicleEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();



                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
