package com.terlici.dragndroplistapp.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.MoreAsserts;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.terlici.dragndroplist.DragNDropAdapter;
import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.DragNDropSimpleAdapter;

public class DragNDropSimpleAdapterTests extends AndroidTestCase {
	
	List<Map<String, Object>> items;
	DragNDropSimpleAdapter adapter;
	DragNDropListView parent;
	View view;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		items = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < 10; ++i) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("name", "item" + i);
			item.put("_id", i);
			
			items.add(item);
		}
		
		adapter = new DragNDropSimpleAdapter(getContext(),
											 items,
											 android.R.layout.simple_list_item_1,
											 new String[]{"name"},
											 new int[]{android.R.id.text1},
											 android.R.id.text1);
		
		parent = new DragNDropListView(getContext());
		
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(android.R.layout.simple_list_item_1, null);
	}
	
	public void testPreconditions() {
		assertNotNull(items);
		assertNotNull(adapter);
		
		for(int i = 0; i < items.size(); ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + i);
			assertEquals(data.get("_id"), i);
			
			assertEquals(adapter.getItemId(i), i);
		}
	}
	
	public void testType() {
		MoreAsserts.assertAssignableFrom(SimpleAdapter.class, adapter);
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
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)adapter.getItem(5);
		assertEquals(map.get("name"), "item1");
		assertEquals(map.get("_id"), 1);
		
		View v = adapter.getView(5, null, null);
		assertEquals("item1", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		v = adapter.getDropDownView(5, null, null);
		assertEquals("item1", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		
		for(int i = 1; i < 5; ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + (i + 1));
			assertEquals(data.get("_id"), (i + 1));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + (i + 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + (i + 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 0; i < 1; ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + i);
			assertEquals(data.get("_id"), i);
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 6; i < items.size(); ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + i);
			assertEquals(data.get("_id"), i);
			
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
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)adapter.getItem(1);
		assertEquals(map.get("name"), "item5");
		assertEquals(map.get("_id"), 5);
		
		View v = adapter.getView(1, null, null);
		assertEquals("item5", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		v = adapter.getDropDownView(1, null, null);
		assertEquals("item5", ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		
		for(int i = 2; i <= 5; ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + (i - 1));
			assertEquals(data.get("_id"), (i - 1));
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + (i - 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + (i - 1), ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 0; i < 1; ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + i);
			assertEquals(data.get("_id"), i);
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
		
		for(int i = 6; i < items.size(); ++i) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)adapter.getItem(i);
			assertEquals(data.get("name"), "item" + i);
			assertEquals(data.get("_id"), i);
			
			v = adapter.getView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
			
			v = adapter.getDropDownView(i, null, null);
			assertEquals("item" + i, ((TextView)v.findViewById(android.R.id.text1)).getText().toString());
		}
	}
}
