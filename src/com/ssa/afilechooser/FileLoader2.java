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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ssa.afilechooser.utils.FileUtils2;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Loader that returns a list of Files in a given file path.
 * 
 * @version 2016-05-17
 * @author SunShan'ai
 */
public class FileLoader2 extends AsyncTaskLoader<List<File2>> {

    private static final int FILE_OBSERVER_MASK = FileObserver.CREATE | FileObserver.DELETE | FileObserver.DELETE_SELF | FileObserver.MOVED_FROM | FileObserver.MOVED_TO
            | FileObserver.MODIFY | FileObserver.MOVE_SELF;

    private FileObserver mFileObserver;

    private List<File2> mData;
    private String mPath;

    public FileLoader2(Context context, String path) {
        super(context);
        this.mPath = path;
    }

    @Override
    public List<File2> loadInBackground() {

        ArrayList<File2> list = new ArrayList<File2>();

        // Current directory File instance
        final File pathDir = new File(mPath);

        // List file in this directory with the directory filter
        final File[] dirs = pathDir.listFiles(FileUtils2.sDirFilter);
        if (dirs != null) {
            // Sort the folders alphabetically
            Arrays.sort(dirs, FileUtils2.sComparator);
            // Add each folder to the File list for the list adapter
            for (File dir : dirs) {
                list.add(adapt2File2(dir));
            }
        }

        // List file in this directory with the file filter
        final File[] files = pathDir.listFiles(FileUtils2.mFileFileterBySuffixs);
        if (files != null) {
            // Sort the files alphabetically
            Arrays.sort(files, FileUtils2.sComparator);
            // Add each file to the File list for the list adapter
            for (File file : files)
                list.add(adapt2File2(file));
        }

        return list;
    }

    @Override
    public void deliverResult(List<File2> data) {
        if (isReset()) {
            onReleaseResources(data);
            return;
        }

        List<File2> oldData = mData;
        mData = data;

        if (isStarted())
            super.deliverResult(data);

        if (oldData != null && oldData != data)
            onReleaseResources(oldData);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null)
            deliverResult(mData);

        if (mFileObserver == null) {
            mFileObserver = new FileObserver(mPath, FILE_OBSERVER_MASK) {
                @Override
                public void onEvent(int event, String path) {
                    onContentChanged();
                }
            };
        }
        mFileObserver.startWatching();

        if (takeContentChanged() || mData == null)
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(List<File2> data) {
        super.onCanceled(data);

        onReleaseResources(data);
    }

    protected void onReleaseResources(List<File2> data) {

        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
    }

    private File2 adapt2File2(File file) {
        File2 file2 = new File2();
        file2.setFile(file);
        file2.setChecked(false);

        return file2;
    }
}