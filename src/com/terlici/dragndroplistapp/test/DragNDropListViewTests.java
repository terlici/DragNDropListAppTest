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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.test.ActivityInstrumentationTestCase2;
import android.test.MoreAsserts;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.terlici.dragndroplist.DragNDropListView;
import com.terlici.dragndroplist.DragNDropSimpleAdapter;
import com.terlici.dragndroplistapp.DragNDropListAppActivity;

public class DragNDropListViewTests extends ActivityInstrumentationTestCase2<DragNDropListAppActivity> {
	
	public DragNDropListViewTests() {
		super(DragNDropListAppActivity.class);
	}
	
	public class TestListener implements DragNDropListView.OnItemDragNDropListener {
		
		public boolean onItemDragCalled = false;
		public boolean onItemDropCalled = false;
		public DragNDropListView parent = null;
		public View view = null;
		public int position = -1;
		public int startPosition = -1;
		public int endPosition = -1;
		public long id = -1;

		@Override
		public void onItemDrag(DragNDropListView parent, View view, int position, long id) {
			onItemDragCalled = true;
			
			this.parent = parent;
			this.view = view;
			this.position = position;
			this.id = id;
		}

		@Override
		public void onItemDrop(DragNDropListView parent, View view, int startPosition, int endPosition, long id) {
			onItemDropCalled = true;
			
			this.parent = parent;
			this.view = view;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.id = id;
		}
		
	}
	
	MotionEvent down, downin, move, up;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		down = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 72.0f, 20.0f, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0);
		downin = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 40, 40, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0);
		move = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_MOVE, 100, 140, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0);
		up = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_UP, 50, 100, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0);
	}
	
	public void testPreconditions() {
		assertNotNull(down);
		assertNotNull(downin);
		assertNotNull(move);
		assertNotNull(up);
	}
	
	public void testType() {
		MoreAsserts.assertAssignableFrom(ListView.class, DragNDropListView.class);
	}
	
	public void testInitialization() {
		DragNDropListView lists[] = new DragNDropListView[3];
		
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		lists[0] = new DragNDropListView(getActivity());
		
		View layout = inflater.inflate(com.terlici.dragndroplistapp.R.layout.testdragndroplist, null);
		lists[1] = (DragNDropListView)layout.findViewById(com.terlici.dragndroplistapp.R.id.list1);
		lists[2] = (DragNDropListView)layout.findViewById(com.terlici.dragndroplistapp.R.id.list2);
		
		assertNotNull(lists[0]);
		assertNotNull(lists[1]);
		assertNotNull(lists[2]);
	}
	
	public void testAdapters() {
		ArrayList<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < 10; ++i) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("name", "item" + i);
			item.put("_id", i);
			
			items.add(item);
		}
		
		DragNDropSimpleAdapter adapter = new DragNDropSimpleAdapter(getActivity(),
											 items,
											 com.terlici.dragndroplistapp.R.layout.testitem,
											 new String[]{"name"},
											 new int[]{com.terlici.dragndroplistapp.R.id.text},
											 com.terlici.dragndroplistapp.R.id.handler);
		
		DragNDropListView list = new DragNDropListView(getActivity());
		list.setDragNDropAdapter(adapter);
		
		assertSame(adapter, list.getAdapter());
	}
	
	public void testIsDrag() {
		DragNDropListView list = (DragNDropListView)getActivity().findViewById(com.terlici.dragndroplistapp.R.id.list1);
		
		assertTrue(list.isDrag(downin));
		assertFalse(list.isDrag(down));
	}
	
	public void testDrag() {
		final DragNDropListView list = (DragNDropListView)getActivity().findViewById(com.terlici.dragndroplistapp.R.id.list1);
		
		TestListener listener = new TestListener() {
			@Override
			public void onItemDrag(DragNDropListView parent, View view, int position, long id) {
				super.onItemDrag(parent, view, position, id);
				
				// We use this to test whether the callback was called before
				// or after the floating item is drawn. It should be before, so
				// that we can make changes to it if we want to. For example
				// to show that it is floating by using a special background.
				view.setBackgroundColor(Color.RED);
			}
		};
		
		list.setOnItemDragNDropListener(listener);
		
		int x = (int)downin.getX();
		int y = (int)downin.getY();
		int startposition = list.pointToPosition(x, y);
		int childposition = startposition - list.getFirstVisiblePosition();
		View item = list.getChildAt(childposition);
		int offset = y - item.getTop();
		offset -= ((int)downin.getRawY()) - y;
		
		item.setDrawingCacheEnabled(true);
        item.setDrawingCacheEnabled(false);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				list.onTouchEvent(downin);				
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		View sameItem = list.getChildAt(childposition);
		
		assertTrue(list.isDragging());
		assertNotNull(list.getDragView());
		
		WindowManager.LayoutParams params = (WindowManager.LayoutParams)list.getDragView().getLayoutParams();
		assertEquals(Gravity.TOP, params.gravity);
		assertEquals(0, params.x);
		assertEquals(y - offset,  params.y);
		assertEquals(WindowManager.LayoutParams.WRAP_CONTENT, params.height);
		assertEquals(WindowManager.LayoutParams.WRAP_CONTENT, params.width);
		assertEquals(PixelFormat.TRANSLUCENT, params.format);
		assertEquals(0, params.windowAnimations);
		int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		assertEquals(flags, params.flags & flags);
		
		assertNotNull(list.getDragView().getWindowToken());
		
		list.getDragView().setDrawingCacheEnabled(true);
		
		list.getDragView().setDrawingCacheEnabled(false);
		
		assertNotNull(item.getDrawingCache());
		assertEquals(sameItem, item);
		assertEquals(View.INVISIBLE, sameItem.getVisibility());
		assertEquals(View.INVISIBLE, item.getVisibility());
		
		
		assertTrue(listener.onItemDragCalled);
		assertSame(list, listener.parent);
		assertSame(item, listener.view);
		assertEquals(startposition, listener.position);
		assertEquals((long)startposition, listener.id);
	}
	
	public void testMove() {
		final DragNDropListView list = (DragNDropListView)getActivity().findViewById(com.terlici.dragndroplistapp.R.id.list1);
		
		int x = (int)downin.getX();
		int y = (int)downin.getY();
		int itemposition = list.pointToPosition(x, y) - list.getFirstVisiblePosition();
		View item = list.getChildAt(itemposition);
		int offset = y - item.getTop();
		offset -= ((int)downin.getRawY()) - y;
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				list.onTouchEvent(downin);
				list.onTouchEvent(move);
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		assertTrue(list.isDragging());
		assertNotNull(list.getDragView());
		
		WindowManager.LayoutParams params = (WindowManager.LayoutParams)list.getDragView().getLayoutParams();

		assertEquals(0, params.x);
		assertEquals((int)move.getY() - offset,  params.y);
	}
	
	public void testDrop() {
		final DragNDropListView list = (DragNDropListView)getActivity().findViewById(com.terlici.dragndroplistapp.R.id.list1);
		
		TestListener listener = new TestListener();
		list.setOnItemDragNDropListener(listener);
		
		int x = (int)downin.getX();
		int y = (int)downin.getY();
		int startposition = list.pointToPosition(x, y);
		int endposition = list.pointToPosition((int)up.getX(), (int)up.getY());
		int childposition = startposition - list.getFirstVisiblePosition();
		View item = list.getChildAt(childposition);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				list.onTouchEvent(downin);
				list.onTouchEvent(move);
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		View dragview = list.getDragView(); 
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				list.onTouchEvent(up);
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		
		assertFalse(list.isDragging());
		assertNull(list.getDragView());
		assertNull(dragview.getWindowToken());
		assertEquals(View.GONE, dragview.getVisibility());
		
		assertEquals(View.VISIBLE, item.getVisibility());
		assertNull(item.getDrawingCache());
		
		// Test callback
		assertTrue(listener.onItemDropCalled);
		assertSame(list, listener.parent);
		assertSame(item, listener.view);
		assertEquals(startposition, listener.startPosition);
		assertEquals(endposition, listener.endPosition);
		assertEquals((long)startposition, listener.id);
		
		// Positioning
		assertEquals((long)startposition, list.getAdapter().getItemId(endposition));
	}
	
	
	public void testScrolledList() {
		final DragNDropListView list = (DragNDropListView)getActivity().findViewById(com.terlici.dragndroplistapp.R.id.list1);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				list.setSelection(list.getCount() - 1 - list.getFooterViewsCount());
			}
		});
		
		getInstrumentation().waitForIdleSync();
		

		int position = list.getLastVisiblePosition() - 1;
		long id = list.getAdapter().getItemId(position);
		View lastChild = list.getChildAt(list.getChildCount() - 1 - list.getFooterViewsCount());
		
		float ystart = (float)(lastChild.getTop() + 40);
		float yend = (float)(lastChild.getTop() - 40);
		
		final MotionEvent start = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, 10.0f, ystart, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0);
		final MotionEvent end = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_UP, 10.0f, yend, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0);
		
		TestListener listener = new TestListener();
		
		list.setOnItemDragNDropListener(listener);
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				list.onTouchEvent(start);
				list.onTouchEvent(end);
			}
		});
		
		getInstrumentation().waitForIdleSync();
		
		assertTrue(listener.onItemDragCalled);
		assertTrue(listener.onItemDropCalled);
		assertEquals(position, listener.startPosition);
		assertEquals(position, listener.position);
		assertEquals(position - 1, listener.endPosition);
		assertEquals(id, listener.id);
	}
}
