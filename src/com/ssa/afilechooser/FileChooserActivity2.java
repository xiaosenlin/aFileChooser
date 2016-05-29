/*
 * Copyright (C) 2013 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ssa.afilechooser;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.ipaulpro.afilechooser.R;

/**
 * Main Activity that handles the FileListFragments
 *
 * @version 2016-05-17
 * @author SunShan'ai 
 */
public class FileChooserActivity2 extends FragmentActivity implements OnBackStackChangedListener, FileListFragment2.Callbacks {

    public static final String PATH = "path";
    public static final String PATHS = "paths";
    public static final String EXTERNAL_BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static final boolean HAS_ACTIONBAR = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    private FragmentManager mFragmentManager;
    private BroadcastReceiver mStorageListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, R.string.storage_removed, Toast.LENGTH_LONG).show();
            finishWithResults(null);
        }
    };

    private String mPath;
    private Button mBtnLeft;
    private Button mBtnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        initActivityTitleUI();
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {
            mPath = EXTERNAL_BASE_PATH;
            addFragment();
        } else {
            mPath = savedInstanceState.getString(PATH);
        }

        setTitle(mPath);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterStorageListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerStorageListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(PATH, mPath);
    }

    @Override
    public void onBackStackChanged() {

        int count = mFragmentManager.getBackStackEntryCount();
        if (count > 0) {
            BackStackEntry fragment = mFragmentManager.getBackStackEntryAt(count - 1);
            mPath = fragment.getName();
        } else {
            mPath = EXTERNAL_BASE_PATH;
        }

        setTitle(mPath);
        if (HAS_ACTIONBAR)
            invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (HAS_ACTIONBAR) {
            boolean hasBackStack = mFragmentManager.getBackStackEntryCount() > 0;

            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(hasBackStack);
            actionBar.setHomeButtonEnabled(hasBackStack);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mFragmentManager.popBackStack();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add the initial Fragment with given path.
     */
    private void addFragment() {
        FileListFragment2 fragment = FileListFragment2.newInstance(mPath);
        mFragmentManager.beginTransaction().add(R.id.content, fragment).commit();
    }

    private void initActivityTitleUI() {
        mBtnLeft = (Button) findViewById(R.id.btn_title_left);
        mBtnRight = (Button) findViewById(R.id.btn_title_right);
    }

    /**
     * "Replace" the existing Fragment with a new one using given path. We're
     * really adding a Fragment to the back stack.
     *
     * @param file The file (directory) to display.
     */
    private void replaceFragment(File file) {
        mPath = file.getAbsolutePath();

        FileListFragment2 fragment = FileListFragment2.newInstance(mPath);
        mFragmentManager.beginTransaction().replace(R.id.content, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(mPath).commit();
    }

    /**
     * Finish this Activity with a result code and URI of the selected file.
     *
     * @param file The file selected.
     */
    private void finishWithResult(File file) {
        if (file != null) {
            Uri uri = Uri.fromFile(file);
            setResult(RESULT_OK, new Intent().setData(uri));
            finish();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * Finish this Activity with a result code and URI of the selected file.
     *
     * @param file The file selected.
     */
    private void finishWithResults(List<File> files) {
        Intent intent = new Intent();
        intent.putExtra(PATHS, (Serializable) files);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when the user selects a File
     *
     * @param file The file that was selected
     */
    @Override
    public void onFileSelected(File2 file) {
        if (file != null) {
            if (file.getFile().isDirectory()) {
                replaceFragment(file.getFile());
            } else {
                finishWithResult(file.getFile());
            }
        } else {
            Toast.makeText(FileChooserActivity2.this, R.string.error_selecting_file, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Register the external storage BroadcastReceiver.
     */
    private void registerStorageListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(mStorageListener, filter);
    }

    /**
     * Unregister the external storage BroadcastReceiver.
     */
    private void unregisterStorageListener() {
        unregisterReceiver(mStorageListener);
    }

    @Override
    public void onFileSelected(List<File2> files) {
        if (null != files && files.size() > 0) {
            ArrayList<File> fileLists = new ArrayList<File>();
            for (File2 file2 : files) {
                if (file2.isChecked()) {
                    fileLists.add(file2.getFile());
                }
            }
            finishWithResults(fileLists);
        }
    }

    public Button getBtnLeft() {
        return mBtnLeft != null ? mBtnLeft : (Button) findViewById(R.id.btn_title_left);
    }

    public Button getBtnRight() {
        return mBtnRight != null ? mBtnRight : (Button) findViewById(R.id.btn_title_right);
    }

    /**
     * convert uri to  file path
     */
    /* private String getFilePath(Uri uri){
         String[] proj={MediaStore.Files.FileColumns.DATA};
         Cursor actualfilecursor=getContentResolver().query(uri, proj, null, null, null);
         int actual_file_column_index= actualfilecursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
         actualfilecursor.moveToFirst();
         String filePath=actualfilecursor.getString(actual_file_column_index);
         return filePath;
     }*/
}
