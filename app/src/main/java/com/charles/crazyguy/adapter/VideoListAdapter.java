package com.charles.crazyguy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.charles.crazyguy.dto.VideoItem;

import com.charles.crazyguy.R;

import java.util.List;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author feiyu
 * @date 2019-08-18
 */
public class VideoListAdapter extends BaseAdapter {
    private Context mContext;
    private List<VideoItem> mVideoItems;

    public VideoListAdapter(Context context) {
        mContext = context;
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        mVideoItems = videoItems;
    }

    public VideoItem getVideoItem(int position) {
        if(position >= 0 && position < mVideoItems.size()) {
            return mVideoItems.get(position);
        }

        return null;
    }

    @Override
    public int getCount() {
        return mVideoItems == null ? 0 : mVideoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View itemView;
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.video_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.thumnailView = itemView.findViewById(R.id.thumnail_view);
            viewHolder.titleView = itemView.findViewById(R.id.title_view);
            viewHolder.durationView = itemView.findViewById(R.id.duration_view);

            itemView.setTag(viewHolder);
        } else {
            itemView = convertView;

            viewHolder = (ViewHolder) itemView.getTag();
        }

        VideoItem videoItem = mVideoItems.get(position);
        viewHolder.titleView.setText(videoItem.title);
        viewHolder.durationView.setText("时长 : " + videoItem.formatDuration);
        return itemView;
    }

    private static class ViewHolder{
        ImageView thumnailView;
        TextView titleView;
        TextView durationView;
    }
}
