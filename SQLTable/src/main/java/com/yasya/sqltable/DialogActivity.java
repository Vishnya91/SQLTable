package com.yasya.sqltable;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DialogActivity extends Activity {
    private EditText editLast;
    private EditText editFirst;
    private EditText editAge;
    private EditText editStreet;
    private Button ok;
    private Button cancel;
    Intent intent;

    private int rowId;
    private DBAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dialog);
        this.setFinishOnTouchOutside(false);

        adapter = new DBAdapter(this);
        adapter.open();

        ok = (Button) findViewById(R.id.btnOK);
        cancel = (Button) findViewById(R.id.btnCANCEL);

        editLast = (EditText) findViewById(R.id.txt_last);
        editFirst = (EditText) findViewById(R.id.txt_first);
        editAge = (EditText) findViewById(R.id.txt_age);
        editStreet = (EditText) findViewById(R.id.txt_street);

        rowId = 0;
        Bundle extras = getIntent().getExtras();
        rowId = (bundle == null) ? 0 : (Integer) bundle
                .getSerializable(DBAdapter.KEY_ROWID);
        if (extras != null) {
            rowId = extras.getInt(DBAdapter.KEY_ROWID);
        }
        populateFields();

        intent = new Intent();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveState();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DBAdapter.KEY_ROWID, rowId);
    }

    private void saveState() {
        String last = editLast.getText().toString();
        String first = editFirst.getText().toString();
        String age = editAge.getText().toString();
        String street = editStreet.getText().toString();
        if (rowId == 0) {
            int id = adapter.createRow(last, first, age, street);

            if (id > 0) {
                rowId = id;
                intent.putExtra("id", rowId);
                intent.putExtra("last", last);
                intent.putExtra("first", first);
                intent.putExtra("age", age);
                intent.putExtra("street", street);
            }
        } else {
            adapter.updateRow(rowId, last, first, age, street);
            intent.putExtra("id", rowId);
            intent.putExtra("last", last);
            intent.putExtra("first", first);
            intent.putExtra("age", age);
            intent.putExtra("street", street);
        }
    }

    private void populateFields() {
        if (rowId != 0) {
            Cursor cursor = adapter.fetchRow(rowId);
            if (cursor != null && cursor.moveToFirst()) {
                String last = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_LAST));
                String first = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_FIRST));
                String age = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_AGE));
                String street = cursor.getString(cursor
                        .getColumnIndexOrThrow(DBAdapter.KEY_STREET));
                editLast.setText(last);
                editFirst.setText(first);
                editAge.setText(age);
                editStreet.setText(street);
            }
        }
    }
}
