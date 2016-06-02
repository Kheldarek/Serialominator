package com.psfs.pz.serialominator.listAdapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.psfs.pz.serialominator.APIhandlers.APIHandler;
import com.psfs.pz.serialominator.R;
import com.psfs.pz.serialominator.database.Episode;
import com.psfs.pz.serialominator.database.SeriesDB;
import com.psfs.pz.serialominator.database.TvSeries;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by psend on 12.05.2016.
 */
public class ToDoListAdapter extends BaseExpandableListAdapter
{
    Context context;
    public List<TvSeries> groups;
    public HashMap<TvSeries, List<Episode>> children;


    public ToDoListAdapter(Context context, List<TvSeries> series, HashMap<TvSeries, List<Episode>> episodes)
    {


        this.context = context;
        this.groups = series;
        this.children = episodes;

    }

    @Override
    public int getGroupCount()
    {
        return this.groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.children.get(this.groups.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.children.get(this.groups.get(groupPosition))
                .get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        View row = convertView;
        GroupHolder holder = null;
        if (row == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infalInflater.inflate(R.layout.todo_series_row, null);
            holder = new GroupHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitleYear);
            holder.counter = (TextView) row.findViewById(R.id.counter);
            row.setTag(holder);
        } else
        {
            holder = (GroupHolder) row.getTag();
        }

        TvSeries tvSeries = (TvSeries) getGroup(groupPosition);
        Picasso.with(this.context)
                .load(tvSeries.getImg())
                .into((holder.imgIcon));
        holder.txtTitle.setText(String.format("%s( %s )", tvSeries.getName(), tvSeries.getYear()));
        SeriesDB db = new SeriesDB(context);
        List<Episode> tmpEpList = db.getShowUnwatchedEpisodes(tvSeries.getName(), tvSeries.getYear());
        holder.counter.setText(String.format("%d unwatched", tmpEpList.size()));
        db.close();


        return row;

    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        View row = convertView;
        ChildrenHolder holder = null;
        parent.setMinimumHeight(90);

        if (row == null)
        {

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = infalInflater.inflate(R.layout.todo_episode_row, null);

            holder = new ChildrenHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.txtDate = (TextView) row.findViewById(R.id.txtDate);
            holder.watched = (ImageButton) row.findViewById(R.id.markWatched);
            holder.watched.setOnClickListener(BtnClickListener);
            row.setTag(holder);

        } else
        {
            holder = (ChildrenHolder) row.getTag();
        }

        Episode object = (Episode) getChild(groupPosition, childPosition);
        holder.txtTitle.setText(String.format("(%d-%d)%s", object.getSeason(), object.getEpisodeNumber(), object.getTitle()));
        Date tmpDt = new Date(object.getReleased());
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        holder.txtDate.setText(String.format("Aired:%s", dateformat.format(tmpDt)));
        row.setMinimumHeight(90);
        holder.watched.setTag(new Position(groupPosition, childPosition));

        return row;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }


    static class ChildrenHolder
    {
        TextView txtTitle;
        TextView txtDate;
        ImageButton watched;

    }

    static class GroupHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView counter;

    }

    static class Position
    {
        public int groupPosition;
        public int childPosition;

        public Position(int a, int b)
        {
            groupPosition = a;
            childPosition = b;
        }
    }


    private View.OnClickListener BtnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Position position = (Position) v.getTag();
            View parent = v.getRootView();
            Log.d("BTNLST", "pos " + position);
            ExpandableListView tmp = (ExpandableListView) parent.findViewById(R.id.ToDoExList);
            Episode toMark = (Episode) tmp.getExpandableListAdapter().getChild(position.groupPosition, position.childPosition);
            toMark.setWatched(1);
            Log.i("WTH", toMark.getWatched() + "");
            SeriesDB seriesDB = new SeriesDB(parent.getContext());
            seriesDB.updateEpisode(toMark);
            seriesDB.close();
            TvSeries key = (TvSeries) tmp.getExpandableListAdapter().getGroup(position.groupPosition);
            List<Episode> deleting = children.get(key);
            deleting.remove(toMark);
            children.remove(key);
            children.put(key, deleting);
            int index = tmp.getFirstVisiblePosition();
            View x = tmp.getChildAt(0);
            int top = (x == null) ? 0 : (x.getTop() - tmp.getPaddingTop());
            tmp.setAdapter(new ToDoListAdapter(parent.getContext(), groups, children));
            tmp.expandGroup(position.groupPosition);
            tmp.setSelectionFromTop(index, top);


        }
    };
}
