package com.yasya.sqltable;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    DBAdapter adapter;
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    protected Object mActionMode;
    private ArrayList<MyTableRow> rows = new ArrayList<MyTableRow>();
    private ArrayList<View> divider = new ArrayList<View>();
    TableLayout table;
    MyTableRow row;
    private static final int DIVIDER_SIZE = 1;
    View dividerRow2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setOrientation();

        adapter = new DBAdapter(this);
        adapter.open();
        adapter.deleteAll();

    }

    private void setOrientation() {
        int orientation = this.getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    private void addRow() {
        table = (TableLayout) findViewById(R.id.table);
        row = new MyTableRow(this);
        dividerRow2 = new View(this);
        dividerRow2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                DIVIDER_SIZE));
        dividerRow2.setBackgroundColor(Color.BLACK);

        table.addView(row, new TableLayout.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        table.addView(dividerRow2);

        Toast.makeText(this, "Table has " + table.getChildCount(),
                Toast.LENGTH_SHORT).show();

        row.setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (mActionMode != null) {
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });
        rows.add(row);
        divider.add(dividerRow2);
        Intent i = new Intent(this, DialogActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void editRow() {
        for (TableRow r : rows) {
            MyTableRow row = (MyTableRow) r;
            if (row.isSelected()) {
                int id = row.getID();
                Intent i = new Intent(this, DialogActivity.class);
                i.putExtra(DBAdapter.KEY_ROWID, id);
                startActivityForResult(i, ACTIVITY_EDIT);
            }
        }
    }

    private void deleteRow() {
        for (TableRow r : rows) {
            MyTableRow row = (MyTableRow) r;
            if (row.isSelected()) {
                int rowID = row.getID();
                adapter.deleteRow(rowID);
                table.removeView(row);
                // table.removeViewAt(row.getId());
                table.removeView(dividerRow2);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addRow();
                return true;
            case R.id.action_read:
                readFromDB();
                return true;
            case R.id.action_clear:
                adapter.deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteRow();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_edit:
                    editRow();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            int rowId = 0;
            int id = intent.getIntExtra("id", rowId);
            MyTableRow currentRow = rows.get(rows.size() - 1);

            String last = intent.getStringExtra("last");
            String first = intent.getStringExtra("first");
            String age = intent.getStringExtra("age");
            String street = intent.getStringExtra("street");

            currentRow.setID(String.valueOf(id));
            currentRow.setLast(last);
            currentRow.setFirst(first);
            currentRow.setAge(age);
            currentRow.setStreet(street);
        }
        if (resultCode == RESULT_CANCELED) {
            table.removeView(rows.get(rows.size() - 1));
            table.removeView(divider.get(divider.size() - 1));
        }
    }

    private void readFromDB() {
        clearTable();
        Cursor cursor = adapter.fetchAll();
        if (cursor.moveToFirst()) {
            do {
                table = (TableLayout) findViewById(R.id.table);
                row = new MyTableRow(this);
                dividerRow2 = new View(this);
                dividerRow2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        DIVIDER_SIZE));
                dividerRow2.setBackgroundColor(Color.BLACK);

                table.addView(row, new TableLayout.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                table.addView(dividerRow2);

                Toast.makeText(this, "Table has " + table.getChildCount(),
                        Toast.LENGTH_SHORT).show();

                row.setOnLongClickListener(new View.OnLongClickListener() {
                    // Called when the user long-clicks on someView
                    public boolean onLongClick(View view) {
                        if (mActionMode != null) {
                            return false;
                        }
                        // Start the CAB using the ActionMode.Callback defined above
                        mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
                        view.setSelected(true);
                        return true;
                    }
                });
                rows.add(row);
                String id = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_ROWID));
                String last = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_LAST));
                String first = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_FIRST));
                String age = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_AGE));
                String street = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_STREET));
                row.setID(id);
                row.setLast(last);
                row.setFirst(first);
                row.setAge(age);
                row.setStreet(street);
            } while (cursor.moveToNext());
        } else
            cursor.close();
    }

    private void clearTable() {
        if (rows.size() > 0) {
            for (int i = 0; i < rows.size(); i++)
                table.removeView(rows.get(i));
        }
        if (divider.size() > 0) {
            for (int i = 0; i < divider.size(); i++)
                table.removeView(divider.get(i));
        }
        // View child = table.getChildAt(i);
        // if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        rows.clear();
        divider.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.close();
        }
    }
}
