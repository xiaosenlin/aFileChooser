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

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.ipaulpro.afilechooser.R;

/**
 * Fragment that displays a list of Files in a given path.
 * 
 * @version 2016-05-17
 * @author SunShan'ai
 */
public class FileListFragment2 extends ListFragment implements LoaderManager.LoaderCallbacks<List<File2>> {

    /**
     * Interface to listen for events.
     */
    public interface Callbacks {
        /**
         * Called when a file is selected from the list.
         *
         * @param file The file selected
         */
        public void onFileSelected(File2 file);

        public void onFileSelected(List<File2> files);
    }

    private static final int LOADER_ID = 0;

    private FileChooserActivity2 mActivity;
    private FileListAdapter2 mAdapter;
    private String mPath;
    private boolean mListShown;
    private View mProgressContainer;
    private View mListContainer;
    private Callbacks mListener;

    /**
     * Create a new instance with the given file path.
     *
     * @param path The absolute path of the file (directory) to display.
     * @return A new Fragment with the given file path.
     */
    public static FileListFragment2 newInstance(String path) {
        FileListFragment2 fragment = new FileListFragment2();
        Bundle args = new Bundle();
        args.putString(FileChooserActivity2.PATH, path);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FileListFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof FileChooserActivity2) {
            mActivity = (FileChooserActivity2) getActivity();
        }
        mAdapter = new FileListAdapter2(getActivity());
        mPath = getArguments() != null ? getArguments().getString(FileChooserActivity2.PATH) : Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_container, container, false);
        mListContainer = contentView.findViewById(R.id.listContainer);
        mProgressContainer = contentView.findViewById(R.id.progressContainer);
        mListShown = true;

        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setEmptyText(getString(R.string.empty_directory));
        setListAdapter(mAdapter);
        setListShown(false);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        setBaseActivityListener();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        FileListAdapter2 adapter = (FileListAdapter2) l.getAdapter();
        if (adapter != null) {
            File2 file = adapter.getItem(position);
            mPath = file.getFile().getAbsolutePath();
            mListener.onFileSelected(file);
        }
    }

    @Override
    public Loader<List<File2>> onCreateLoader(int id, Bundle args) {
        return new FileLoader2(getActivity(), mPath);
    }

    @Override
    public void onLoadFinished(Loader<List<File2>> loader, List<File2> data) {
        mAdapter.setListItems(data);

        if (isResumed())
            setListShown(true);
        else
            setListShownNoAnimation(true);
    }

    @Override
    public void onLoaderReset(Loader<List<File2>> loader) {
        mAdapter.clear();
    }

    @Override
    public void setEmptyText(CharSequence text) {
        TextView emptyView = (TextView) getListView().getEmptyView();
        emptyView.setVisibility(View.GONE);
        emptyView.setText(text);
    }

    public void setListShown(boolean shown, boolean animate) {
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.INVISIBLE);
        }
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    private void setBaseActivityListener() {
        if (null == mActivity) {
            return;
        }
        mActivity.getBtnLeft().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mActivity.getBtnRight().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.onFileSelected(mAdapter.getListItems());
            }
        });
    }
}
