/*
 * Copyright (C) 2012 Paul Burke
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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ipaulpro.afilechooser.R;

/**
 * List adapter for Files.
 * 
 * @version 2016-5-17
 * @author SunShan'ai
 */
public class FileListAdapter2 extends BaseAdapter {

    private final static int ICON_FOLDER = R.drawable.ic_folder_blue;
    private final static int ICON_FILE = R.drawable.ic_file_blue;

    private final LayoutInflater mInflater;

    private List<File2> mData = new ArrayList<File2>();

    public FileListAdapter2(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void add(File2 file) {
        mData.add(file);
        notifyDataSetChanged();
    }

    public void remove(File2 file) {
        mData.remove(file);
        notifyDataSetChanged();
    }

    public void insert(File2 file, int index) {
        mData.add(index, file);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public File2 getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public List<File2> getListItems() {
        return mData;
    }

    /**
     * Set the list items without notifying on the clear. This prevents loss of
     * scroll position.
     *
     * @param data
     */
    public void setListItems(List<File2> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.file_check, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Get the file at the current position
        final File2 file2 = getItem(position);
        viewHolder.tvFileInfo.setText(file2.getFile().getName());

        // If the item is not a directory, use the file icon
        int icon = file2.getFile().isDirectory() ? ICON_FOLDER : ICON_FILE;
        int visibleStatus = file2.getFile().isDirectory() ? View.GONE : View.VISIBLE;
        viewHolder.cbCheckInfo.setVisibility(visibleStatus);
        viewHolder.tvFileInfo.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        viewHolder.cbCheckInfo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                file2.setChecked(isChecked);
            }
        });

        if (file2.isChecked()) {
            viewHolder.cbCheckInfo.setChecked(true);
        } else {
            viewHolder.cbCheckInfo.setChecked(false);
        }

        return convertView;
    }

    static class ViewHolder {
        private TextView tvFileInfo;
        private CheckBox cbCheckInfo;

        public ViewHolder(View convertView) {
            tvFileInfo = (TextView) convertView.findViewById(R.id.tv_fileinfo);
            cbCheckInfo = (CheckBox) convertView.findViewById(R.id.cb_check);
        }
    }
}