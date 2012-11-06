/*
 * Copyright 2012 Terlici Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.terlici.dragndroplistapp.test;

import android.content.Context;
import android.database.MatrixCursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropCursorAdapter;
import com.terlici.dragndroplist.DragNDropListView;

public class DragNDropCursorAdapterTests extends AndroidTestCase {
	MatrixCursor cursor;
	DragNDropCursorAdapter adapter;
	DragNDropListView parent;
	View view;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		cursor = new MatrixCursor(new String[] {"_id", "name"});
		
		for(int i = 0; i < 10; ++i) {			
			cursor.addRow(new Object[] {i, ("item" + i)});
		}
		
		adapter = new DragNDropCursorAdapter(getContext(),
											 android.R.layout.simple_list_item_1,
											 cursor,
											 new String[]{"name"},
											 new int[]{android.R.id.text1},
											 android.R.id.text1);
		
		parent = new DragNDropListView(getContext());
		
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(android.R.layout.simple_list_item_1, null);
	}
	
	public void testPreconditions() {
		assertNotNull(cursor);
		assertNotNull(adapter);
		
		for(int i = 0; i < cursor.getCount(); ++i) {
			MatrixCursor data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + i, data.getString(data.getColumnIndex("name")));
			assertEquals(i, data.getLong(data.getColumnIndex("_id")));
			
			assertEquals(adapter.getItemId(i), i);
		}
	}
	
	public void testType() {
		MoreAsserts.assertAssignableFrom(SimpleCursorAdapter.class, adapter);
		MoreAsserts.assertAssignableFrom(DragNDropAdapter.class, adapter);
	}
	
	public void testDragHandler() {
		assertEquals(android.R.id.text1, adapter.getDragHandler());
	}
	
	public void testMoveUp() {
		((TextView)view.findViewById(android.R.id.text1)).setText("item1");
		
		adapter.onItemDrag(parent, view, 1, 1L);
		adapter.onItemDrop(parent, view, 1, 5, 1);
		
		assertEquals(1, adapter.getItemId(5));
		MatrixCursor data = (MatrixCursor)adapter.getItem(5);
		assertEquals("item1", data.getString(data.getColumnIndex("name")));
		assertEquals(1, data.getLong(data.getColumnIndex("_id")));
		
		View v = adapter.getView(5, null, null);
		assertEquals("item1", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		v = adapter.getDropDownView(5, null, null);
		assertEquals("item1", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		
		for(int i = 1; i < 5; ++i) {
			data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + (i + 1), data.getString(data.getColumnIndex("name")));
			assertEquals(i + 1, data.getLong(data.getColumnIndex("_id")));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + (i + 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + (i + 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 0; i < 1; ++i) {
			data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + i, data.getString(data.getColumnIndex("name")));
			assertEquals(i, data.getLong(data.getColumnIndex("_id")));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 6; i < cursor.getCount(); ++i) {
			data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + i, data.getString(data.getColumnIndex("name")));
			assertEquals(i, data.getLong(data.getColumnIndex("_id")));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
	}
	
	public void testMoveDown() {
		((TextView)view.findViewById(android.R.id.text1)).setText("item5");
		
		adapter.onItemDrag(parent, view, 5, 1L);
		adapter.onItemDrop(parent, view, 5, 1, 1);
		
		assertEquals(5, adapter.getItemId(1));
		
		MatrixCursor data = (MatrixCursor)adapter.getItem(1);
		assertEquals("item5", data.getString(data.getColumnIndex("name")));
		assertEquals(5, data.getLong(data.getColumnIndex("_id")));
		
		View v = adapter.getView(1, null, null);
		assertEquals("item5", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		v = adapter.getDropDownView(1, null, null);
		assertEquals("item5", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		for(int i = 2; i <= 5; ++i) {
			data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + (i - 1), data.getString(data.getColumnIndex("name")));
			assertEquals(i - 1, data.getLong(data.getColumnIndex("_id")));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + (i - 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + (i - 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 0; i < 1; ++i) {
			data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + i, data.getString(data.getColumnIndex("name")));
			assertEquals(i, data.getLong(data.getColumnIndex("_id")));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 6; i < cursor.getCount(); ++i) {
			data = (MatrixCursor)adapter.getItem(i);
			assertEquals("item" + i, data.getString(data.getColumnIndex("name")));
			assertEquals(i, data.getLong(data.getColumnIndex("_id")));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
	}
}
