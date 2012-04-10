package com.terlici.dragndroplistapp.test;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.widget.ListAdapter;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropListView;

public class DragNDropAdapterTests extends AndroidTestCase {
	public void testInterfaces() {
		MoreAsserts.assertAssignableFrom(DragNDropListView.OnItemDragNDropListener.class, DragNDropAdapter.class);
		MoreAsserts.assertAssignableFrom(ListAdapter.class, DragNDropAdapter.class);
	}
}
